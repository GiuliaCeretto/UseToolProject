package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.LoginResult
import com.example.usetool.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var indirizzo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }

    val loginState by userViewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Feedback visivo per gli errori
    val isError = loginState is LoginResult.Error

    // Funzione di validazione locale
    fun validateInputs(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$".toRegex()

        return when {
            nome.isBlank() || cognome.isBlank() || indirizzo.isBlank() -> {
                userViewModel.triggerError("Tutti i campi sono obbligatori")
                false
            }
            !email.matches(emailRegex) -> {
                userViewModel.triggerError("Formato email non valido (es. nome@esempio.it)")
                false
            }
            telefono.length < 8 -> {
                userViewModel.triggerError("Inserisci un numero di telefono valido")
                false
            }
            password.length < 6 -> {
                userViewModel.triggerError("La password deve avere almeno 6 caratteri")
                false
            }
            password != confirmPassword -> {
                userViewModel.triggerError("Le password non coincidono")
                false
            }
            else -> true
        }
    }

    // Gestione Successo
    LaunchedEffect(loginState) {
        if (loginState is LoginResult.SuccessRegister) {
            showSuccessDialog = true
            // Reset campi
            nome = ""; cognome = ""; telefono = ""; indirizzo = ""
            email = ""; password = ""; confirmPassword = ""
        }
    }

    // Gestione Errori Snackbar
    LaunchedEffect(userViewModel.errorMessage) {
        userViewModel.errorMessage.collectLatest { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LightGrayBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.usetoollogo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            Text(
                text = "Crea un Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BluePrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Configurazione colori con supporto Errore (Bordo Rosso)
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = YellowPrimary,
                unfocusedBorderColor = GreyLight,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                cursorColor = YellowPrimary,
                focusedLabelColor = YellowPrimary
            )

            // Helper per pulire l'errore mentre l'utente scrive
            val onValueChangeWrapper: (String, (String) -> Unit) -> Unit = { newValue, setter ->
                setter(newValue)
                if (isError) userViewModel.resetState()
            }

            // --- CAMPI DI INPUT ---
            val fields = listOf(
                Triple("Nome", nome) { v: String -> nome = v },
                Triple("Cognome", cognome) { v: String -> cognome = v },
                Triple("Telefono", telefono) { v: String -> telefono = v },
                Triple("Indirizzo", indirizzo) { v: String -> indirizzo = v },
                Triple("Email", email) { v: String -> email = v }
            )

            fields.forEach { (label, value, setter) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { onValueChangeWrapper(it, setter) },
                    label = { Text(label) },
                    isError = isError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { onValueChangeWrapper(it) { v -> password = v } },
                label = { Text("Password") },
                isError = isError,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { onValueChangeWrapper(it) { v -> confirmPassword = v } },
                label = { Text("Conferma Password") },
                isError = isError,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (validateInputs()) {
                        userViewModel.register(nome, cognome, email, telefono, indirizzo, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                enabled = loginState !is LoginResult.Loading
            ) {
                if (loginState is LoginResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("REGISTRATI", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Hai già un account? Accedi", color = GreyMedium, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- POP-UP DI SUCCESSO ---
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Successo", fontWeight = FontWeight.Bold) },
                text = { Text("Registrazione completata con successo! Ora puoi accedere.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            userViewModel.resetState()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("ACCEDI ORA", color = Color.White)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}