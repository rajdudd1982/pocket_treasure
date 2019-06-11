package com.stavro_xhardha.pockettreasure.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stavro_xhardha.pockettreasure.brain.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    private val _fajrCheck: MutableLiveData<Boolean> = MutableLiveData()
    private val _dhuhrCheck: MutableLiveData<Boolean> = MutableLiveData()
    private val _asrCheck: MutableLiveData<Boolean> = MutableLiveData()
    private val _maghribCheck: MutableLiveData<Boolean> = MutableLiveData()
    private val _ishaCheck: MutableLiveData<Boolean> = MutableLiveData()

    val fajrCheck: LiveData<Boolean> = _fajrCheck
    val dhuhrCheck: LiveData<Boolean> = _dhuhrCheck
    val asrCheck: LiveData<Boolean> = _asrCheck
    val maghribCheck: LiveData<Boolean> = _maghribCheck
    val ishaCheck: LiveData<Boolean> = _ishaCheck

    private var fajrCheckHelper: Boolean = false
    private var dhuhrCheckHelper: Boolean = false
    private var asrCheckCheckHelper: Boolean = false
    private var mahgribCheckCheckHelper: Boolean = false
    private var ishaCheckCheckHelper: Boolean = false

    init {
        listenToRepository()
    }

    private fun listenToRepository() {
        EspressoIdlingResource.increment()
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.let {
                fajrCheckHelper = it.getFajrChecked()
                dhuhrCheckHelper = it.getDhuhrChecked()
                asrCheckCheckHelper = it.getAsrChecked()
                mahgribCheckCheckHelper = it.getMaghribChecked()
                ishaCheckCheckHelper = it.getIshaChecked()
            }
            withContext(Dispatchers.Main) {
                this@SettingsViewModel._fajrCheck.value = fajrCheckHelper
                this@SettingsViewModel._dhuhrCheck.value = dhuhrCheckHelper
                this@SettingsViewModel._asrCheck.value = asrCheckCheckHelper
                this@SettingsViewModel._maghribCheck.value = mahgribCheckCheckHelper
                this@SettingsViewModel._ishaCheck.value = ishaCheckCheckHelper
                EspressoIdlingResource.decrement()
            }
        }
    }

    fun onSwFajrClick(checked: Boolean) {
        EspressoIdlingResource.increment()
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.putFajrNotification(checked)
            withContext(Dispatchers.Main) {
                EspressoIdlingResource.decrement()
                _fajrCheck.value = settingsRepository.getFajrChecked()
            }
        }
    }

    fun onSwDhuhrClick(checked: Boolean) {
        EspressoIdlingResource.increment()
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.putDhuhrNotification(checked)
            withContext(Dispatchers.Main) {
                EspressoIdlingResource.decrement()
                _dhuhrCheck.value = settingsRepository.getDhuhrChecked()
            }
        }
    }

    fun onSwAsrClick(checked: Boolean) {
        EspressoIdlingResource.increment()
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.putAsrNotification(checked)
            withContext(Dispatchers.Main) {
                EspressoIdlingResource.decrement()
                _asrCheck.value = settingsRepository.getAsrChecked()
            }
        }
    }

    fun onSwMaghribClick(checked: Boolean) {
        EspressoIdlingResource.increment()
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.putMaghribNotification(checked)
            withContext(Dispatchers.Main) {
                EspressoIdlingResource.decrement()
                _maghribCheck.value = settingsRepository.getMaghribChecked()
            }
        }
    }

    fun onSwIshaClick(checked: Boolean) {
        EspressoIdlingResource.increment()
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.putIshaNotification(checked)
            withContext(Dispatchers.Main) {
                EspressoIdlingResource.decrement()
                _ishaCheck.value = settingsRepository.getIshaChecked()
            }
        }
    }
}