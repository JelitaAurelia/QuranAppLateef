package alquran.latheef.jelita.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import alquran.latheef.jelita.model.AyahEdition
import alquran.latheef.jelita.repository.QuranRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuzViewModel : ViewModel() { //mengelola data dan logika untuk layar juz detail
    private val repository = QuranRepository()

    // State untuk daftar ayat
    private val _juzDetail = MutableStateFlow<List<AyahEdition>>(emptyList())
    val juzDetail: StateFlow<List<AyahEdition>> = _juzDetail

    // State untuk status loading
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State untuk loading batch berikutnya
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    // State untuk error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Edisi yang diambil, termasuk audio
    private val editions = "quran-uthmani,en.transliteration,id.indonesian"

    // Variabel untuk pemuatan bertahap
    private var currentPage = 0
    private val pageSize = 11
    private var totalAyahs = 0
    private var juzNumberCache = 0
    private val loadedPages = mutableSetOf<Int>()

    // Fungsi untuk ambil ayat Juz, 11 ayat per batch
    fun fetchJuzDetail(juzNumber: Int, reset: Boolean = false) {
        // Reset data jika Juz berubah atau reset diminta
        if (juzNumber != juzNumberCache || reset) {
            currentPage = 0
            _juzDetail.value = emptyList()
            juzNumberCache = juzNumber
            totalAyahs = 0
            loadedPages.clear()
            Log.d("JuzViewModel", "Reset data untuk Juz $juzNumber")
        }

        viewModelScope.launch {
            try {
                // Set status loading
                if (currentPage == 0) _isLoading.value = true else _isLoadingMore.value = true
                _error.value = null

                // Ambil total ayat jika belum ada
                if (totalAyahs == 0 || reset) {
                    val response = repository.getJuz(juzNumber, editions)
                    if (response.code == 200) {
                        totalAyahs = response.data.ayahs.size
                        Log.d("JuzViewModel", "Total ayat di Juz $juzNumber: $totalAyahs")
                    } else {
                        _error.value = "Gagal mengambil data Juz: ${response.status}"
                        Log.e("JuzViewModel", "Error kode: ${response.code}")
                        return@launch
                    }
                }

                // Hitung rentang ayat untuk batch
                val startAyah = currentPage * pageSize
                val endAyah = minOf(startAyah + pageSize - 1, totalAyahs - 1)
                if (startAyah >= totalAyahs) {
                    Log.d("JuzViewModel", "Tidak ada lagi ayat untuk Juz $juzNumber")
                    return@launch
                }

                // Cegah muat ulang halaman yang sama
                if (loadedPages.contains(currentPage)) {
                    Log.d("JuzViewModel", "Halaman $currentPage sudah dimuat")
                    return@launch
                }

                // Ambil ayat untuk batch
                val ayahList = mutableListOf<AyahEdition>()
                val response = repository.getJuz(juzNumber, editions)
                if (response.code == 200) {
                    val ayahs = response.data.ayahs.slice(startAyah..endAyah)
                    Log.d("JuzViewModel", "Muat ayat dari index $startAyah sampai $endAyah")
                    for (ayah in ayahs) {
                        val reference = "${ayah.surah.number}:${ayah.numberInSurah}"
                        try {
                            val editionsResponse = repository.getAyahEditions(reference, editions)
                            if (editionsResponse.code == 200) {
                                ayahList.addAll(editionsResponse.data)
                                Log.d("JuzViewModel", "Berhasil ambil edisi untuk ayat $reference")
                            } else {
                                Log.w("JuzViewModel", "Gagal ambil edisi $reference, code: ${editionsResponse.code}")
                            }
                        } catch (e: Exception) {
                            Log.e("JuzViewModel", "Error edisi $reference: ${e.message}")
                        }
                        delay(44L) // Delay untuk hindari error 429
                    }

                    // Gabungkan ayat baru dengan yang sudah ada, urutkan berdasarkan surah dan numberInSurah
                    val currentList = _juzDetail.value.toMutableList()
                    currentList.addAll(ayahList)
                    _juzDetail.value = currentList.sortedWith(
                        compareBy({ it.surah.number }, { it.numberInSurah })
                    )
                    loadedPages.add(currentPage)
                    currentPage++
                    Log.d("JuzViewModel", "Muat ${ayahList.size} edisi ayat, total ayat sekarang: ${currentList.size}")
                } else {
                    _error.value = "Gagal mengambil data Juz: ${response.status}"
                    Log.e("JuzViewModel", "Error kode: ${response.code}")
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("JuzViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    // Cek apakah masih ada ayat yang bisa dimuat
    fun hasMoreAyahs(): Boolean {
        return (currentPage * pageSize) < totalAyahs
    }
}