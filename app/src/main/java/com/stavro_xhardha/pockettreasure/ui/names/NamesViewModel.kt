package com.stavro_xhardha.pockettreasure.ui.names

import android.view.View
import androidx.lifecycle.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.stavro_xhardha.pockettreasure.brain.NAMES_LIST_STATE
import com.stavro_xhardha.pockettreasure.model.Name
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NamesViewModel @AssistedInject constructor(
    private val repository: NamesRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): NamesViewModel
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
        showError()
    }

    private val _progressBarVisibility: MutableLiveData<Int> = MutableLiveData()
    private val _errorLayoutVisibility: MutableLiveData<Int> = MutableLiveData()
    private val _namesList: MutableLiveData<List<Name>> = savedStateHandle.getLiveData(NAMES_LIST_STATE)

    val namesList: LiveData<List<Name>> = _namesList
    val progressBarVisibility: LiveData<Int> = _progressBarVisibility
    val errorLayoutVisibility: LiveData<Int> = _errorLayoutVisibility

    init {
        loadNamesList()
    }

    private fun loadNamesList() {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            startProgressBar()
            withContext(Dispatchers.IO) {
                val dataIsLocally = dataExistInDatabase()
                if (dataIsLocally) {
                    makeNamesDatabaseCall()
                } else {
                    makeNamesApiCall()
                }
            }
        }
    }

    private fun startProgressBar() {
        _progressBarVisibility.value = View.VISIBLE
        _errorLayoutVisibility.value = View.GONE
    }

    private suspend fun makeNamesDatabaseCall() {
        val names = repository.getNamesFromDatabase()
        withContext(Dispatchers.Main) {
            savedStateHandle.set(NAMES_LIST_STATE, names)
            _progressBarVisibility.value = View.GONE
            _errorLayoutVisibility.value = View.GONE
        }
    }

    private suspend fun makeNamesApiCall() {
        val namesResponse = repository.fetchNintyNineNamesAsync()
        if (namesResponse.isSuccessful) {
            saveNameToDatabase(namesResponse.body()?.data)
            makeNamesDatabaseCall()
        } else {
            withContext(Dispatchers.Main) {
                showError()
            }
        }
    }

    private suspend fun saveNameToDatabase(data: ArrayList<Name>?) {
        data?.forEach {
            repository.saveToDatabase(it)
        }
    }

    private suspend fun dataExistInDatabase(): Boolean = repository.countNamesInDatabase() > 0

    private fun showError() {
        _errorLayoutVisibility.value = View.VISIBLE
        _progressBarVisibility.value = View.GONE
    }

    fun retryConnection() {
        loadNamesList()
    }
}