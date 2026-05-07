package com.homearcade.android.data.api

import android.util.Log
import com.homearcade.android.data.local.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * OkHttp interceptor that handles Home Assistant Ingress authentication.
 *
 * HA Ingress requires a session cookie (`ingress_token`) rather than a plain
 * Bearer token. This interceptor:
 *   1. Exchanges the user's Long-Lived Access Token (LLAT) for an ingress
 *      session token via POST /api/ingress/session on the HA instance.
 *   2. Caches that session token for up to 60 minutes.
 *   3. Attaches it as a Cookie header on every request to the ingress URL.
 *   4. On 401, refreshes the session token once and retries.
 *
 * If the server URL does NOT contain "hassio_ingress" (e.g. direct port
 * access), this interceptor is a no-op and just passes the Bearer token.
 */
class IngressAuthInterceptor(
    private val prefs: AppPreferences,
) : Interceptor {

    @Volatile private var ingressSessionToken: String? = null
    @Volatile private var sessionFetchedAt: Long = 0L
    private val sessionTtlMs = TimeUnit.MINUTES.toMillis(50) // HA sessions last 60 min

    /** Bare OkHttpClient used only for the /api/ingress/session exchange. */
    private val sessionClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val haToken = runBlocking { prefs.haToken.first() }
        val serverUrl = runBlocking { prefs.serverUrl.first() }

        // If not using HA ingress, just add Bearer token and pass through
        if (!serverUrl.contains("hassio_ingress")) {
            val req = if (haToken.isNotBlank()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $haToken")
                    .build()
            } else chain.request()
            return chain.proceed(req)
        }

        // Derive HA base URL from the ingress URL
        // e.g. "https://ha.example.com:8123/api/hassio_ingress/TOKEN" → "https://ha.example.com:8123"
        val haBaseUrl = serverUrl.substringBefore("/api/hassio_ingress")

        // Get (or refresh) ingress session token
        var sessionToken = getCachedSession()
        if (sessionToken == null) {
            sessionToken = fetchIngressSession(haBaseUrl, haToken)
        }

        val request = buildRequest(chain.request(), haToken, sessionToken)
        var response = chain.proceed(request)

        // On 401 try once more with a fresh session token
        if (response.code == 401 && sessionToken != null) {
            response.close()
            val fresh = fetchIngressSession(haBaseUrl, haToken)
            response = chain.proceed(buildRequest(chain.request(), haToken, fresh))
        }

        return response
    }

    private fun buildRequest(original: Request, haToken: String, sessionToken: String?): Request {
        val builder = original.newBuilder()
        if (haToken.isNotBlank()) {
            builder.header("Authorization", "Bearer $haToken")
        }
        if (sessionToken != null) {
            builder.header("Cookie", "ingress_token=$sessionToken")
        }
        return builder.build()
    }

    private fun getCachedSession(): String? {
        val token = ingressSessionToken ?: return null
        if (System.currentTimeMillis() - sessionFetchedAt > sessionTtlMs) return null
        return token
    }

    /**
     * POST /api/ingress/session — exchanges a LLAT for an ingress session token.
     * Returns null on any failure (caller will fall back to Bearer-only auth).
     */
    private fun fetchIngressSession(haBaseUrl: String, haToken: String): String? {
        if (haToken.isBlank()) return null
        return try {
            val request = Request.Builder()
                .url("$haBaseUrl/api/ingress/session")
                .post(okhttp3.RequestBody.create(null, ByteArray(0)))
                .header("Authorization", "Bearer $haToken")
                .build()

            val response = sessionClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("IngressAuth", "Session fetch failed: HTTP ${response.code}")
                return null
            }
            val body = response.body?.string() ?: return null
            val token = JSONObject(body).optString("session", null)
            if (token != null) {
                ingressSessionToken = token
                sessionFetchedAt = System.currentTimeMillis()
                Log.d("IngressAuth", "Ingress session obtained")
            }
            token
        } catch (e: Exception) {
            Log.e("IngressAuth", "Session fetch error: ${e.message}")
            null
        }
    }
}
