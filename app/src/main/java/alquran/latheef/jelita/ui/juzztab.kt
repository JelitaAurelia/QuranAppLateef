package alquran.latheef.jelita.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun JuzTab(navController: NavController) {
    val juzList = (1..30).map { JuzItem(it, "Juz $it") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4F6457))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(juzList) { juz ->
            JuzCard(juz, navController)
        }
    }
}

data class JuzItem(val number: Int, val name: String)

@Composable
fun JuzCard(juz: JuzItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { //ke juz detail screen
                Log.d("JuzScreen", "Navigating to juzDetail/${juz.number}")
                navController.navigate("juzDetail/${juz.number}")
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = juz.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "Juz ${juz.number}",
                fontSize = 16.sp,
                color = Color(0xFF00796B)
            )
        }
    }
}