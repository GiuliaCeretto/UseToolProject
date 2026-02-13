package com.example.usetool.ui.screens.locker

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.component.*
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import androidx.compose.ui.platform.LocalDensity
import com.example.usetool.data.dao.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaDistributoreScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    // Recupera il locker dalla lista osservata nel ViewModel (Entity)
    val lockers by viewModel.lockers.collectAsState()
    val locker = lockers.find { it.id == id } ?: return

    // Osserva gli slot specifici di questo locker (Entity)
    val lockerSlots by viewModel.getSlotsForLocker(id).collectAsState(initial = emptyList())

    // Osserva tutti i tool per visualizzare i dettagli (Entity)
    val allTools by viewModel.topTools.collectAsState()

    // Filtra gli attrezzi presenti in questo distributore
    val toolsInLocker = allTools.filter { tool ->
        lockerSlots.any { it.toolId == tool.id }
    }

    val selected = remember { mutableStateMapOf<String, Boolean>() }
    val sheetState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = sheetState,
        topBar = { AppTopBar(navController) },
        sheetPeekHeight = 140.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // HEADER - Utilizza LockerEntity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(locker.name, style = MaterialTheme.typography.titleLarge)
                }

                Text(locker.address, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(16.dp))

                // STRUMENTI A NOLEGGIO (Filtra per tipo Entity)
                Text("Strumenti a noleggio", style = MaterialTheme.typography.titleMedium)

                toolsInLocker.filter { it.type == "noleggio" }
                    .sortedByDescending { t ->
                        lockerSlots.any { it.toolId == t.id && it.status == "DISPONIBILE" }
                    }
                    .forEach { tool ->
                        DistributorToolRow(
                            tool = tool,
                            allSlots = lockerSlots,
                            checked = selected[tool.id] == true,
                            onCheckedChange = { selected[tool.id] = it }
                        )
                    }

                Spacer(Modifier.height(12.dp))

                // MATERIALI DI CONSUMO (Filtra per tipo Entity)
                Text("Materiali di consumo", style = MaterialTheme.typography.titleMedium)

                toolsInLocker.filter { it.type == "acquisto" }
                    .sortedByDescending { t ->
                        lockerSlots.any { it.toolId == t.id && it.status == "DISPONIBILE" }
                    }
                    .forEach { tool ->
                        DistributorToolRow(
                            tool = tool,
                            allSlots = lockerSlots,
                            checked = selected[tool.id] == true,
                            onCheckedChange = { selected[tool.id] = it }
                        )
                    }

                Spacer(Modifier.height(16.dp))

                val selectedTools = toolsInLocker.filter { selected[it.id] == true }
                Text("Totale articoli selezionati: ${selectedTools.size}", style = MaterialTheme.typography.labelLarge)

                Spacer(Modifier.height(12.dp))

                // PULSANTE AGGIUNGI AL CARRELLO
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedTools.isNotEmpty(),
                    onClick = {
                        selectedTools.forEach { tool ->
                            val slot = lockerSlots.find { it.toolId == tool.id }
                            if (slot != null) {
                                // CORRETTO: Passiamo direttamente le Entity, niente DTO nel ViewModel
                                cartVM.addToolToCart(tool, slot)
                            }
                        }
                        navController.navigate(NavRoutes.Carrello.route)
                    }
                ) {
                    Text("Aggiungi al carrello (${selectedTools.size})")
                }
            }
        }
    ) { padding ->
        // ================= MAPPA (UI PURA) =================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(R.drawable.placeholder_map),
                contentDescription = "Mappa distributore",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // POSIZIONI FITTIZIE (Placeholder coordinate schermo)
            val userPosition = Offset(250f, 600f)
            val lockerPosition = Offset(550f, 350f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Blue,
                    start = userPosition,
                    end = lockerPosition,
                    strokeWidth = 6f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f))
                )
            }

            val density = LocalDensity.current

            // MARKER UTENTE
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Posizione utente",
                tint = Color.Blue,
                modifier = Modifier
                    .size(40.dp)
                    .offset(
                        x = with(density) { userPosition.x.toDp() },
                        y = with(density) { userPosition.y.toDp() }
                    )
            )

            // MARKER DISTRIBUTORE
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Distributore",
                tint = Color.Red,
                modifier = Modifier
                    .size(40.dp)
                    .offset(
                        x = with(density) { lockerPosition.x.toDp() },
                        y = with(density) { lockerPosition.y.toDp() }
                    )
            )
        }
    }
}