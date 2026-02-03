package com.example.usetool.screens.consulting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.model.Expert
import com.example.usetool.viewmodel.ConsultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaConsulenteScreen(
    navController: NavController,
    expertId: String,
    consultViewModel: ConsultViewModel = viewModel()
) {
    val expert = consultViewModel.findExpertById(expertId)

    Scaffold(
        topBar = { AppTopBar(navController, "Consulente") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        if (expert == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Esperto non trovato")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = expert.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = expert.profession,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = expert.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
