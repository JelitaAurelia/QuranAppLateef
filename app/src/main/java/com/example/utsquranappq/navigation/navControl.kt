package com.example.utsquranappq.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utsquranappq.ui.BookmarkScreen
import com.example.utsquranappq.ui.BottomNavigationBar
import com.example.utsquranappq.ui.DoaScreen
import com.example.utsquranappq.ui.HomeScreen
import com.example.utsquranappq.ui.JuzDetailScreen
import com.example.utsquranappq.ui.SurahDetailScreen
import com.example.utsquranappq.ui.SurahTab

class HomeScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("bookmark") { BookmarkScreen(navController) } // Sekarang menampilkan Doa
                    composable("doa") { DoaScreen() } // Sekarang menampilkan Jadwal Solat
                    composable("surahTab") { SurahTab(navController) }
                    composable("surahDetail/{surahNumber}") { backStackEntry ->
                        val surahNumber = backStackEntry.arguments?.getString("surahNumber")?.toIntOrNull()
                        val ayahNumber = backStackEntry.arguments?.getString("ayahNumber")?.toIntOrNull()
                        Log.d("Navigation", "Navigasi ke SurahDetailScreen dengan nomor: $surahNumber")
                        SurahDetailScreen(surahNumber, navController)
                    }
                    composable("juzDetail/{juzNumber}") { backStackEntry ->
                        val juzNumber = backStackEntry.arguments?.getString("juzNumber")?.toIntOrNull()
                        Log.d("Navigation", "Navigasi ke JuzDetailScreen dengan nomor: $juzNumber")
                        JuzDetailScreen(juzNumber, navController)
                    }
                }
            }
        }
    }
}