@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.usetool.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.ui.viewmodel.LinkingViewModel

@Composable
fun CollegamentoScreen(
    navController: NavController,
    linkingViewModel: LinkingViewModel
) {

    val inputCode by linkingViewModel.inputCode.collectAsState()
    val isLinked by linkingViewModel.isLinked.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val toneGenerator = remember {
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    // Mappatura numero → frequenza (Hz simulata tramite TONE_DTMF)
    fun playTone(number: Int) {
        val tone = when (number) {
            1 -> ToneGenerator.TONE_DTMF_1
            2 -> ToneGenerator.TONE_DTMF_2
            3 -> ToneGenerator.TONE_DTMF_3
            4 -> ToneGenerator.TONE_DTMF_4
            5 -> ToneGenerator.TONE_DTMF_5
            6 -> ToneGenerator.TONE_DTMF_6
            7 -> ToneGenerator.TONE_DTMF_7
            8 -> ToneGenerator.TONE_DTMF_8
            9 -> ToneGenerator.TONE_DTMF_9
            else -> ToneGenerator.TONE_DTMF_0
        }

        toneGenerator.startTone(tone, 200)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // BOX CODICE
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Il tuo codice di collegamento è:",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "2568",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Inserito: $inputCode",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }


        Spacer(modifier = Modifier.height(32.dp))


        // TASTIERINO

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (row in 0..2) {
                Row {
                    for (col in 1..3) {
                        val number = row * 3 + col
                        Button(
                            onClick = {
                                playTone(number)
                                linkingViewModel.addDigit(number)
                            },
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(number.toString())
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // PULSANTE COLLEGA

        Button(
            onClick = {
                if (linkingViewModel.checkCode()) {
                    linkingViewModel.link()
                    showDialog = true
                } else {
                    linkingViewModel.resetCode()
                }
            },
            enabled = inputCode.length == 4,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Collega")
        }
    }

    // POPUP SUCCESSO

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("home") {
                            popUpTo("collegamento") { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Successo") },
            text = { Text("Collegamento avvenuto con successo") }
        )
    }
}

