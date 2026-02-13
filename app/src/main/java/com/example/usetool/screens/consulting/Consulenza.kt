package com.example.usetool.screens.consulting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.usetool.component.*
import com.example.usetool.ui.theme.Green2
import com.example.usetool.ui.theme.GreyLight
import com.example.usetool.viewmodel.ConsultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Consulenza(
    navController: NavController,
    consultViewModel: ConsultViewModel = viewModel()
) {
    // StateFlow -> State per Compose
    val experts = consultViewModel.experts.collectAsState().value

    // Stato per la ricerca testuale
    var searchQuery by remember { mutableStateOf("") }

    // Stato per il filtro professione
    var selectedProfession by remember { mutableStateOf<String?>(null) }

    // Lista filtrata in base alla ricerca e al filtro professione
    val filteredExperts = experts.filter { expert ->
        (searchQuery.isEmpty() || expert.name.contains(searchQuery, ignoreCase = true)) &&
                (selectedProfession == null || expert.profession == selectedProfession)
    }

    // Lista delle professioni disponibili
    val professions = experts.map { it.profession }.distinct()

    // Root senza Scaffold
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // BARRA DI RICERCA
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Cerca esperto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreyLight,
                unfocusedBorderColor = GreyLight,
                focusedLabelColor = GreyLight,
                cursorColor = GreyLight
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CAROSELLO PROFESSIONI
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pulsante "Tutte"
            item {
                FilterChip(
                    selected = selectedProfession == null,
                    onClick = { selectedProfession = null },
                    label = { Text("Tutte") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Green2,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedProfession == null,
                        borderColor = GreyLight,
                        selectedBorderColor = GreyLight,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.dp
                    )
                )
            }

            items(professions) { profession ->
                FilterChip(
                    selected = selectedProfession == profession,
                    onClick = { selectedProfession = profession },
                    label = { Text(profession) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Green2,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedProfession == profession,
                        borderColor = GreyLight,
                        selectedBorderColor = GreyLight,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.dp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GRID DI CARD
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
