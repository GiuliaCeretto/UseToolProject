package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.usetool.R
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.LoginResult
import com.example.usetool.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showWelcomeDialog by remember { mutableStateOf(false) }

    val loginState by userViewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Osserva lo stato di login: attiva il dialogo ma NON resetta subito lo stato
    LaunchedEffect(loginState) {
        if (loginState is LoginResult.SuccessLogin) {
            showWelcomeDialog = true
        }
    }

    // Gestione messaggi di errore tramite SharedFlow
    LaunchedEffect(userViewModel.errorMessage) {
        userViewModel.errorMessage.collectLatest { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LightGrayBackground
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.usetoollogo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.65f).padding(bottom = 40.dp)
            )

            Text("Bentornato!", color = BluePrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            val yellowTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = YellowPrimary,
                unfocusedBorderColor = GreyLight,
                cursorColor = YellowPrimary,
                focusedLabelColor = YellowPrimary
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("es. mario@email.it") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = yellowTextFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = yellowTextFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { userViewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                enabled = loginState !is LoginResult.Loading
            ) {
                if (loginState is LoginResult.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("ACCEDI", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            TextButton(onClick = { navController.navigate(NavRoutes.Register.route) }) {
                Text("Non hai un account? Registrati", color = GreyMedium, style = MaterialTheme.typography.bodySmall)
            }
        }

        if (showWelcomeDialog) {
            AlertDialog(
                onDismissRequest = { }, // Obbliga l'interazione con il tasto ENTRA
                title = { Text("Benvenuto!", fontWeight = FontWeight.Bold) },
                text = { Text("Accesso eseguito con successo.") },
                confirmButton = {
                    Button(onClick = {
                        showWelcomeDialog = false
                        userViewModel.resetState() // Resetta lo stato PRIMA della navigazione
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)) {
                        Text("ENTRA", color = Color.White)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}