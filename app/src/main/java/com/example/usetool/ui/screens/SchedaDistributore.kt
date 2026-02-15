package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.ui.component.DistributorToolRow
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import com.example.usetool.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaDistributoreScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    val lockers by viewModel.lockers.collectAsStateWithLifecycle()
    val locker = lockers.find { it.id == id }
    val allTools by viewModel.topTools.collectAsStateWithLifecycle()
    val allSlots by viewModel.slots.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val lockerSlots = allSlots.filter { it.lockerId == id }

    // Carichiamo tutti i tool che appartengono al locker
    val toolsInLocker = allTools.filter { tool ->
        lockerSlots.any { it.toolId == tool.id }
    }

    val selected = remember { mutableStateMapOf<String, Boolean>() }
    var isProcessing by remember { mutableStateOf(false) }

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    )

    if (locker == null) return

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 420.dp,
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Box(Modifier.fillMaxWidth().height(4.dp).width(40.dp).background(Color.LightGray, RoundedCornerShape(50)).align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(20.dp))

                LockerHeaderCard(locker)

                Spacer(Modifier.height(16.dp))

                toolsInLocker.forEach { tool ->
                    val slot = lockerSlots.find { it.toolId == tool.id }
                    val isActuallyInCart = slot?.status == "IN_CARRELLO"
                    val isAvailable = slot?.status == "DISPONIBILE"

                    // 🔥 Se non è disponibile e non è già nel carrello, la riga diventa opaca
                    val isEnabled = isAvailable == true || isActuallyInCart
                    val rowAlpha = if (isEnabled) 1.0f else 0.5f

                    Box(modifier = Modifier.alpha(rowAlpha)) {
                        DistributorToolRow(
                            tool = tool,
                            checked = isActuallyInCart || (selected[tool.id] == true),
                            available = isAvailable ?: false,
                            isAlreadyInCart = isActuallyInCart,
                            onCheckedChange = { isChecked ->
                                // Permette il click solo se l'attrezzo è disponibile o nel carrello
                                if (isEnabled && !isActuallyInCart && !isProcessing) {
                                    selected[tool.id] = isChecked
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                val newSelectedIds = selected.filter { (toolId, isSelected) ->
                    val slot = lockerSlots.find { it.toolId == toolId }
                    isSelected && slot?.status == "DISPONIBILE"
                }.keys

                val totalCost = newSelectedIds.sumOf { toolId -> allTools.find { it.id == toolId }?.price ?: 0.0 }
                val distance = viewModel.getDistanceToLocker(locker)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("${"%.1f".format(distance)} Km", color = Color(0xFFFFD600), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("Tot nuovi: €${"%.2f".format(totalCost)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    }

                    Button(
                        enabled = newSelectedIds.isNotEmpty() && !isProcessing,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        modifier = Modifier.height(56.dp).fillMaxWidth(0.75f),
                        onClick = {
                            isProcessing = true
                            scope.launch {
                                val selectedList = newSelectedIds.toList()
                                selectedList.forEachIndexed { index, toolId ->
                                    val tool = allTools.find { it.id == toolId }
                                    val slot = lockerSlots.find { it.toolId == toolId && it.status == "DISPONIBILE" }

                                    if (tool != null && slot != null) {
                                        cartVM.addToolToCart(tool, slot)
                                        delay(600)
                                    }

                                    if (index == selectedList.size - 1) {
                                        navController.navigate(NavRoutes.Carrello.route)
                                        isProcessing = false
                                    }
                                }
                            }
                        }
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("PRENOTA (${newSelectedIds.size})", color = Color.White, fontWeight = FontWeight.Bold)
                        }
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

            MapVisualization()

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(16.dp).background(Color.White, RoundedCornerShape(50))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }
        }
    }
}

@Composable
private fun MapVisualization() {
    val userPos = Offset(350f, 750f)
    val lockerPos = Offset(650f, 450f)
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color(0xFF1A237E),
                start = userPos,
                end = lockerPos,
                strokeWidth = 8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f))
            )
        }

        Image(
            painter = painterResource(id = R.drawable.pin),
            contentDescription = null,
            modifier = Modifier.size(40.dp).offset(
                x = with(density) { userPos.x.toDp() - 20.dp },
                y = with(density) { userPos.y.toDp() - 40.dp }
            )
        )

        Image(
            painter = painterResource(id = R.drawable.pin),
            contentDescription = null,
            modifier = Modifier.size(45.dp).offset(
                x = with(density) { lockerPos.x.toDp() - 22.dp },
                y = with(density) { lockerPos.y.toDp() - 45.dp }
            )
        )
    }
}

@Composable
private fun LockerHeaderCard(locker: LockerEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.placeholder_locker), contentDescription = null, modifier = Modifier.size(60.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(locker.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                AddressIndicator(locker.address)
            }
        }
    }
}

@Composable
private fun AddressIndicator(address: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(Color(0xFFFFC107), RoundedCornerShape(50)))
            Spacer(Modifier.width(8.dp))
            Text(address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Box(Modifier.padding(start = 3.5.dp).height(12.dp).width(1.dp).background(Color.LightGray))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).border(1.dp, Color(0xFFFFC107), RoundedCornerShape(50)))
            Spacer(Modifier.width(8.dp))
            Text("La tua posizione", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
        }
    }
}