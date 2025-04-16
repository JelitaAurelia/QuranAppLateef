package com.example.utsquranappq.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.repository.QuranRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuzViewModel : ViewModel() {
    private val repository = QuranRepository()

    private val _juzDetail = MutableStateFlow<List<AyahEdition>>(emptyList())
    val juzDetail: StateFlow<List<AyahEdition>> = _juzDetail

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val editions = "quran-uthmani,en.transliteration,id.indonesian"

    fun fetchJuzDetail(juzNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.getJuz(juzNumber, editions)
                if (response.code == 200) {
                    val ayahList = mutableListOf<AyahEdition>()
                    val ayahs = response.data.ayahs
                    Log.d("JuzViewModel", "Jumlah ayat di Juz $juzNumber: ${ayahs.size}")

                    for (ayah in ayahs) {
                        val reference = "${ayah.surah.number}:${ayah.numberInSurah}"
                        try {
                            val editionsResponse = repository.getAyahEditions(reference, editions)

                            if (editionsResponse.code == 200) {
                                ayahList.addAll(editionsResponse.data)
                            } else {
                                Log.w("JuzViewModel", "Gagal ambil edisi untuk ayat $reference, code: ${editionsResponse.code}")
                            }
                        } catch (e: Exception) {
                            Log.e("JuzViewModel", "Exception saat ambil edisi ayat $reference: ${e.message}")
                        }

                        // Delay antar request biar gak 429
                        delay(300L) // 300ms jeda → 3 request per detik
                    }

                    _juzDetail.value = ayahList
                } else {
                    _error.value = "Gagal mengambil data Juz: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("JuzViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
