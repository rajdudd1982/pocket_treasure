package com.sxhardha.quran_module.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SurahResponse(
    @Json(name ="surahs")
    val surahs: List<Surah>
)