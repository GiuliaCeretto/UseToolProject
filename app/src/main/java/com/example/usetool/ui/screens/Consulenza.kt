package com.example.usetool.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.usetool.R
import com.example.usetool.data.dao.ExpertEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.ExpertViewModel
import com.example.usetool.ui.theme.* import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Consulenza(
    navController: NavController,
    expertViewModel: ExpertViewModel
) {
    val experts by expertViewModel.experts.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(expertViewModel.errorMessage) {
        expertViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedProfession by remember { mutableStateOf<String?>(null) }

    val filteredExperts by remember(searchQuery, selectedProfession, experts) {
        derivedStateOf {
            experts.filter { expert ->
                val matchesSearch = searchQuery.isEmpty() ||
                        expert.firstName.contains(searchQuery, ignoreCase = true) ||
                        expert.lastName.contains(searchQuery, ignoreCase = true) ||
                        expert.profession.contains(searchQuery, ignoreCase = true) ||
                        expert.focus.contains(searchQuery, ignoreCase = true)

                val matchesProfession = selectedProfession == null || expert.profession == selectedProfession

                matchesSearch && matchesProfession
            }
        }
    }

    val professions = remember(experts) {
        experts.map { it.profession }.distinct().sorted()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LightGrayBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Campo di ricerca COMPATTO
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Cerca esperto...",
                        color = GreyMedium,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = 13.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = GreyLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filtri Professione
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedProfession == null,
                        onClick = { selectedProfession = null },
                        label = { Text("Tutte", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green1,
                            containerColor = Green2.copy(alpha = 0.5f),
                            selectedLabelColor = Color.White,
                            labelColor = Color.Black
                        )
                    )
                }
                items(professions) { profession ->
                    FilterChip(
                        selected = selectedProfession == profession,
                        onClick = { selectedProfession = profession },
                        label = { Text(profession, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green1,
                            containerColor = Green2.copy(alpha = 0.5f),
                            selectedLabelColor = Color.White,
                            labelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (experts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green1)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredExperts) { expert ->
                        ConsultantCardCompact(expert = expert) {
                            navController.navigate(NavRoutes.SchedaConsulente.createRoute(expert.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultantCardCompact(
    expert: ExpertEntity,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GreyLight.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${expert.firstName} ${expert.lastName}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BluePrimary
                ),
                maxLines = 1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Green2),
                contentAlignment = Alignment.Center
            ) {
                // Utilizzo di AsyncImage per caricare l'immagine da URL
                AsyncImage(
                    model = expert.imageUrl,
                    contentDescription = "Foto di ${expert.firstName}",
                    modifier = Modifier.fillMaxHeight(0.85f),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.placeholder_profilo),
                    error = painterResource(id = R.drawable.placeholder_profilo)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expert.profession,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Green1,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    modifier = Modifier.size(22.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Green1.copy(alpha = 0.8f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}