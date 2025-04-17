package com.example.utsquranappq.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.viewmodel.SurahDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surahNumber: Int?,
    navController: NavController,
    viewModel: SurahDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    if (surahNumber == null || surahNumber <= 0) {
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

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItem >= totalItems - 2 && !isLoadingMore && viewModel.hasMoreAyahs()) {
                    viewModel.fetchSurahDetail(surahNumber)
                }
            }
    }

    LaunchedEffect(surahNumber) {
        viewModel.fetchSurahDetail(surahNumber, reset = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF324851), Color(0xFF4F6457), Color(0xFFACD0C0)
                    )
                )
            )
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detail Surah",
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.quran11),
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2C3E50),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

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
                isError = searchError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF1E1E1E),
                    focusedBorderColor = Color(0xFF1E1E1E),
                    cursorColor = Color(0xFF1E1E1E)
                )
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
                            val targetIndex = surahDetail.indexOfFirst { it.numberInSurah == ayahNumber }
                            if (targetIndex >= 0) {
                                listState.animateScrollToItem(targetIndex)
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                )
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchSurahDetail(surahNumber, reset = true) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            surahDetail.isEmpty() && !isLoadingMore -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data ayat", style = MaterialTheme.typography.bodyLarge)
                }
            }

            else -> {
                val showBismillah = surahNumber != 9 && surahDetail.any { it.numberInSurah == 1 }

                LazyColumn(state = listState) {
                    // Tampilkan Bismillah di awal jika diperlukan
                    if (showBismillah) {
                        val bismillah = surahDetail.first { it.numberInSurah == 1 && it.edition.identifier == "quran-uthmani" }
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = bismillah.text,
                                        textAlign = TextAlign.Center,
                                        fontSize = 33.sp,
                                        lineHeight = 44.sp,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontFamily = FontFamily(Font(R.font.hafs))
                                    )
                                }
                            }
                        }
                    }

                    // Tampilkan ayat-ayat
                    items(surahDetail
                        .groupBy { it.numberInSurah }
                        .filterKeys {
                            // Skip ayat 1 jika sudah ditampilkan sebagai Bismillah
                            !(showBismillah && it == 1)
                        }
                        .keys
                        .sorted()
                    ) { numberInSurah ->
                        val ayahs = surahDetail
                            .filter { it.numberInSurah == numberInSurah }

                        AyahCard(
                            ayahs = ayahs,
                            player = player,
                            numberInSurah = numberInSurah,
                            // Sesuaikan penomoran jika Bismillah ditampilkan
                            showNumber = if (showBismillah) numberInSurah - 1 else numberInSurah
                        )
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
fun AyahCard(
    ayahs: List<AyahEdition>,
    player: ExoPlayer,
    numberInSurah: Int,
    showNumber: Int
) {
    var isPlaying by remember { mutableStateOf(false) }
    val audioUrl = ayahs.find { it.edition.identifier == "ar.alafasy" }?.audio

    LaunchedEffect(isPlaying, audioUrl) {
        if (audioUrl == null) return@LaunchedEffect
        try {
            if (isPlaying) {
                player.stop()
                player.setMediaItem(MediaItem.fromUri(audioUrl))
                player.prepare()
                player.play()
            } else {
                player.stop()
            }
        } catch (e: Exception) {
            Log.e("AyahCard", "Error: ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.stop()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ayat $showNumber",
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(
                    onClick = {
                        isPlaying = !isPlaying
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Transparent, shape = CircleShape),
                    enabled = audioUrl != null
                ) {
                    val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isPlaying) "Stop" else "Play",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ayahs.forEach { ayah ->
                when (ayah.edition.identifier) {
                    "quran-uthmani" -> Text(
                        text = ayah.text,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 33.sp,
                        lineHeight = 44.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily(Font(R.font.hafs))
                    )
                    "en.transliteration" -> Text(
                        text = "Latin: ${ayah.text}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    "id.indonesian" -> Text(
                        text = "Terjemahan: ${ayah.text}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}