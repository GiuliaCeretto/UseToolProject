package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.ui.component.*
import com.example.usetool.ui.theme.Green2
import com.example.usetool.ui.theme.GreyLight
import com.example.usetool.ui.viewmodel.ExpertViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Consulenza(
    navController: NavController,
    expertViewModel: ExpertViewModel
) {
    // CORREZIONE: Uso di collectAsStateWithLifecycle per ottimizzare il consumo di risorse
    val experts by expertViewModel.experts.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(expertViewModel.errorMessage) {
        expertViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedProfession by remember { mutableStateOf<String?>(null) }

    // OTTIMIZZAZIONE: Uso di derivedStateOf per evitare ricalcoli inutili della lista filtrata
    val filteredExperts by remember(searchQuery, selectedProfession, experts) {
        derivedStateOf {
            experts.filter { expert ->
                val matchesSearch = searchQuery.isEmpty() ||
                        "${expert.firstName} ${expert.lastName}".contains(searchQuery, ignoreCase = true)
                val matchesProfession = selectedProfession == null || expert.profession == selectedProfession
                matchesSearch && matchesProfession
            }
        }
    }

    val professions = remember(experts) {
        experts.map { it.profession }.distinct()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { AppTopBar(navController) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Rubrica esperti",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cerca esperto") },
                placeholder = { Text("Nome o cognome...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green2,
                    unfocusedBorderColor = GreyLight
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedProfession == null,
                        onClick = { selectedProfession = null },
                        label = { Text("Tutte") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green2
                        )
                    )
                }
                items(professions) { profession ->
                    FilterChip(
                        selected = selectedProfession == profession,
                        onClick = { selectedProfession = profession },
                        label = { Text(profession) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green2
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (experts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green2)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExperts) { expert ->
                        ConsultantCard(
                            expert = expert,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}