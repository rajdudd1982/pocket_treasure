@file:Suppress("UNCHECKED_CAST")

package com.stavro_xhardha.pockettreasure.brain

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import com.stavro_xhardha.pockettreasure.BuildConfig
import com.stavro_xhardha.pockettreasure.model.*
import com.sxhardha.smoothie.Smoothie

val isDebugMode: Boolean = BuildConfig.DEBUG

fun buildPagedList() = PagedList.Config.Builder()
    .setPageSize(INITIAL_PAGE_SIZE)
    .setEnablePlaceholders(false)
    .build()

val DIFF_UTIL_AYA = object : DiffUtil.ItemCallback<Aya>() {
    override fun areItemsTheSame(oldItem: Aya, newItem: Aya): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Aya, newItem: Aya): Boolean =
        oldItem.ayatText == newItem.ayatText
                && oldItem.audioUrl == newItem.audioUrl
                && oldItem.id == newItem.id

}

val DIFF_UTIL_GALLERY = object : DiffUtil.ItemCallback<UnsplashResult>() {
    override fun areItemsTheSame(oldItem: UnsplashResult, newItem: UnsplashResult): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UnsplashResult, newItem: UnsplashResult): Boolean =
        oldItem.id == newItem.id && oldItem.description == newItem.description
                && oldItem.altDescription == newItem.description
                && oldItem.photoUrls == newItem.photoUrls
}

val DIFF_UTIL_HOME = object : DiffUtil.ItemCallback<HomePrayerTime>() {
    override fun areItemsTheSame(oldItem: HomePrayerTime, newItem: HomePrayerTime): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: HomePrayerTime, newItem: HomePrayerTime): Boolean =
        oldItem.name == newItem.name && oldItem.time == newItem.time
}

val DIFF_UTIL_NAMES = object : DiffUtil.ItemCallback<Name>() {
    override fun areItemsTheSame(oldItem: Name, newItem: Name): Boolean =
        oldItem.arabicName == newItem.arabicName

    override fun areContentsTheSame(oldItem: Name, newItem: Name): Boolean {
        return oldItem.arabicName == newItem.arabicName
                && oldItem.englishNameMeaning == newItem.englishNameMeaning
                && oldItem.number == newItem.number
                && oldItem.transliteration == newItem.transliteration
    }
}

val DIFF_UTIL_QURAN = object : DiffUtil.ItemCallback<Surah>() {
    override fun areItemsTheSame(oldItem: Surah, newItem: Surah): Boolean =
        oldItem.surahNumber == newItem.surahNumber

    override fun areContentsTheSame(oldItem: Surah, newItem: Surah): Boolean =
        oldItem.englishName == newItem.englishName
                && oldItem.englishTranslation == newItem.englishTranslation
                && oldItem.surahArabicName == newItem.surahArabicName
                && oldItem.revelationType == newItem.revelationType
                && oldItem.surahNumber == newItem.surahNumber

}

val DIFF_UTIL_TASBEEH = object : DiffUtil.ItemCallback<Tasbeeh>() {
    override fun areItemsTheSame(oldItem: Tasbeeh, newItem: Tasbeeh): Boolean =
        oldItem.arabicPhrase == newItem.arabicPhrase

    override fun areContentsTheSame(oldItem: Tasbeeh, newItem: Tasbeeh): Boolean =
        oldItem.arabicPhrase == newItem.arabicPhrase
                && oldItem.translation == newItem.translation
                && oldItem.transliteration == newItem.transliteration

}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}

fun incrementIdlingResource() {
    if (isDebugMode)
        Smoothie.startProcess()
}

fun decrementIdlingResource() {
    if (isDebugMode)
        Smoothie.endProcess()
}

inline fun <reified T : ViewModel> Fragment.viewModel(
    crossinline provider: (SavedStateHandle) -> T
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, Bundle()) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T =
            provider(handle) as T
    }
}