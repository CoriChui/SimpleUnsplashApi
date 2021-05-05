package com.kaonstudio.imagesearchmvipagination.di

import com.kaonstudio.imagesearchmvipagination.model.UnsplashModelStore
import com.kaonstudio.imagesearchmvipagination.network.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(UnsplashApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi =
        retrofit.create(UnsplashApi::class.java)

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideUnsplashModelStore() : UnsplashModelStore {
        return UnsplashModelStore()
    }
}