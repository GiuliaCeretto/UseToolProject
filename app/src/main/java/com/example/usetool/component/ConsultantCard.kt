package com.example.usetool.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.model.Expert
import com.example.usetool.navigation.*
import com.example.usetool.ui.theme.*

@Composable
fun ConsultantCard(
    expert: Expert,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(NavRoutes.SchedaConsulente.createRoute(expert.id))
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // NOME
            Text(
                text = expert.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start, //
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            )

            Spacer(Modifier.height(10.dp))

            // FOTO CENTRATA DENTRO IL RIQUADRO
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Green2),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = expert.imageRes ?: R.drawable.placeholder_profilo),
                    contentDescription = expert.name,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(10.dp))

            // PROFESSIONE + FRECCIA
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween, // spazio tra professione e freccia
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) // margine a sx e dx
            ) {
                // Testo professione a sinistra
                Text(
                    text = expert.profession,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Green1
                    )
                )
            }
        }
    }
}
