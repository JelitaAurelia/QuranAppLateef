package com.example.utsquranappq.ui
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.utiils.getTranslation


@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar() }, // Pass navController
    ) { padding ->
        // Membuat brush dengan gradien 3 warna
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF324851), Color(0xFF4F6457), Color(0xFF324851)),
            start = Offset(0f, 0f),
            end = Offset(1900f, 880f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)  // Menggunakan gradien sebagai background
                .padding(padding)
        ) {
            GreetingSection()
            TabSection(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Al- Qur'an", fontSize = 20.sp, fontWeight = FontWeight.Bold,color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF324851))
    )
}

@Composable
fun GreetingSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Ini yang membuat semua child di tengah
    ) {
        Text(
            text = "السَّلاَمُ عَلَيْكُمْ",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.hafs)),
            color = Color.White,
            textAlign = TextAlign.Center, // Untuk memastikan teks arab rata tengah
            modifier = Modifier.fillMaxWidth() // Agar textAlign bekerja optimal
        )
        Image(
            painter = painterResource(id = R.drawable.quran11),
            contentDescription = "Greeting Icon",
            modifier = Modifier
                .size(125.dp)
                .padding(top = 0.dp)
        )

    }
}

@Composable
fun SearchSection(searchQuery: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Cari Surah...") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1E1E1E),
            unfocusedContainerColor = Color(0xFF1E1E1E),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
fun SearchResults(surahs: List<Surah>, navController: NavController) {
    if (surahs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Tidak ada hasil ditemukan", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(surahs) { surah ->
                SurahSearchCard(surah, succeeds = { navController.navigate("surahDetail/${surah.number}") })
            }
        }
    }
}

@Composable
fun SurahSearchCard(surah: Surah, succeeds: () -> Unit) {
    val (surahIndo, meaningIndo, revelationIndo) = getTranslation(
        surah.englishName,
        surah.englishNameTranslation,
        surah.revelationType
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { succeeds() },
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
                    text = "${surah.number}. $surahIndo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = meaningIndo,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = surah.name,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "$revelationIndo - ${surah.numberOfAyahs} Ayat",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun TabSection(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Surah", "Juz")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF324851), // Warna latar TabRow
            contentColor = Color.White // Warna indikator tab terpilih
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
        val items = listOf("Jadwal Solat", "Quran", "Doa")
        val icons = listOf(
            R.drawable.solat, // Ganti dengan ikon jadwal solat
            R.drawable.quran,
            R.drawable.doa
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
                        0 -> navController.navigate("jadwalSolat")
                        1 -> navController.navigate("home")
                        2 -> navController.navigate("doa")
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

