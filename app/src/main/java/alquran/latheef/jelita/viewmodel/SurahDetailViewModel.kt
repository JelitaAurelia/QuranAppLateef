package alquran.latheef.jelita.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import alquran.latheef.jelita.model.AyahEdition
import alquran.latheef.jelita.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class SurahDetailViewModel : ViewModel() { //mengelola data dan logika untuk layar surah detail
    private val repository = QuranRepository()

    private val _surahDetail = MutableStateFlow<List<AyahEdition>>(emptyList())
    val surahDetail: StateFlow<List<AyahEdition>> = _surahDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentPage = 0
    private val pageSize = 10 //pagination
    private var totalAyahs = 0
    private var surahNumberCache = 0
    private val loadedPages = mutableSetOf<Int>()

    //mengambil ayat ayat untuk surah tertentu
    fun fetchSurahDetail(surahNumber: Int, reset: Boolean = false, targetAyah: Int? = null) {
        if (surahNumber != surahNumberCache || reset) {
            currentPage = 0
            _surahDetail.value = emptyList()
            surahNumberCache = surahNumber
            totalAyahs = 0
            loadedPages.clear()
        }

        viewModelScope.launch {
            try {
                if (currentPage == 0 && targetAyah == null) {
                    _isLoading.value = true
                } else {
                    _isLoadingMore.value = true
                }
                _error.value = null

                // Tambahkan ar.alafasy ke editions
                val editions = "quran-uthmani,en.transliteration,id.indonesian,ar.alafasy" //berisi daftar edisi yang akan diminta dari API Al-Qur'an. Edisi ini ditentukan oleh ID unik
                val ayahList = mutableListOf<AyahEdition>()

                if (totalAyahs == 0 || reset) {
                    val surahResponse = repository.getSurah(surahNumber)
                    if (surahResponse.code != 200) {
                        _error.value = "Gagal memuat surah: ${surahResponse.status}"
                        return@launch
                    }
                    totalAyahs = surahResponse.data.ayahs.size
                }

                val pageToLoad = if (targetAyah != null) {
                    maxOf(0, ceil(targetAyah.toDouble() / pageSize).toInt() - 1)
                } else {
                    currentPage
                }

                if (loadedPages.contains(pageToLoad)) {
                    _isLoadingMore.value = false
                    return@launch
                }

                val startAyah = pageToLoad * pageSize + 1
                val endAyah = minOf(startAyah + pageSize - 1, totalAyahs)

                if (startAyah > totalAyahs) {
                    return@launch
                }

                val surahResponse = repository.getSurah(surahNumber)
                if (surahResponse.code == 200) {
                    val ayahs = surahResponse.data.ayahs
                        .filter { it.numberInSurah in startAyah..endAyah }

                    ayahs.forEach { ayah ->
                        val reference = "$surahNumber:${ayah.numberInSurah}"
                        val response = repository.getAyahEditions(reference, editions)
                        if (response.code == 200) {
                            ayahList.addAll(response.data)
                        }
                    }

                    val currentList = _surahDetail.value.toMutableList()
                    currentList.addAll(ayahList)
                    _surahDetail.value = currentList.sortedBy { it.numberInSurah }
                    loadedPages.add(pageToLoad)
                    currentPage = maxOf(currentPage, pageToLoad + 1)
                    Log.d("SurahDetailViewModel", "Berhasil mengambil ${ayahList.size} edisi ayat untuk halaman ${pageToLoad + 1}")
                } else {
                    _error.value = "Gagal memuat surah: ${surahResponse.status}"
                }
            } catch (e: Exception) {
                _error.value = "Kesalahan: ${e.message}"
                Log.e("SurahDetailViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun hasMoreAyahs(): Boolean { //buat atur
        return (currentPage * pageSize) < totalAyahs
    }

    fun getTotalAyahs(): Int {
        return totalAyahs
    }
}