package com.example.usetool.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.usetool.model.CartItem
import com.example.usetool.ui.theme.BlueLight
import com.example.usetool.ui.theme.BluePrimary

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // sfondo bianco
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // bordino GreyLight
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.tool.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "Locker: ${item.distributorId ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Image(
                    painter = painterResource(item.tool.imageRes),
                    contentDescription = item.tool.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(12.dp))

            // FOOTER: PREZZO + CONTROLLI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // PREZZO
                Column {
                    if (item.tool.pricePerHour != null) {
                        // Mostra prezzo orario e totale in base alle ore
                        Text(
                            buildAnnotatedString {
                                append("€ ")
                                withStyle(style = androidx.compose.ui.text.SpanStyle(
                                    color = BluePrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                                )) {
                                    append("%.2f".format(item.subtotal))
                                }
                                append(" (€${item.tool.pricePerHour}/h)")
                            }
                        )
                    } else if (item.tool.purchasePrice != null) {
                        // Prezzo totale per gli oggetti acquistabili
                        Text(
                            buildAnnotatedString {
                                append("€ ")
                                withStyle(style = androidx.compose.ui.text.SpanStyle(
                                    color = BluePrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                                )) {
                                    append("%.2f".format(item.subtotal))
                                }
                            }
                        )
                    }
                }


                // CONTATORE PIÙ PICCOLO
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlueLight,
                    modifier = Modifier.width(120.dp) // larghezza fissa più piccola
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // - BUTTON
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onDecrease,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Meno")
                            }
                        }

                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )

                        // NUMERO
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (item.tool.pricePerHour != null)
                                    "${item.durationHours} h"
                                else
                                    "${item.quantity}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )

                        // + BUTTON
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onIncrease,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Più")
                            }
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                // PULSANTE ELIMINA DENTRO RICUADRO
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Rimuovi",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
