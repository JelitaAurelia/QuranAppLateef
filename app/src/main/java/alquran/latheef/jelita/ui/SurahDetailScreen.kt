package alquran.latheef.jelita.ui

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
import alquran.latheef.jelita.model.AyahEdition
import alquran.latheef.jelita.viewmodel.SurahDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import alquran.latheef.jelita.R

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
    var searchTargetAyah by remember { mutableStateOf<Int?>(null) }

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

    // Normalisasi teks untuk menangani variasi tanda baca
    fun normalizeText(text: String): String {
        return text.replace(Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u06D6-\\u06DC\\u06DF-\\u06E8\\u06EA-\\u06ED]"), "").trim()
    }

    // Filter Bismillah kecuali untuk Surah 1 (Al-Fatihah)
    val bismillahText = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
    val normalizedBismillah = normalizeText(bismillahText)
    val filteredSurahDetail = remember(surahDetail, surahNumber) {
        if (surahNumber != 1 && surahNumber != 9) {
            surahDetail.map { ayah ->
                if (ayah.edition.identifier == "quran-uthmani" && normalizeText(ayah.text).contains(normalizedBismillah)) {
                    // Hapus Bismillah dan simpan teks ayat
                    val cleanedText = ayah.text.replace(bismillahText, "").trim()
                    if (cleanedText.isNotBlank()) {
                        ayah.copy(text = cleanedText)
                    } else {
                        null
                    }
                } else {
                    ayah
                }
            }.filterNotNull()
        } else {
            surahDetail // Tidak ada filter untuk Surah 1 dan Surah 9
        }
    }


    // Scroll ke ayat yang dicari
    LaunchedEffect(filteredSurahDetail, searchTargetAyah, isLoadingMore) {
        searchTargetAyah?.let { ayahNumber ->
            while (isLoadingMore) {
                delay(100)
            }
            val groupedAyahs = filteredSurahDetail.groupBy { it.numberInSurah }
            val targetIndex = groupedAyahs.keys.sorted().indexOf(ayahNumber)
            if (targetIndex >= 0) {
                val finalIndex = if (surahNumber != 1 && surahNumber != 9) targetIndex + 1 else targetIndex
                coroutineScope.launch {
                    listState.animateScrollToItem(finalIndex)
                }
                searchTargetAyah = null
            } else if (viewModel.hasMoreAyahs() && !isLoadingMore) {
                viewModel.fetchSurahDetail(surahNumber, targetAyah = ayahNumber)
            } else {
                searchError = "Ayat $ayahNumber tidak ditemukan"
                searchTargetAyah = null
            }
        }
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
                Text(
                    text = "Detail Surah",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
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
                    focusedBorderColor = Color(0xFF2C3E50),
                    unfocusedBorderColor = Color(0xFF2C3E50),
                    cursorColor = Color(0xFF2C3E50)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val ayahNumber = searchAyah.toIntOrNull()
                    if (ayahNumber == null || ayahNumber <= 0) {
                        searchError = "Masukkan nomor ayat yang valid"
                    } else if (viewModel.getTotalAyahs() > 0 && ayahNumber > viewModel.getTotalAyahs()) {
                        searchError = "Ayat $ayahNumber tidak ada di Surah ini"
                    } else {
                        searchTargetAyah = ayahNumber
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

            filteredSurahDetail.isEmpty() && !isLoadingMore -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data ayat", style = MaterialTheme.typography.bodyLarge)
                }
            }

            else -> {
                LazyColumn(state = listState) {
                    if (surahNumber != 1 && surahNumber != 9) {
                        item {
                            BismillahHeader()
                        }
                    }

                    items(
                        filteredSurahDetail
                            .groupBy { it.numberInSurah }
                            .filterKeys { it >= 1 }
                            .keys
                            .sorted()
                    ) { numberInSurah ->
                        val ayahs = filteredSurahDetail.filter { it.numberInSurah == numberInSurah }
                        if (ayahs.isNotEmpty()) {
                            AyahCard(
                                ayahs = ayahs,
                                player = player,
                                numberInSurah = numberInSurah,
                                surahNumber = surahNumber
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

@Composable
fun BismillahHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 33.sp,
                lineHeight = 44.sp,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.hafs))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AyahCard(
    ayahs: List<AyahEdition>,
    player: ExoPlayer,
    numberInSurah: Int,
    surahNumber: Int
) {
    var isBookmarked by remember { mutableStateOf(BookmarkManager.getBookmarks().contains(Bookmark(surahNumber, numberInSurah))) }
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
                    text = "Ayat $numberInSurah",
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

                IconButton(
                    onClick = {
                        try {
                            val bookmark = Bookmark(surahNumber, numberInSurah)
                            if (isBookmarked) {
                                BookmarkManager.removeBookmark(bookmark)
                            } else {
                                BookmarkManager.addBookmark(bookmark)
                            }
                            isBookmarked = !isBookmarked
                        } catch (e: Exception) {
                            Log.e("AyahCard", "Error handling bookmark: ${e.message}")
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Transparent, shape = CircleShape)
                ) {
                    val iconRes = if (isBookmarked) R.drawable.filled else R.drawable.empty
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isBookmarked) "Hapus Bookmark" else "Tambah Bookmark",
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