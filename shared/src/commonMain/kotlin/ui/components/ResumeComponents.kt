package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ResumeCard(
    month: String,
    totalIncome: String,
    totalOutgoing: String,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = month,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                IconButton(
                    onClick = onDetailsClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Income",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = totalIncome,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Outgoing",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = totalOutgoing,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}

//TODO: Remake the viewmodel and change the state based if there is or not some data
//@Composable
//fun ResumeDocker(
//    viewModel: BalanceViewModel,
//    onDetailsClick: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val state by viewModel.state.collectAsState()
//    if () {
//        Column(
//            modifier = modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "To start, click on",
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add Data",
//                    tint = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier.size(48.dp)
//                )
//        }
//    } else {
//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = 180.dp), // Adjust to 2 columns; use Adaptive for responsive design if needed
//            modifier = modifier.fillMaxSize(),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Group by year and sort months
//            val balancesByYear = state.balancesMensuales.groupBy { it.monthDisplay.split(" ")[1].toInt() }
//            balancesByYear.keys.sorted().forEach { year ->
//                item {
//                    Text(
//                        text = "Year $year",
//                        style = MaterialTheme.typography.headlineSmall,
//                        color = MaterialTheme.colorScheme.onSurface,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                }
//                items(balancesByYear[year]?.sortedBy { it.monthDisplay.split(" ")[0] } ?: emptyList()) { finance ->
//                    ResumeCard(
//                        month = finance.monthDisplay,
//                        totalIncome = finance.totalIncome,
//                        totalOutgoing = finance.totalOutgoing,
//                        onDetailsClick = { onDetailsClick(finance.monthDisplay) }
//                    )
//                }
//            }
//        }
//    }

