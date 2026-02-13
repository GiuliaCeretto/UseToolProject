package com.example.usetool.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // USERNAME
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ERRORE
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // BUTTON LOGIN
                Button(
                    onClick = {

                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Compila tutti i campi"
                            return@Button
                        }

                        // LOGIN FITTIZIO
                        userViewModel.loginFake(username)

                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Accedi")
                }
            }
        }
    }
}
