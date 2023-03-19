package com.raghav.market.di

import com.raghav.market.data.remote.RetrofitCreator
import com.raghav.market.data.remote.api.SellerDetailsApi
import com.raghav.market.domain.dispatcher.AppDispatcher
import com.raghav.market.domain.dispatcher.StandardDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return RetrofitCreator(SellerDetailsApi.BASE_URL).getInstance()
    }

    @Provides
    @Singleton
    fun provideDispatcher(): AppDispatcher {
        return StandardDispatcher()
    }
}