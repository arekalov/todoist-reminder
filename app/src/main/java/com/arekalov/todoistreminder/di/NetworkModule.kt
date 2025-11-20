package com.arekalov.todoistreminder.di

import com.arekalov.todoistreminder.BuildConfig
import com.arekalov.todoistreminder.data.remote.mcp.McpApi
import com.arekalov.todoistreminder.data.remote.yandex.YandexGptApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("Ktor", message)
                }
            }
            level = LogLevel.INFO
        }
    }
    
    @Provides
    @Singleton
    fun provideMcpApi(httpClient: HttpClient): McpApi {
        return McpApi(httpClient, BuildConfig.MCP_SERVER_URL)
    }
    
    @Provides
    @Singleton
    fun provideYandexGptApi(httpClient: HttpClient): YandexGptApi {
        return YandexGptApi(
            httpClient,
            BuildConfig.YANDEX_API_KEY,
            BuildConfig.YANDEX_FOLDER_ID
        )
    }
}

