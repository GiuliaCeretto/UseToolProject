package com.example.usetool.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.usetool.R
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.ExpertViewModel
import androidx.core.net.toUri

@Composable
fun SchedaConsulenteScreen(
    expertId: String,
    expertViewModel: ExpertViewModel
) {
    val context = LocalContext.current
    val experts by expertViewModel.experts.collectAsStateWithLifecycle()
    val expert = remember(experts, expertId) { experts.find { it.id == expertId } }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = LightGrayBackground
    ) { paddingValues ->
        if (expert == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Green1)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    // Rimosso il paddingValues.calculateTopPadding() per eliminare lo spazio in alto
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rimosso l'item con lo Spacer(24.dp) iniziale per far aderire il contenuto al bordo superiore

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp), // Assicura che la card parta da zero
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GreyLight.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Immagine Profilo
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .background(color = Green2, shape = RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.placeholder_profilo),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxHeight(0.9f),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Nome e Cognome
                            Text(
                                text = "${expert.firstName} ${expert.lastName}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BluePrimary,
                                    letterSpacing = (-0.5).sp
                                )
                            )

                            // Professione
                            Text(
                                text = expert.profession,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Green1,
                                    fontSize = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = GreyLight.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))

                            // Bio (Descrizione Lunga)
                            Text(
                                text = expert.bio,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 24.sp,
                                    color = Color.DarkGray
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Focus (Descrizione Breve)
                            Surface(
                                color = Green2.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Green1)) {
                                            append("Focus: ")
                                        }
                                        append(expert.focus)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Bottone Consulenza Verde Chiaro
                            Button(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Green2,
                                    contentColor = Green1
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    "AVVIA CONSULENZA TELEFONICA",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        if (showDialog && expert != null) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Chiamata in uscita", fontWeight = FontWeight.Bold) },
                text = { Text("Stai per contattare ${expert.firstName}. La chiamata utilizzerà il tuo piano tariffario.") },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:${expert.phoneNumber}".toUri()
                        }
                        context.startActivity(intent)
                    }) {
                        Text("CHIAMA", color = Green1, fontWeight = FontWeight.ExtraBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { }) {
                        Text("ANNULLA", color = GreyMedium)
                    }
                }
            )
        }
    }
}