package com.stavro_xhardha.pockettreasure.ui.quran

import android.view.View
import androidx.lifecycle.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.stavro_xhardha.pockettreasure.brain.decrementIdlingResource
import com.stavro_xhardha.pockettreasure.brain.incrementIdlingResource
import com.stavro_xhardha.core_module.core_dependencies.AppCoroutineDispatchers
import com.stavro_xhardha.core_module.model.Surah
import com.stavro_xhardha.core_module.core_dependencies.SurahsDao
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class QuranViewModel @AssistedInject constructor(
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
    private val surahsDao: SurahsDao,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): QuranViewModel
    }

    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            showError()
        }

    private val _surahs: MutableLiveData<List<Surah>> = MutableLiveData()
    private val _errorVisibility: MutableLiveData<Int> = MutableLiveData()
    private val _listVisibility: MutableLiveData<Int> = MutableLiveData()
    private val _progressVisibility: MutableLiveData<Int> = MutableLiveData()

    val surahs: LiveData<List<Surah>> = _surahs
    val errorVisibility: LiveData<Int> = _errorVisibility
    val listVisibility: LiveData<Int> = _listVisibility
    val progressVisibility: LiveData<Int> = _progressVisibility

    init {
        showProgress()
    }

    fun startQuranDatabaseCall() {
        viewModelScope.launch(appCoroutineDispatchers.ioDispatcher + coroutineExceptionHandler) {
            incrementIdlingResource()
            makeLocalDatabaseCall()
            decrementIdlingResource()
        }
    }

    fun showProgress() {
        _progressVisibility.postValue(View.VISIBLE)
        _errorVisibility.postValue(View.GONE)
        _listVisibility.postValue(View.GONE)
    }

    fun showError() {
        _progressVisibility.postValue(View.GONE)
        _errorVisibility.postValue(View.VISIBLE)
        _listVisibility.postValue(View.GONE)
    }

    private fun showData() {
        _progressVisibility.postValue(View.GONE)
        _errorVisibility.postValue(View.GONE)
        _listVisibility.postValue(View.VISIBLE)
    }

    private suspend fun makeLocalDatabaseCall() {
        val surahs = surahsDao.getAllSuras()
        _surahs.postValue(surahs)
        showData()
    }
}
