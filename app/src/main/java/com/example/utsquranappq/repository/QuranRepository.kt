package com.example.utsquranappq.repository

import android.util.Log
import com.example.utsquranappq.model.AyahEditionResponse
import com.example.utsquranappq.model.JuzResponse
import com.example.utsquranappq.model.SurahDetailResponse
import com.example.utsquranappq.model.SurahResponse
import com.example.utsquranappq.network.RetrofitInstance

class QuranRepository { //perantara API dan ViewModel, menangani logika pengambilan data
    private val api = RetrofitInstance.api //memanggil fungsi dari ApiService

    //daftar surah
    suspend fun getSurahList(): SurahResponse {
        return api.getSurahList()
    }

    // Mengambil detail surah berdasarkan nomor surah
    suspend fun getSurah(surahNumber: Int): SurahDetailResponse {
        val response = api.getSurah(surahNumber)
        return if (response.isSuccessful) {
            response.body() ?: throw Exception("No data")
        } else {
            throw Exception("API Error: ${response.code()} - ${response.message()}")
        }
    }

    // Mengambil ayat dengan beberapa edisi (Arab utsmani, Latin, Indonesia)
    suspend fun getAyahEditions(reference: String, editions: String): AyahEditionResponse {
        val response = api.getAyahEditions(reference, editions)
        return if (response.isSuccessful) {
            response.body() ?: throw Exception("No data")
        } else {
            throw Exception("API Error: ${response.code()} - ${response.message()}")
        }
    }

    //mengambil ayat dalam juz
    suspend fun getJuz(juzNumber: Int, editions: String): JuzResponse {
        try {
            val response = api.getJuz(juzNumber,editions)
            return if (response.isSuccessful) {
                response.body() ?: throw Exception("No data received")
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "Error fetching Juz $juzNumber: ${e.message}")
            throw e
        }
    }

    //mengambil URL audio ayat
    suspend fun getAyahAudio(reference: String): String? {
        try {
            val response = api.getAyahAudio(reference)
            return if (response.isSuccessful) {
                response.body()?.data?.audio
            } else {
                Log.e("QuranRepository", "Error fetching audio for $reference: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "Exception fetching audio for $reference: ${e.message}")
            return null
        }
    }
}