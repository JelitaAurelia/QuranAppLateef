package com.example.utsquranappq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoaScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF324851), Color(0xFF4F6457), Color(0xFFACD0C0)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Doa Harian",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val doaList = listOf(
            DoaItem(
                title = "Doa Sebelum Makan",
                arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا وَقِنَا عَذَابَ النَّارِ",
                transliteration = "Allahumma barik lana fi ma razaqtana wa qina adzaban nar",
                translation = "Ya Allah, berkahilah kami dalam rezeki yang Engkau berikan dan lindungilah kami dari siksa api neraka."
            ),
            DoaItem(
                title = "Doa Sesudah Makan",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
                transliteration = "Alhamdulillahilladzi at'amana wa saqana wa-ja'alana muslimin",
                translation = "Segala puji bagi Allah yang telah memberi kami makan dan minum serta menjadikan kami muslim."
            ),
            DoaItem(
                title = "Doa Keluar Rumah",
                arabic = "بِسْمِ اللهِ تَوَكَّلْتُ عَلَى اللهِ لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللهِ",
                transliteration = "Bismillahi tawakkaltu 'alallah, la hawla wa la quwwata illa billah",
                translation = "Dengan nama Allah, aku bertawakal kepada Allah, tiada daya dan kekuatan kecuali dengan pertolongan Allah."
            )
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(doaList) { doa ->
                DoaCard(doa)
            }
        }
    }
}

data class DoaItem(
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String
)

@Composable
fun DoaCard(doa: DoaItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = doa.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = doa.arabic,
                fontSize = 20.sp,
                color = Color(0xFF00796B), // Aksen ungu
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Latin: ${doa.transliteration}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Terjemahan: ${doa.translation}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

