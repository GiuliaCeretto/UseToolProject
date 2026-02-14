package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaDistributoreScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    // 1. Recupero Locker e Dati dal VM (Reattivo)
    val lockers by viewModel.lockers.collectAsState()
    val locker = lockers.find { it.id == id }

    val allTools by viewModel.topTools.collectAsState()
    val allSlots by viewModel.slots.collectAsState()

    // Filtriamo gli slot di questo locker e i relativi attrezzi
    val lockerSlots = allSlots.filter { it.lockerId == id }
    val toolsInLocker = allTools.filter { tool ->
        lockerSlots.any { it.toolId == tool.id }
    }

    // Stato per la selezione (ID attrezzo -> Booleano)
    val selected = remember { mutableStateMapOf<String, Boolean>() }

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    if (locker == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 380.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShape = RoundedCornerShape(topStart = 38.dp, topEnd = 24.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                LockerHeaderCard(locker)

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(Modifier.height(12.dp))

                // SEZIONE NOLEGGIO
                Text("Strumenti a noleggio", style = MaterialTheme.typography.titleMedium)
                toolsInLocker.filter { it.type == "noleggio" }
                    .forEach { tool ->
                        val isAvailable = lockerSlots.any { it.toolId == tool.id && it.status == "DISPONIBILE" }
                        DistributorToolRow(
                            tool = tool,
                            available = isAvailable,
                            checked = selected[tool.id] == true,
                            onCheckedChange = { selected[tool.id] = it }
                        )
                    }

                Spacer(Modifier.height(12.dp))

                // SEZIONE CONSUMABILI
                Text("Materiali di consumo", style = MaterialTheme.typography.titleMedium)
                toolsInLocker.filter { it.type == "acquisto" }
                    .forEach { tool ->
                        val isAvailable = lockerSlots.any { it.toolId == tool.id && it.status == "DISPONIBILE" }
                        DistributorToolRow(
                            tool = tool,
                            available = isAvailable,
                            checked = selected[tool.id] == true,
                            onCheckedChange = { selected[tool.id] = it }
                        )
                    }

                Spacer(Modifier.height(24.dp))

                // AZIONE FINALE: Aggiunta al carrello
                val selectedToolIds = selected.filter { it.value }.keys

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Vicinanza: OK", // LockerEntity non ha distanza, usiamo un placeholder
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFFD600)
                    )

                    Button(
                        enabled = selectedToolIds.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            selectedToolIds.forEach { toolId ->
                                val tool = allTools.find { it.id == toolId }
                                val slot = lockerSlots.find { it.toolId == toolId && it.status == "DISPONIBILE" }
                                if (tool != null && slot != null) {
                                    cartVM.addToolToCart(tool, slot)
                                }
                            }
                            navController.navigate(NavRoutes.Carrello.route)
                        }
                    ) {
                        Text("Aggiungi al carrello (${selectedToolIds.size})")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(R.drawable.placeholder_map),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Bottone Indietro (Fix 'ic_back')
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(16.dp).background(Color.White, RoundedCornerShape(50))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }

            MapVisualization()
        }
    }
}

@Composable
private fun DistributorToolRow(
    tool: ToolEntity,
    available: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = available
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(tool.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                if (available) "Disponibile - €${tool.price}" else "Esaurito",
                style = MaterialTheme.typography.labelSmall,
                color = if (available) Color.Gray else Color.Red
            )
        }
    }
}

@Composable
private fun LockerHeaderCard(locker: LockerEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.placeholder_locker),
                contentDescription = null,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(locker.name, style = MaterialTheme.typography.titleLarge)
                Text(locker.address, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun MapVisualization() {
    val userPosition = Offset(250f, 600f)
    val lockerPosition = Offset(550f, 350f)
    val density = LocalDensity.current

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            color = Color.Blue,
            start = userPosition,
            end = lockerPosition,
            strokeWidth = 6f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f))
        )
    }

    Icon(
        imageVector = Icons.Default.Place,
        contentDescription = null,
        tint = Color.Blue,
        modifier = Modifier.size(40.dp).offset(
            x = with(density) { userPosition.x.toDp() - 20.dp },
            y = with(density) { userPosition.y.toDp() - 40.dp }
        )
    )

    Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = null,
        tint = Color.Red,
        modifier = Modifier.size(40.dp).offset(
            x = with(density) { lockerPosition.x.toDp() - 20.dp },
            y = with(density) { lockerPosition.y.toDp() - 40.dp }
        )
    )
}