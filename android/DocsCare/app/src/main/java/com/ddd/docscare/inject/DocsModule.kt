package com.ddd.docscare.inject

import com.ddd.docscare.common.BASE_URL
import com.ddd.docscare.network.ApiService
import com.ddd.docscare.repository.AppDataSource
import com.ddd.docscare.repository.RemoteRepository
import com.ddd.docscare.ui.login.LoginViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val retrofitPart = module {
    single<ApiService> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("X-API-KEY", "c30d6e11-67cc-4643-8e20-b2cdff2799ef")
                chain.proceed(requestBuilder.build())
            }.build())
            .build()
            .create(ApiService::class.java)
    }
}

val repositoryPart = module {
    factory<AppDataSource> {
        RemoteRepository(get())
    }
}

val viewModelPart = module {
    viewModel { LoginViewModel(get()) }
}


val appModule = listOf(repositoryPart, repositoryPart, viewModelPart)