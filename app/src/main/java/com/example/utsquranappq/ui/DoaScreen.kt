package com.example.utsquranappq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoaScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF324851), Color(0xFF4F6457), Color(0xFFACD0C0)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Doa Harian",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val doaList = listOf(
            DoaItem(
                title = "Doa Membaca Al-Qur'an",
                arabic = "اللَّهُمَّ ارْزُقْنِي تِلَاوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ",
                transliteration = "Allahumma-rzuqnii tilaawatahu aanaa al-layli wa athroofa an-nahaar",
                translation = "Ya Allah, anugerahkanlah kepadaku rezeki untuk bisa membaca Al-Qur’an di waktu malam dan siang"
            ),
            DoaItem(
                title = "Doa Sebelum Makan",
                arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا وَقِنَا عَذَابَ النَّارِ",
                transliteration = "Allahumma barik lana fi ma razaqtana wa qina adzaban nar",
                translation = "Ya Allah, berkahilah kami dalam rezeki yang Engkau berikan dan lindungilah kami dari siksa api neraka."
            ),
            DoaItem(
                title = "Doa Sesudah Makan",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
                transliteration = "Alhamdulillahilladzi at'amana wa saqana wa-ja'alana muslimin",
                translation = "Segala puji bagi Allah yang telah memberi kami makan dan minum serta menjadikan kami muslim."
            ),
            DoaItem(
                title = "Doa Keluar Rumah",
                arabic = "بِسْمِ اللهِ تَوَكَّلْتُ عَلَى اللهِ لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللهِ",
                transliteration = "Bismillahi tawakkaltu 'alallah, la hawla wa la quwwata illa billah",
                translation = "Dengan nama Allah, aku bertawakal kepada Allah, tiada daya dan kekuatan kecuali dengan pertolongan Allah."
            ),
            DoaItem(
                title = "Doa Sebelum Tidur",
                arabic = "بِسْمِكَ االلّٰهُمَّ اَحْيَا وَبِاسْمِكَ اَمُوْتُ",
                transliteration = "Bismikallaahumma ahyaa wa amuut",
                translation = "Dengan menyebut nama Allah, aku hidup dan aku mati"
            ),
            DoaItem(
                title = "Doa Bangun Tidur",
                arabic = "اَلْحَمْدُ ِللهِ الَّذِىْ اَحْيَانَا بَعْدَمَآ اَمَاتَنَا وَاِلَيْهِ النُّشُوْرُ",
                transliteration = "Alhamdu lillahil ladzii ahyaanaa ba’da maa amaa tanaa wa ilaihin nusyuuru",
                translation = "Segala puji bagi Allah yang telah menghidupkan kami sesudah kami mati (membangunkan dari tidur) dan hanya kepada-Nya kami dikembalikan"
            ),
            DoaItem(
                title = "Doa Masuk Kamar Mandi",
                arabic = "اَللّٰهُمَّ اِنّىْ اَعُوْذُبِكَ مِنَ الْخُبُثِ وَالْخَبَآئِثِ",
                transliteration = "Allahumma Innii a'uudzubika minal khubutsi wal khobaaitsi",
                translation = "Ya Allah, aku berlindung pada-Mu dari godaan setan laki-laki dan setan perempuan"
            ),
            DoaItem(
                title = "Doa Ketika Bercermin",
                arabic = "اَلْحَمْدُ ِللهِ كَمَا حَسَّنْتَ خَلْقِىْ فَحَسِّـنْ خُلُقِىْ",
                transliteration = "Alhamdulillaahi kamaa hassanta kholqii fahassin khuluqii",
                translation = "Segala puji bagi Allah, baguskanlah budi pekertiku sebagaimana Engkau telah membaguskan rupa wajahku"
            ),
            DoaItem(
                title = "Doa Masuk Rumah",
                arabic = "اَللّٰهُمَّ اِنّىْ اَسْأَلُكَ خَيْرَالْمَوْلِجِ وَخَيْرَالْمَخْرَجِ بِسْمِ اللهِ وَلَجْنَا وَبِسْمِ اللهِ خَرَجْنَا وَعَلَى اللهِ رَبّنَا تَوَكَّلْنَا",
                transliteration = "Allahumma innii as-aluka khoirol mauliji wa khoirol makhroji bismillaahi wa lajnaa wa bismillaahi khorojnaa wa'alallohi robbina tawakkalnaa",
                translation = "Ya Allah, sesungguhnya aku mohon kepada-Mu baiknya tempat masuk dan baiknya tempat keluar..."
            ),
            DoaItem(
                title = "Doa Memohon Ilmu yang Bermanfaat",
                arabic = "اَللّٰهُمَّ اِنِّى اَسْأَلُكَ عِلْمًا نَافِعًا وَرِزْقًا طَيِّبًا وَعَمَلاً مُتَقَبَّلاً",
                transliteration = "Allahumma innii as-aluka 'ilman naafi'an wa rizqan thoyyiban wa 'amalan mutaqabbalan",
                translation = "Ya Allah, sesungguhnya aku mohon kepada-Mu ilmu yang berguna, rezki yang baik dan amal yang baik Diterima"
            ),
            DoaItem(
                title = "Doa Sebelum Belajar",
                arabic = "يَارَبِّ زِدْنِىْ عِلْمًا وَارْزُقْنِىْ فَهْمًا",
                transliteration = "Yaa robbi zidnii 'ilman warzuqnii fahmaa",
                translation = "Ya Allah, tambahkanlah aku ilmu dan berikanlah aku rizqi akan kepahaman"
            ),
            DoaItem(
                title = "Doa Sesudah Belajar",
                arabic = "اللَّهُمَّ انْفَعْنِي بِمَا عَلَّمْتَنِي وَعَلِّمْنِي مَا يَنْفَعُنِي وَزِدْنِي عِلْمًا",
                transliteration = "Allahumma infa’nii bimaa ‘allamtanii wa ‘allimnii maa yanfa’unii wa zidnii ‘ilmaa",
                translation = "Ya Allah, berilah manfaat kepadaku dari apa yang Engkau ajarkan, ajarkanlah aku apa yang bermanfaat bagiku, dan tambahkanlah aku ilmu"
            ),
            DoaItem(
                title = "Niat Wudhu",
                arabic = "نَوَيْتُ الْوُضُوْءَ لِرَفْعِ الْحَدَثِ الأَصْغَرِ فَرْضًا لِلَّهِ تَعَالَى",
                transliteration = "Nawaitul wudhu-a li raf'il hadatsil ashghari fardhan lillaahi ta'aalaa",
                translation = "Aku berniat berwudhu untuk menghilangkan hadas kecil, fardhu karena Allah Ta’ala"
            ),
            DoaItem(
                title = "Doa Setelah Wudhu",
                arabic = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ، وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ، وَاجْعَلْنِي مِنْ عِبَادِكَ الصَّالِحِينَ",
                transliteration = "Asyhadu allaa ilaaha illallaahu wahdahu laa syariikalah. Wa asyhadu anna Muhammadan ‘abduhu wa rasuuluh. Allaahumma-j‘alnii minat-tawwaabiin, waj‘alnii minal-mutaṭahhiriin, waj‘alnii min ‘ibaadikash-shaalihiin",
                translation = "Aku bersaksi bahwa tidak ada Tuhan selain Allah Yang Maha Esa, tiada sekutu bagi-Nya. Dan aku bersaksi bahwa Muhammad adalah hamba dan utusan-Nya. Ya Allah, jadikanlah aku termasuk orang-orang yang bertaubat, termasuk orang-orang yang bersuci, dan termasuk hamba-hamba-Mu yang saleh."
            ),
            DoaItem(
                title = "Doa Masuk Masjid",
                arabic = "اَللّٰهُمَّ افْتَحْ لِىْ أَبْوَابَ رَحْمَتِكَ",
                transliteration = "Allahummaf tahlii abwaaba rohmatik",
                translation = "Ya Allah, bukakanlah untukku pintu-pintu rahmat-Mu"
            ),
            DoaItem(
                title = "Doa Naik Kendaraan",
                arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
                transliteration = "Subhaanalladzii sakhkhoro lanaa haadzaa wa maa kunnaa lahu muqriniin. Wa innaa ilaa robbinaa lamunqolibuun",
                translation = "Maha Suci Allah yang telah menundukkan semua ini bagi kami, padahal kami sebelumnya tidak mampu menguasainya, dan sesungguhnya kami akan kembali kepada Tuhan kami"
            ),
            DoaItem(
                title = "Doa Memakai Pakaian",
                arabic = "اَلْـحَمْدُ ِللهِ الَّذِي كَسَانِيْ هٰذَا وَرَزَقَنِيْهِ مِنْ غَيْرِ حَوْلٍ مِنِّيْ وَلاَ قُوَّةٍ",
                transliteration = "Alhamdulillaahil ladzii kasaanii haadzaa wa rozaqoniihi min ghoiri haulin minnii wa laa quwwatin",
                translation = "Segala puji bagi Allah yang telah memberi aku pakaian ini..."
            ),
            DoaItem(
                title = "Doa Melepas Pakaian",
                arabic = "بِسْمِ اللهِ الَّذِي لاَ إِلٰهَ إِلاَّ هُوَ",
                transliteration = "Bismillaahil ladzii laa ilaaha illaa huwa",
                translation = "Dengan menyebut nama Allah yang tiada Tuhan selain Dia"
            ),
            DoaItem(
                title = "Doa Masuk WC",
                arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبْثِ وَالْخَبَائِثِ",
                transliteration = "Allahumma inni a'udzu bika minal khubutsi wal khobaaits",
                translation = "Ya Allah, aku berlindung kepada-Mu dari jin jahat laki-laki dan perempuan"
            ),
            DoaItem(
                title = "Doa Keluar WC",
                arabic = "غُفْرَانَكَ، الْحَمْدُ لِلَّهِ الَّذِي أَذْهَبَ عَنِّي الْأَذَى وَعَافَانِي",
                transliteration = "Ghufraanaka, alhamdu lillaahi allathee adhaba 'annee al-adhaa wa 'aafaanee",
                translation = "Aku memohon ampunan-Mu, segala puji bagi Allah yang telah menghilangkan mudharat dariku dan memberikan aku kesehatan."
            ),
            DoaItem(
                title = "Doa Mandi Besar",
                arabic = "نَوَيْتُ الْغُسْلَ لِرَفْعِ الْحَدَثِ الأَكْبَرِ فَرْضًا لِلَّهِ تَعَالَى",
                transliteration = "Nawaitul ghusla lirof'il hadatsil akbari fardhon lillaahi ta'aala",
                translation = "Aku berniat mandi besar untuk menghilangkan hadats besar fardhu karena Allah Ta’ala"
            )
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(doaList) { doa ->
                DoaCard(doa)
            }
        }
    }
}

data class DoaItem(
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String
)

@Composable
fun DoaCard(doa: DoaItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = doa.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = doa.arabic,
                fontSize = 20.sp,
                color = Color(0xFF00796B), // Aksen ungu
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Latin: ${doa.transliteration}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Terjemahan: ${doa.translation}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

