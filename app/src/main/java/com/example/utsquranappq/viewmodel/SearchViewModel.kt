package com.example.utsquranappq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = QuranRepository()

    private val _searchResults = MutableStateFlow<List<Surah>>(emptyList())
    val searchResults: StateFlow<List<Surah>> = _searchResults

    fun searchSurah(query: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSurahList()
                if (response.code == 200) {
                    val results = response.data.filter { surah ->
                        surah.name.contains(query, ignoreCase = true) ||
                                surah.englishName.contains(query, ignoreCase = true) ||
                                surah.englishNameTranslation.contains(query, ignoreCase = true)
                    }
                    _searchResults.value = results
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }    }
}