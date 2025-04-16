package com.example.utsquranappq.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.utiils.getTranslation
import com.example.utsquranappq.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchSurah(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Surah atau Ayat...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Results
        if (searchQuery.isEmpty()) {
            Text("Masukkan kata kunci untuk mencari", color = Color.Gray)
        } else if (searchResults.isEmpty()) {
            Text("Tidak ada hasil ditemukan", color = Color.Gray)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { surah ->
                    SurahSearchCard(surah, navController)
                }
            }
        }
    }
}

@Composable
fun SurahSearchCard(surah: Surah, navController: NavController) {
    val (surahIndo, meaningIndo, revelationIndo) = getTranslation(
        surah.englishName,
        surah.englishNameTranslation,
        surah.revelationType
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("surahDetail/${surah.number}") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
                horizontalAlignment = androidx.compose.ui.Alignment.End
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