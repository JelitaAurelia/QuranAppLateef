package com.example.utsquranappq.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.viewmodel.SurahDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun SurahDetailScreen(
    surahNumber: Int?,
    navController: NavController,
    viewModel: SurahDetailViewModel = viewModel()
) {

    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Log.d("SurahDetailScreen", "Layar dimuat dengan surahNumber: $surahNumber")

    if (surahNumber == null || surahNumber <= 0) {
        Log.e("SurahDetailScreen", "SurahNumber tidak valid, menampilkan UI kosong")
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Surah tidak valid", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val surahDetail by viewModel.surahDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var searchAyah by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf<String?>(null) }

    // Logika infinite scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItem >= totalItems - 2 && !isLoadingMore && viewModel.hasMoreAyahs()) {
                    Log.d("SurahDetailScreen", "Memuat lebih banyak ayat")
                    viewModel.fetchSurahDetail(surahNumber)
                }
            }
    }

    LaunchedEffect(surahNumber) {
        Log.d("SurahDetailScreen", "Mengambil data awal untuk surahNumber: $surahNumber")
        viewModel.fetchSurahDetail(surahNumber, reset = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF324851), Color(0xFF4F6457), Color(0xFFACD0C0))
                )
            )
    ) {

    // Kolom pencarian
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchAyah,
                onValueChange = {
                    searchAyah = it
                    searchError = null
                },
                label = { Text("Cari ayat") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                isError = searchError != null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val ayahNumber = searchAyah.toIntOrNull()
                    if (ayahNumber == null || ayahNumber <= 0) {
                        searchError = "Masukkan nomor ayat yang valid"
                    } else if (ayahNumber > viewModel.getTotalAyahs() && viewModel.getTotalAyahs() > 0) {
                        searchError = "Ayat $ayahNumber tidak ada di Surah ini"
                    } else {
                        viewModel.fetchSurahDetail(surahNumber, targetAyah = ayahNumber)
                        coroutineScope.launch {
                            // Cari indeks ayat di daftar
                            val targetIndex = surahDetail.indexOfFirst { it.numberInSurah == ayahNumber }
                            if (targetIndex >= 0) {
                                listState.animateScrollToItem(targetIndex)
                            }
                        }
                    }
                }
            ) {
                Text("Cari")
            }
        }
        if (searchError != null) {
            Text(
                text = searchError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        when {
            isLoading -> {
                Log.d("SurahDetailScreen", "Memuat data...")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Log.e("SurahDetailScreen", "Kesalahan: $errorMessage")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchSurahDetail(surahNumber, reset = true) }
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            surahDetail.isEmpty() && !isLoadingMore -> {
                Log.w("SurahDetailScreen", "Tidak ada ayat untuk Surah ini")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data ayat", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                Log.d("SurahDetailScreen", "Menampilkan ayat")
                LazyColumn(state = listState) {
                    items(surahDetail.groupBy { it.numberInSurah }.keys.toList()) { numberInSurah ->
                        val ayahs = surahDetail.filter { it.numberInSurah == numberInSurah }
                        AyahCard(ayahs)
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
@Composable
fun AyahCard(ayahs: List<AyahEdition>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ayahs.forEach { ayah ->
                when (ayah.edition.identifier) {
                    "quran-uthmani" -> {
                        Text(
                            text = "${ayah.numberInSurah}. ${ayah.text}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.hafs))
                        )
                    }
                    "en.transliteration" -> {
                        Text(
                            text = "Latin: ${ayah.text}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    "id.indonesian" -> {
                        Text(
                            text = "Terjemahan: ${ayah.text}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}