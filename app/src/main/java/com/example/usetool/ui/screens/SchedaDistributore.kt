package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.DistributorToolRow
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
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

    val isCartProcessing by cartVM.isProcessing.collectAsStateWithLifecycle()
    val myCartItems by cartVM.cartItems.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val lockerSlots = remember(allSlots, id) {
        allSlots.filter { it.lockerId == id }
    }

    val toolsInLocker = remember(allTools, lockerSlots) {
        allTools.filter { tool -> lockerSlots.any { it.toolId == tool.id } }
    }

    val selected = remember { mutableStateMapOf<String, Boolean>() }

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    )

    if (locker == null) return

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = sheetState,
        sheetPeekHeight = 420.dp,
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(LightGrayBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Box(
                    Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(4.dp)
                        .width(40.dp)
                        .background(GreyLight, RoundedCornerShape(50))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))
                LockerHeaderCard(locker)
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Seleziona lo strumento che cerchi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BluePrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                toolsInLocker.forEach { tool ->
                    val slot = lockerSlots.find { it.toolId == tool.id }

                    // 1. Identificazione tipo
                    val isRental = tool.type.trim().contains("rent", ignoreCase = true) ||
                            tool.type.trim().contains("noleggio", ignoreCase = true)

                    val isActuallyInMyCart = myCartItems.any { it.slotId == slot?.id }

                    // 2. Logica condizionale: Rental blocca se in altri carrelli, Acquisto no
                    val isOccupiedByOthers = isRental && (slot?.status == "IN_CARRELLO") && !isActuallyInMyCart

                    val isAvailableToMe = if (isRental) {
                        slot?.status == "DISPONIBILE" && !isOccupiedByOthers
                    } else {
                        (slot?.quantity ?: 0) > 0
                    }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .alpha(if (isAvailableToMe || isActuallyInMyCart) 1.0f else 0.4f)
                            .clickable(enabled = isAvailableToMe || isActuallyInMyCart) {
                                navController.navigate(NavRoutes.SchedaStrumento.createRoute(tool.id))
                            }
                    ) {
                        Column {
                            DistributorToolRow(
                                tool = tool,
                                checked = isActuallyInMyCart || (selected[tool.id] == true),
                                available = isAvailableToMe,
                                isAlreadyInCart = isActuallyInMyCart,
                                onCheckedChange = { isChecked ->
                                    if (isAvailableToMe && !isCartProcessing) {
                                        selected[tool.id] = isChecked
                                    }
                                }
                            )

                            if (isOccupiedByOthers) {
                                Text(
                                    text = "Momentaneamente nel carrello di un altro utente",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(start = 72.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                val toolsToAdd = selected.filter { it.value }.mapNotNull { (toolId, _) ->
                    val tool = allTools.find { it.id == toolId }
                    val slot = lockerSlots.find { it.toolId == toolId && it.status == "DISPONIBILE" }
                    if (tool != null && slot != null) tool to slot else null
                }

                val totalCost = toolsToAdd.sumOf { it.first.price }
                val distance = viewModel.getDistanceToLocker(locker)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("${"%.1f".format(distance)} Km", color = YellowPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("Tot: €${"%.2f".format(totalCost)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BluePrimary)
                    }

                    Button(
                        enabled = toolsToAdd.isNotEmpty() && !isCartProcessing,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            contentColor = Color.White,
                            disabledContainerColor = GreyLight
                        ),
                        modifier = Modifier.height(56.dp).fillMaxWidth(0.7f),
                        onClick = {
                            scope.launch {
                                cartVM.addMultipleToolsToCart(toolsToAdd)
                                navController.navigate(NavRoutes.Carrello.route)
                            }
                        }
                    ) {
                        if (isCartProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("PRENOTA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.placeholder_map),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            MapVisualization()
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.statusBarsPadding().padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = BluePrimary
                )
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
                color = BluePrimary,
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
        border = BorderStroke(1.dp, GreyLight.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.placeholder_locker), contentDescription = null, modifier = Modifier.size(60.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(locker.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BluePrimary)
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
            Box(Modifier.size(8.dp).background(YellowPrimary, RoundedCornerShape(50)))
            Spacer(Modifier.width(8.dp))
            Text(address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Box(Modifier.padding(start = 3.5.dp).height(12.dp).width(1.dp).background(GreyLight))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).border(1.dp, YellowPrimary, RoundedCornerShape(50)))
            Spacer(Modifier.width(8.dp))
            Text("La tua posizione", style = MaterialTheme.typography.bodySmall, color = GreyMedium)
        }
    }
}