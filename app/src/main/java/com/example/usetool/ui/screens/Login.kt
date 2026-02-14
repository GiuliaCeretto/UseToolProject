package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.LoginResult
import com.example.usetool.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Osserviamo lo stato del login
    val loginState by userViewModel.loginState.collectAsState()

    // Navigazione automatica al successo
    LaunchedEffect(loginState) {
        if (loginState is LoginResult.Success) {
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginState !is LoginResult.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginState !is LoginResult.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Errori dinamici dal LoginState
                if (loginState is LoginResult.Error) {
                    Text(
                        text = (loginState as LoginResult.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { userViewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginState !is LoginResult.Loading
                ) {
                    if (loginState is LoginResult.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Accedi")
                    }
                }
            }
        }
    }
}