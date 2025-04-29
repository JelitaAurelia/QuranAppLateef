package com.example.utsquranappq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.viewmodel.SurahViewModel
import com.example.utsquranappq.utiils.getTranslation

@Composable
fun BookmarkScreen(
    navController: NavController,
    surahViewModel: SurahViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        BookmarkManager.initialize(context)
    }

    val bookmarks = remember { mutableStateListOf<Bookmark>().apply { addAll(BookmarkManager.getBookmarks()) } }
    val surahList by surahViewModel.surahList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF324851), Color(0xFF4F6457), Color(0xFFACD0C0))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bookmark",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (bookmarks.isEmpty()) {
                Text(
                    text = "Belum ada bookmark",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookmarks) { bookmark ->
                        val surah = surahList.find { it.number == bookmark.surahNumber }
                        if (surah != null) {
                            val (namaSurah, _, _) = getTranslation(
                                surah.englishName,
                                surah.englishNameTranslation,
                                surah.revelationType
                            )
                            BookmarkCard(
                                bookmark = bookmark,
                                surahName = namaSurah,
                                onClick = {
                                    navController.navigate("surahDetail/${bookmark.surahNumber}?ayah=${bookmark.ayahNumber}")
                                },
                                onRemove = {
                                    BookmarkManager.removeBookmark(bookmark)
                                    bookmarks.remove(bookmark)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Bookmark(val surahNumber: Int, val ayahNumber: Int)

@Composable
fun BookmarkCard(
    bookmark: Bookmark,
    surahName: String,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
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
                    text = "$surahName - Ayat ${bookmark.ayahNumber}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            IconButton(
                onClick = { onRemove() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.utsquranappq.R.drawable.delete),
                    contentDescription = "Hapus Bookmark",
                    tint = Color(0xFF00796B)
                )
            }
        }
    }
}