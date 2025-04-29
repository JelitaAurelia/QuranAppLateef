package com.example.quranapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SurahViewModel : ViewModel() { //mengelola data untuk tabsurah
    private val repository = QuranRepository()

    private val _surahList = MutableStateFlow<List<Surah>>(emptyList())
    val surahList: StateFlow<List<Surah>> = _surahList

    init {
        fetchSurahList()
    }
    //mengambil daftar semua surah saat vm diinisialisasi
    private fun fetchSurahList() {
        viewModelScope.launch {
            try {
                val response = repository.getSurahList()
                _surahList.value = response.data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
