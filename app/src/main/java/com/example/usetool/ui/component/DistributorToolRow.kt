package com.example.usetool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.usetool.model.Tool

@Composable
fun DistributorToolRow(
    tool: Tool,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val alpha = if (tool.available) 1f else 0.4f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .toggleable(
                    value = checked,
                    enabled = tool.available,
                    onValueChange = onCheckedChange
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(tool.imageRes),
                contentDescription = tool.name,
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(tool.name, style = MaterialTheme.typography.titleSmall)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tool.technicalData.values.take(3).forEach {
                        Text(it, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = tool.pricePerHour?.let { "€$it / h" }
                        ?: tool.purchasePrice?.let { "€$it" }
                        ?: "",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Checkbox(
                checked = checked,
                onCheckedChange = null,
                enabled = tool.available
            )
        }
    }
}
