package com.example.utsquranappq.ui
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.utsquranappq.R

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar() },
    ) { padding ->
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF324851), Color(0xFF4F6457), Color(0xFF324851)),
            start = Offset(0f, 0f),
            end = Offset(1900f, 880f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            TabSection(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Al-Qur'an",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF324851)
        )
    )
}

@Composable
fun TabSection(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Surah", "Juz")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF324851),
            contentColor = Color.White
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray
                        )
                    }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> SurahTab(navController)
            1 -> JuzTab(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF34675c)) {
        val items = listOf("Doa", "Quran", "Bookmark")
        val icons = listOf(
            R.drawable.doa,
            R.drawable.quran,
            R.drawable.solat
        )
        var selectedItem by remember { mutableStateOf(1) } // Default ke Quran (beranda)

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    when (index) {
                        0 -> navController.navigate("doa")
                        1 -> navController.navigate("home")
                        2 -> navController.navigate("bookmark")
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF03A9F4),
                    unselectedIconColor = Color(0xFFB0BEC5),
                    indicatorColor = Color(0xFF2D3033),
                    selectedTextColor = Color(0xFF2D3033),
                    unselectedTextColor = Color(0xFFB0BEC5)
                )
            )
        }
    }
}