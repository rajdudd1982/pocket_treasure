package com.sxhardha.names_module.di

import com.stavro_xhardha.core_module.dependency_injection.FragmentScoped
import com.sxhardha.names_module.network.NamesApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class NamesNetworkModule(private val retrofit: Retrofit) {

    @Provides
    @FragmentScoped
    fun provideNamesApi(): NamesApi = retrofit.create(NamesApi::class.java)
}
