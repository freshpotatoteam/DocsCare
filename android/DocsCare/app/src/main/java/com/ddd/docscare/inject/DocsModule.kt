package com.ddd.docscare.inject

import com.ddd.docscare.BuildConfig.X_API_KEY
import com.ddd.docscare.common.BASE_URL
import com.ddd.docscare.network.ApiService
import com.ddd.docscare.repository.AppRepository
import com.ddd.docscare.repository.RemoteAppDataSource
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
                    .addHeader("X-API-KEY", X_API_KEY)
                chain.proceed(requestBuilder.build())
            }.build())
            .build()
            .create(ApiService::class.java)
    }
}

val dataSourcePart = module {
    factory { RemoteAppDataSource(get()) }
}

val repositoryPart = module {
    factory { AppRepository(get()) }
}

val viewModelPart = module {
    viewModel { LoginViewModel(get()) }
}


val appModule = listOf(retrofitPart, dataSourcePart, repositoryPart, viewModelPart)