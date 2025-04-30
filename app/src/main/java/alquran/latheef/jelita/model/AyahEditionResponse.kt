package alquran.latheef.jelita.model

data class AyahEditionResponse(
    val code: Int,
    val status: String,
    val data: List<AyahEdition>
)

data class AyahEdition( //representasi satu ayat
    val number: Int,
    val text: String,
    val surah: SurahInfo,
    val numberInSurah: Int,
    val juz: Int,
    val edition: Edition,
    val audio: String? = null
)

data class SurahInfo(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
)

data class Edition(
    val identifier: String,
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String
)

