package com.stavro_xhardha.pockettreasure.ui.setup

import com.stavro_xhardha.pockettreasure.brain.*
import com.stavro_xhardha.pockettreasure.model.Country
import com.stavro_xhardha.pockettreasure.network.TreasureApi
import kotlinx.coroutines.Deferred
import javax.inject.Inject

class SetupRepository @Inject constructor(
    private val treasureApi: TreasureApi,
    private val mSharedPreferences: MSharedPreferences
) {

    fun makeCountryApiCallAsync(): Deferred<ArrayList<Country>> =
        treasureApi.getCountriesList(COUNTRIES_API_URL)

    fun saveCountryToSharedPreferences(country: Country) {
        mSharedPreferences.writeString(COUNTRY_SHARED_PREFERENCE_KEY, country.name)
        mSharedPreferences.writeString(CAPITAL_SHARED_PREFERENCES_KEY, country.capitalCity)
    }

    fun saveWakingUpUser() {
        mSharedPreferences.writeBoolean(WAKE_UP_USER_FOR_FAJR, true)
    }
}