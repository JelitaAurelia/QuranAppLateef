package alquran.latheef.jelita.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import alquran.latheef.jelita.model.AyahEdition
import alquran.latheef.jelita.viewmodel.JuzViewModel
import alquran.latheef.jelita.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzDetailScreen(
    juzNumber: Int?,
    navController: NavController,
    viewModel: JuzViewModel = viewModel() //mengambil data
) {
    if (juzNumber == null || juzNumber !in 1..30) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nomor Juz Tidak Valid")
        }
        return
    }

    val juzDetail by viewModel.juzDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItem >= totalItems - 2 && !isLoadingMore && viewModel.hasMoreAyahs()) {
                    viewModel.fetchJuzDetail(juzNumber)
                }
            }
    }

    LaunchedEffect(juzNumber) {
        viewModel.fetchJuzDetail(juzNumber, reset = true)
    }

    Scaffold(
        topBar = {
            TopAppBar( //tombol kembali pakai icon
                title = { Text("Juz $juzNumber") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.quran),
                            contentDescription = "Kembali",
                            tint = Color.Unspecified
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF2C3E50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF324851),
                            Color(0xFF4F6457),
                            Color(0xFFACD0C0)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error ?: "Kesalahan tidak diketahui", color = MaterialTheme.colorScheme.error)
                    }
                }
                juzDetail.isEmpty() && !isLoadingMore -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada data untuk Juz $juzNumber")
                    }
                }
                else -> {
                    val grouped = juzDetail.groupBy { it.surah.number }
                    LazyColumn(state = listState) {
                        grouped.entries.sortedBy { it.key }.forEach { (surahNumber, ayahs) ->
                            item {
                                val surahName = ayahs.firstOrNull()?.surah?.englishName ?: "Surah $surahNumber"
                                SurahCardOnlyText(
                                    surahName = surahName,
                                    ayahs = ayahs.sortedBy { it.numberInSurah }
                                )
                            }
                        }

                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahCardOnlyText(
    surahName: String,
    ayahs: List<AyahEdition>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E),
            contentColor = Color(0xFF4F6457)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Surah: $surahName",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(12.dp))

            val groupedAyahs = ayahs.groupBy { it.numberInSurah }

            groupedAyahs.entries.sortedBy { it.key }.forEach { (numberInSurah, editions) ->
                val arabicText = editions.find { it.edition.identifier == "quran-uthmani" }?.text ?: "-"
                val transliteration = editions.find { it.edition.identifier == "en.transliteration" }?.text ?: "-"
                val translation = editions.find { it.edition.identifier == "id.indonesian" }?.text ?: "-"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "$numberInSurah.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = arabicText,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 22.sp,
                            lineHeight = 44.sp,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Latin:\n$transliteration",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0))
                    )

                    Spacer(modifier = Modifier.height(11.dp))

                    Text(
                        text = "Terjemahan:\n$translation",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0))
                    )
                }

                Divider(
                    color = Color(0x33FFFFFF),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}
