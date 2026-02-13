package com.example.usetool.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.GreyLight
import com.example.usetool.component.PastRentalCard
import com.example.usetool.viewmodel.UseToolViewModel

@Composable
fun ProfiloScreen(
    navController: NavController,
    viewModel: UseToolViewModel
) {
    // Imposto il background dell'intera schermata
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TITOLO
        item {
            Text(
                text = "Profilo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item { Spacer(Modifier.height(16.dp)) }

        // FOTO PROFILO
        item {
            Image(
                painter = painterResource(R.drawable.placeholder_profilo),
                contentDescription = "Foto profilo",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        item { Spacer(Modifier.height(16.dp)) }

        // CARD INFO UTENTE
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GreyLight),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // bianco
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(
                        text = "Mario Rossi",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "@mariorossi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "mario.rossi@email.com",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("12 noleggi", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.width(8.dp))
                        Text("|")
                        Spacer(Modifier.width(8.dp))
                        Text("2 in corso", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // NOLEGGI IN CORSO
        item {
            Text(
                text = "Noleggi in corso",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GreyLight),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RentalActionButton("Rinnova")
                        RentalActionButton("Gestione")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RentalActionButton("Pagamento")
                        RentalActionButton("Fattura")
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // STORICO NOLEGGI
        item {
            Text(
                text = "Storico noleggi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        items(3) {
            PastRentalCard()
        }
    }
}

@Composable
fun ProfileActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
            .clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = buttonColor,
            modifier = Modifier.size(56.dp) // <- rimuovo tonalElevation
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor // ← ora bianco funziona
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = buttonColor // se vuoi il testo colorato come il pulsante
        )
    }
}

@Composable
fun RentalActionButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(150.dp)
    ) {
        Text(text)
    }
}
