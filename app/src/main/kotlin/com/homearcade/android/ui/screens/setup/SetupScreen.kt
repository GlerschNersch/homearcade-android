package com.homearcade.android.ui.screens.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SetupScreen(
    onComplete: () -> Unit,
    vm: SetupViewModel = hiltViewModel(),
) {
    val saving by vm.saving.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("HomeArcade", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Connect to your HomeArcade server to get started.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = vm.serverUrl,
            onValueChange = { vm.serverUrl = it },
            label = { Text("HomeArcade Ingress URL") },
            placeholder = { Text("https://your-ha.duckdns.org:8123/api/hassio_ingress/…") },
            supportingText = {
                Text(
                    "In Home Assistant: Settings → Add-ons → HomeArcade → " +
                    "copy the address from the \"Open Web UI\" button URL.",
                    style = MaterialTheme.typography.labelSmall,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = vm.haToken,
            onValueChange = { vm.haToken = it },
            label = { Text("HA Long-Lived Access Token") },
            supportingText = {
                Text(
                    "Profile → Long-Lived Access Tokens → Create Token",
                    style = MaterialTheme.typography.labelSmall,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { vm.save(onComplete) },
            modifier = Modifier.fillMaxWidth(),
            enabled = vm.serverUrl.isNotBlank() && vm.haToken.isNotBlank() && !saving,
        ) {
            if (saving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("Connect")
        }
    }
}
