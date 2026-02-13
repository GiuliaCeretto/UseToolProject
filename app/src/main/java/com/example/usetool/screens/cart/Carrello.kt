package com.example.usetool.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.*
import com.example.usetool.navigation.*
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.viewmodel.CartViewModel

@Composable
fun CarrelloScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val items by cartViewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Titolo della schermata
        Text(
            text = "Riepilogo carrello",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (items.isEmpty()) {
            Text("Il carrello è vuoto")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                CartItemCard(
                    item = item,
                    onIncrease = {
                        if (item.tool.pricePerHour != null)
                            cartViewModel.updateDuration(item.id, +1)
                        else
                            cartViewModel.updateQuantity(item.id, +1)
                    },
                    onDecrease = {
                        if (item.tool.pricePerHour != null)
                            cartViewModel.updateDuration(item.id, -1)
                        else
                            cartViewModel.updateQuantity(item.id, -1)
                    },
                    onRemove = {
                        cartViewModel.remove(item.id)
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Totale: ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = "€${"%.2f".format(cartViewModel.total)}",
                style = MaterialTheme.typography.headlineMedium,
                color = BluePrimary,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline()
            )
        }
    }
}
