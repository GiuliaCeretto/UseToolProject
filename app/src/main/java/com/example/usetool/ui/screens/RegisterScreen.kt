package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.usetool.R
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.LoginResult
import com.example.usetool.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    // Stati per i campi di input
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val loginState by userViewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Gestione messaggi di errore
    LaunchedEffect(userViewModel.errorMessage) {
        userViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Effetto per navigazione post-registrazione (se il ViewModel usa lo stesso stato del login)
    LaunchedEffect(loginState) {
        if (loginState is LoginResult.Success) {
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Permette lo scroll se la tastiera copre i campi
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo UseTool
            Image(
                painter = painterResource(id = R.drawable.usetoollogo),
                contentDescription = "Logo UseTool",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 24.dp, top = 16.dp)
            )

            Text(
                text = "Crea un Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E) // Blu scuro coerente
            )

            Text(
                text = "Unisciti alla community di UseTool",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo Nome
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome Completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = loginState !is LoginResult.Loading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = loginState !is LoginResult.Loading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = loginState !is LoginResult.Loading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Conferma Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Conferma Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = loginState !is LoginResult.Loading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsante Registrati
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        // Chiama la funzione di registrazione nel tuo ViewModel
                        // userViewModel.register(nome, email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF283593) // Blu primario
                ),
                enabled = loginState !is LoginResult.Loading
            ) {
                if (loginState is LoginResult.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("REGISTRATI", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Torna al Login
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Hai già un account? Accedi",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}