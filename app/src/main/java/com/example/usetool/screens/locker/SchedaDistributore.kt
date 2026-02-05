package com.example.usetool.screens.distributor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.DistributorToolRow
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.CartViewModel
import com.example.usetool.viewmodel.UseToolViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaDistributoreScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    val locker = viewModel.findLockerById(id) ?: return
    val tools = viewModel.topTools.collectAsState().value
        .filter { locker.toolsAvailable.contains(it.id) }

    val selected = remember { mutableStateMapOf<String, Boolean>() }

    val sheetState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = sheetState,
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
                // ================= HEADER =================
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

                Text(locker.address)

                Spacer(Modifier.height(16.dp))

                // ================= STRUMENTI A NOLEGGIO =================
                Text("Strumenti a noleggio", style = MaterialTheme.typography.titleMedium)
                tools.filter { it.pricePerHour != null }
                    .sortedBy { !it.available }
                    .forEach { tool ->
                        DistributorToolRow(
                            tool = tool,
                            checked = selected[tool.id] == true,
                            onCheckedChange = { selected[tool.id] = it }
                        )
                    }

                Spacer(Modifier.height(12.dp))

                // ================= MATERIALI DI CONSUMO =================
                Text("Materiali di consumo", style = MaterialTheme.typography.titleMedium)
                tools.filter { it.purchasePrice != null }
                    .sortedBy { !it.available }
                    .forEach { tool ->
                        DistributorToolRow(
                            tool = tool,
                            checked = selected[tool.id] == true,
                            onCheckedChange = { selected[tool.id] = it }
                        )
                    }

                Spacer(Modifier.height(16.dp))

                val selectedTools = tools.filter { selected[it.id] == true }

                Text("Distanza: ${locker.distanceKm} km")
                Text("Totale articoli: ${selectedTools.size}")

                Spacer(Modifier.height(12.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedTools.isNotEmpty(),
                    onClick = {
                        selectedTools.forEach {
                            cartVM.add(it, locker.id)
                        }
                        navController.navigate(NavRoutes.Carrello.route)
                    }
                ) {
                    Text("Aggiungi al carrello (${selectedTools.size})")
                }
            }
        }
    ) { padding ->
        // ================= MAPPA =================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // MAPPA
            Image(
                painter = painterResource(R.drawable.placeholder_map),
                contentDescription = "Mappa distributore",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // POSIZIONI FAKE (coordinate schermo)
            val userPosition = Offset(250f, 600f)
            val lockerPosition = Offset(550f, 350f)

            // LINEA TRATTEGGIATA
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
