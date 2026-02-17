package com.example.usetool.ui.screens



import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavController

import com.example.usetool.navigation.NavRoutes

import com.example.usetool.ui.component.CartItemCard

import com.example.usetool.ui.theme.BluePrimary

import com.example.usetool.ui.viewmodel.CartViewModel



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun CarrelloScreen(

    navController: NavController,

    cartViewModel: CartViewModel

) {

    val cartHeader by cartViewModel.cartHeader.collectAsStateWithLifecycle()

    val items by cartViewModel.cartItems.collectAsStateWithLifecycle()

    val isProcessing by cartViewModel.isProcessing.collectAsStateWithLifecycle()



    // 🔥 Calcolo del totale locale per sicurezza (evita il "parte da trenta" se il DB è sporco)

    val totaleEffettivo = remember(items) {

        items.sumOf { it.price * it.quantity }

    }



    val uniqueLockerIds = remember(items) {

        items.mapNotNull { it.lockerId }.distinct()

    }



    LaunchedEffect(Unit) {

        cartViewModel.refreshCart()

    }



    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = { Text("IL TUO CARRELLO", fontWeight = FontWeight.Bold) },

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)

            )

        }

    ) { padding ->

        Box(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding)

                .background(Color(0xFFF8F9FA))

        ) {

            // --- LOGICA VISUALIZZAZIONE ---

            when {

                // 1. CARRELLO VUOTO

                items.isEmpty() && !isProcessing -> {

                    EmptyCartView(onGoBack = { navController.popBackStack() })

                }



                // 2. LISTA ARTICOLI

                else -> {

                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

                        LazyColumn(

                            modifier = Modifier.weight(1f),

                            verticalArrangement = Arrangement.spacedBy(12.dp),

                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)

                        ) {

                            items(items, key = { it.slotId }) { item ->

                                val isNoleggio = item.toolId.contains("RENT", ignoreCase = true)

                                CartItemWrapper(isNoleggio) {

                                    CartItemCard(

                                        item = item,

                                        onIncrease = { cartViewModel.updateItemQuantity(item.slotId, item.quantity + 1) },

                                        onDecrease = { if (item.quantity > 1) cartViewModel.updateItemQuantity(item.slotId, item.quantity - 1) },

                                        onRemove = { cartViewModel.removeItem(item.slotId) }

                                    )

                                }

                            }

                        }



                        // 3. FOOTER CON TOTALE CORRETTO

                        CartFooter(

                            totale = totaleEffettivo,

                            lockerCount = uniqueLockerIds.size,

                            isEnabled = items.isNotEmpty() && !isProcessing,

                            isProcessing = isProcessing,

                            onCheckout = {

                                val idsString = uniqueLockerIds.joinToString(",")

                                navController.navigate(NavRoutes.Linking.createRoute(idsString))

                            }

                        )

                    }

                }

            }

        }

    }

}



@Composable

fun EmptyCartView(onGoBack: () -> Unit) {

    Column(

        modifier = Modifier.fillMaxSize(),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Icon(

            Icons.Default.ShoppingCart,

            contentDescription = null,

            modifier = Modifier.size(80.dp),

            tint = Color.LightGray

        )

        Spacer(Modifier.height(16.dp))

        Text("Il tuo carrello è vuoto", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Text("Aggiungi qualcosa per iniziare", color = Color.Gray)

        Spacer(Modifier.height(24.dp))

        Button(

            onClick = onGoBack,

            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)

        ) {

            Text("TORNA ALLO SHOP")

        }

    }

}



@Composable

fun CartItemWrapper(isNoleggio: Boolean, content: @Composable () -> Unit) {

    Column {

        Surface(

            color = if (isNoleggio) Color(0xFFFFF3E0) else Color(0xFFE3F2FD),

            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),

            modifier = Modifier.padding(start = 4.dp)

        ) {

            Text(

                text = if (isNoleggio) "MODALITÀ NOLEGGIO" else "ACQUISTO DEFINITIVO",

                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),

                style = MaterialTheme.typography.labelSmall,

                fontWeight = FontWeight.Bold,

                color = if (isNoleggio) Color(0xFFE65100) else BluePrimary

            )

        }

        content()

    }

}



@Composable

fun CartFooter(

    totale: Double,

    lockerCount: Int,

    isEnabled: Boolean,

    isProcessing: Boolean,

    onCheckout: () -> Unit

) {

    Surface(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 16.dp),

        tonalElevation = 4.dp,

        shape = RoundedCornerShape(24.dp),

        shadowElevation = 8.dp

    ) {

        Column(modifier = Modifier.padding(20.dp)) {

            if (lockerCount > 1) {

                Text(

                    "⚠️ Oggetti situati in $lockerCount locker diversi",

                    color = Color(0xFFD32F2F),

                    style = MaterialTheme.typography.labelSmall,

                    modifier = Modifier.padding(bottom = 8.dp)

                )

            }

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                Text("Totale", style = MaterialTheme.typography.titleMedium)

                Text(

                    text = "€ ${"%.2f".format(totale)}",

                    style = MaterialTheme.typography.headlineMedium,

                    color = BluePrimary,

                    fontWeight = FontWeight.Black

                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(

                onClick = onCheckout,

                enabled = isEnabled,

                modifier = Modifier.fillMaxWidth().height(56.dp),

                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)

            ) {

                if (isProcessing) {

                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))

                } else {

                    Text("PROCEDI AL COLLEGAMENTO", fontWeight = FontWeight.Bold)

                }

            }

        }

    }

}