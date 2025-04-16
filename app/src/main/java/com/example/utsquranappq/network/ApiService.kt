package com.example.utsquranappq.network

import com.example.utsquranappq.model.AyahEditionResponse
import com.example.utsquranappq.model.JuzResponse
import com.example.utsquranappq.model.SurahDetailResponse
import com.example.utsquranappq.model.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("surah")
    suspend fun getSurahList(): SurahResponse

    @GET("surah/{surahNumber}")
    suspend fun getSurah(
        @Path("surahNumber") surahNumber: Int
    ): Response<SurahDetailResponse>

    @GET("juz/{juzNumber}/{editions}")
    suspend fun getJuz(
        @Path("juzNumber") juzNumber: Int,
        @Path("editions") editions: String
    ): Response<JuzResponse>

    @GET("ayah/{reference}/editions/{editions}")
    suspend fun getAyahEditions(
        @Path("reference") reference: String,
        @Path("editions") editions: String
    ): Response<AyahEditionResponse>
    @GET("ayah/{reference}")

    suspend fun getAyahAudio(
        @Path("reference") reference: String
    ): Response<AyahAudioResponse>
}

data class AyahAudioResponse(
    val code: Int,
    val status: String,
    val data: AyahAudioData
)

data class AyahAudioData(
    val audio: String
)

