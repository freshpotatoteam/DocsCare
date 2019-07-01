package com.ddd.docscare.inject

import com.ddd.docscare.BuildConfig.X_API_KEY
import com.ddd.docscare.common.BASE_URL
import com.ddd.docscare.db.DocsDatabase
import com.ddd.docscare.network.ApiService
import com.ddd.docscare.repository.RecentlyViewLocalDataSource
import com.ddd.docscare.repository.RecentlyViewRepository
import com.ddd.docscare.repository.folder.FolderLocalDataSource
import com.ddd.docscare.repository.folder.FolderRepository
import com.ddd.docscare.repository.login.LoginRemoteDataSource
import com.ddd.docscare.repository.login.LoginRepository
import com.ddd.docscare.repository.scan.ScanLocalDataSource
import com.ddd.docscare.repository.scan.ScanRemoteDataSource
import com.ddd.docscare.repository.scan.ScanRepository
import com.ddd.docscare.ui.folder.FolderViewModel
import com.ddd.docscare.ui.login.LoginViewModel
import com.ddd.docscare.ui.main.RecentlyItemViewModel
import com.ddd.docscare.ui.scan.ScanViewModel
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

val databasePart = module {
    single {
        DocsDatabase.getInstance(get())
    }
}

val dataSourcePart = module {
    single { LoginRemoteDataSource(get()) }
    single { RecentlyViewLocalDataSource((get<DocsDatabase>().recentlyViewedItemDAO())) }
    single { FolderLocalDataSource((get<DocsDatabase>().folderItemDAO())) }
    single { ScanLocalDataSource() }
    single { ScanRemoteDataSource(get()) }
}

val repositoryPart = module {
    single { LoginRepository(get()) }
    single { RecentlyViewRepository(get()) }
    single { FolderRepository(get()) }
    single { ScanRepository(get(), get()) }
}

val viewModelPart = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RecentlyItemViewModel(get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { FolderViewModel(get()) }
}


val appModule = listOf(retrofitPart, databasePart, dataSourcePart, repositoryPart, viewModelPart)