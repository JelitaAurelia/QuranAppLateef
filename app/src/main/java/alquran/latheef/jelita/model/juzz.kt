package alquran.latheef.jelita.model

data class JuzResponse(
    val code: Int,
    val status: String,
    val data: JuzData
)

data class JuzData(
    val number: Int,
    val ayahs: List<AyahEdition>
)