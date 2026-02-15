package com.example.usetool.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.theme.Green1
import com.example.usetool.ui.viewmodel.ExpertViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaConsulenteScreen(
    navController: NavController,
    expertId: String,
    expertViewModel: ExpertViewModel
) {
    val context = LocalContext.current
    val experts by expertViewModel.experts.collectAsState()
    val expert = remember(experts, expertId) { experts.find { it.id == expertId } }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (expert == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Green1)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Inseriamo un piccolo spacer solo se vuoi evitare che tocchi il bordo fisico superiore
                item { Spacer(modifier = Modifier.height(4.dp)) }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
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
                                    .height(260.dp)
                                    .background(color = Color(0xFFE8F9ED), shape = RoundedCornerShape(16.dp)),
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

                            Text(
                                text = "${expert.firstName} ${expert.lastName}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2C3E50),
                                    letterSpacing = (-0.5).sp
                                )
                            )

                            Text(
                                text = expert.profession,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A9078),
                                    fontSize = 19.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = expert.bio,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 26.sp,
                                    color = Color(0xFF546E7A)
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Surface(
                                color = Color(0xFFF1F8F4),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF4A9078))) {
                                            append("Focus: ")
                                        }
                                        append("Consulenza tecnica, risoluzione problemi e supporto all'acquisto.")
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D705D)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    "AVVIA CONSULENZA TELEFONICA",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Spacer finale per non far toccare il fondo
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        if (showDialog && expert != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Chiamata in uscita") },
                text = { Text("Stai per contattare ${expert.firstName}. La chiamata utilizzerà il tuo piano tariffario.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${expert.phoneNumber}")
                        }
                        context.startActivity(intent)
                    }) {
                        Text("CHIAMA", color = Color(0xFF4A9078), fontWeight = FontWeight.ExtraBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("ANNULLA", color = Color.Gray)
                    }
                }
            )
        }
    }
}