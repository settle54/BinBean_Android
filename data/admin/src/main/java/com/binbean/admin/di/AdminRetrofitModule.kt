package com.binbean.admin.di

import com.binbean.admin.api.CafeRegisterService
import com.binbean.admin.datasource.CafeRegisterRemoteDataSource
import com.binbean.retrofit.RetrofitModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdminRetrofitModule {

    @Provides
    @Singleton
    fun provideAdminRetrofitService(
        @RetrofitModule.ServiceApi adminRetrofit: Retrofit
    ): CafeRegisterService = adminRetrofit.create(CafeRegisterService::class.java)

    @Provides
    @Singleton
    fun provideCafeRegisterRemoteDataSource(registerApi: CafeRegisterService) =
        CafeRegisterRemoteDataSource(registerApi)
}