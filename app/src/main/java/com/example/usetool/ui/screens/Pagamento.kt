package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.data.service.toPurchaseEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.PI
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagamentoScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    lockerId: Int
) {
    // 🔹 Tutti gli articoli nel carrello aggiornati
    val allCartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()

    // 🔹 Calcolo dinamico dei PurchaseEntity per questo locker
    val purchaseList = allCartItems
        .filter { it.lockerId.toIntOrNull() == lockerId }
        .map { it.toPurchaseEntity() }

    val isProcessing by cartViewModel.isProcessing.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔹 Lista dinamica di articoli filtrati dal locker
    val itemsToPay = allCartItems.filter { it.lockerId.toIntOrNull() == lockerId }

    // 🔹 Totale dinamico
    val partialTotal = itemsToPay.sumOf { it.price * it.quantity }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }

    // Gestione messaggi di ritorno dal ViewModel
    LaunchedEffect(Unit) {
        cartViewModel.errorMessage.collectLatest { message ->
            if (message.contains("successo", ignoreCase = true)) {
                showSuccessDialog = true
            } else {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PAGAMENTO LOCKER #$lockerId", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // 🔹 Totale locker
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Totale per questo locker", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(
                        text = "€ ${"%.2f".format(partialTotal)}",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Articoli del locker
            Text("Articoli pronti al ritiro", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            if (itemsToPay.isEmpty()) {
                Text(
                    "Nessun articolo trovato per questo locker.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                itemsToPay.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.toolName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Quantità: ${item.quantity}", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text(
                            "€ ${"%.2f".format(item.price * item.quantity)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BluePrimary
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // 🔹 Metodo pagamento
            Text("Metodo di Pagamento", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16) cardNumber = it },
                label = { Text("Numero Carta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CreditCardTransformation(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it },
                label = { Text("Titolare Carta") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { if (it.length <= 4) expiryDate = it },
                    label = { Text("MM/AA") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 Bottone conferma
            Button(
                onClick = {
                    cartViewModel.performCheckout(
                        lockerId = lockerId,
                        onSuccess = { rentalIds ->
                            // Avvia i noleggi
                            rentalIds.forEach { id -> userViewModel.startRental(id) }

                            // Aggiorna il carrello
                            cartViewModel.refreshCart()

                            // Naviga a RitiroScreen
                            navController.navigate(NavRoutes.Ritiro.createRoute(lockerId))
                        }
                    )
                },
                enabled = cardNumber.length >= 16 &&
                        cvv.length >= 3 &&
                        cardHolder.isNotBlank() &&
                        !isProcessing &&
                        itemsToPay.isNotEmpty()
            ) {
                Text("CONFERMA E PAGA € ${"%.2f".format(partialTotal)}")
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("VAI AI MIEI ORDINI", color = Color.White)
                    }
                },
                title = { Text("Pagamento Completato", fontWeight = FontWeight.Bold) },
                text = { Text("Il locker #$lockerId è stato sbloccato correttamente. Puoi procedere al ritiro. Gli altri articoli restano nel tuo carrello.") },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}

// 🔹 Trasformazione per carta di credito
class CreditCardTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }

        val creditCardOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }
        return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
    }
}
