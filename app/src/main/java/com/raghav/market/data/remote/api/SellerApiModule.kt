package com.raghav.market.data.remote.api

import com.raghav.market.data.repoimpl.SellerDetailsRepositoryImpl
import com.raghav.market.domain.repository.SellerDetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SellerApiModule {

    @Provides
    fun provideSellerApi(retrofit: Retrofit): SellerDetailsApi {
        return retrofit.create(SellerDetailsApi::class.java)
    }

    @Provides
    fun provideSellerRepository(api: SellerDetailsApi): SellerDetailsRepository {
        return SellerDetailsRepositoryImpl(api)
    }
}