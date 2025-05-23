package alquran.latheef.jelita.model

data class SurahDetailResponse( //detail surah termasuk daftar ayatnya
    val code: Int,
    val status: String,
    val data: SurahData
)

data class SurahData(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    val ayahs: List<Ayah>
)
data class Ayah(
    val number: Int,
    val text: String,
    val numberInSurah: Int
)