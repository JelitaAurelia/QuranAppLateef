package com.example.utsquranappq.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.viewmodel.JuzViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzDetailScreen(
    juzNumber: Int?,
    navController: NavController,
    viewModel: JuzViewModel = viewModel()
) {
    if (juzNumber == null || juzNumber !in 1..30) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Invalid Juz Number")
        }
        return
    }

    val juzDetail by viewModel.juzDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    LaunchedEffect(juzNumber) {
        Log.d("JuzDetailScreen", "Fetching Juz $juzNumber")
        viewModel.fetchJuzDetail(juzNumber)
    }

    LaunchedEffect(juzDetail) {
        Log.d("JuzDetailScreen", "JuzDetail size: ${juzDetail.size}")
        juzDetail.forEach {
            Log.d("JuzDetailScreen", "Ayat ${it.numberInSurah}: ${it.text}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juz $juzNumber") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.quran),
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Log.d("JuzDetailScreen", "Loading Juz $juzNumber")
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Log.e("JuzDetailScreen", "Error: $error")
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                    }
                }

                juzDetail.isEmpty() -> {
                    Log.w("JuzDetailScreen", "No data available for Juz $juzNumber")
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available for Juz $juzNumber")
                    }
                }

                else -> {
                    Log.d("JuzDetailScreen", "Rendering Juz $juzNumber with ${juzDetail.size} ayahs")
                    val grouped = juzDetail.groupBy { it.surah.number }

                    LazyColumn {
                        items(grouped.entries.toList()) { (surahNumber, ayahs) ->
                            val surahName = ayahs.firstOrNull()?.surah?.englishName ?: "Surah $surahNumber"
                            Log.d("JuzDetailScreen", "Rendering Surah $surahNumber - $surahName with ${ayahs.size} ayat")
                            SurahCardOnlyText(
                                surahName = surahName,
                                ayahs = ayahs,
                                player = player
                            )
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
    ayahs: List<AyahEdition>,
    player: ExoPlayer
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0218),
            contentColor = Color(0xFFAA9AAB)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Surah: $surahName",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val groupedAyahs = ayahs.groupBy { it.numberInSurah }

            groupedAyahs.forEach { (numberInSurah, editions) ->
                val arabicText = editions.find { it.edition.identifier == "quran-uthmani" }?.text ?: "-"
                val transliteration = editions.find { it.edition.identifier == "en.transliteration" }?.text ?: "-"
                val translation = editions.find { it.edition.identifier == "id.indonesian" }?.text ?: "-"
                val audioUrl = editions.find { it.edition.identifier == "quran-uthmani" }?.audioUrl

                var isPlaying by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$numberInSurah. $arabicText",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, color = Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔤 Transliterasi: $transliteration",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🌍 Terjemahan: $translation",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0))
                        )
                    }
                    if (audioUrl != null) {
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    player.stop()
                                    isPlaying = false
                                } else {
                                    player.setMediaItem(MediaItem.fromUri(audioUrl))
                                    player.prepare()
                                    player.play()
                                    isPlaying = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                                ),
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF00796B)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}