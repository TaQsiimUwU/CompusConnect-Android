package com.taqsiim.compusconnect.di

import android.content.Context
import androidx.room.Room
import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.CampusDatabase
import com.taqsiim.compusconnect.data.local.TokenManager
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.data.repository.PostRepository
import com.taqsiim.compusconnect.data.repository.UserRepository
import com.taqsiim.compusconnect.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Database ---
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CampusDatabase {
        return Room.databaseBuilder(
            context,
            CampusDatabase::class.java,
            "campus_connect_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCampusDao(database: CampusDatabase): CampusDao {
        return database.campusDao()
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    // --- Network ---
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // --- Repositories ---
    @Provides
    @Singleton
    fun provideUserRepository(
        api: ApiService, 
        dao: CampusDao,
        tokenManager: TokenManager
    ): UserRepository {
        return UserRepository(api, dao, tokenManager)
    }

    @Provides
    @Singleton
    fun provideClubRepository(api: ApiService, dao: CampusDao): ClubRepository {
        return ClubRepository(api, dao)
    }

    @Provides
    @Singleton
    fun provideEventRepository(api: ApiService, dao: CampusDao): EventRepository {
        return EventRepository(api, dao)
    }

    @Provides
    @Singleton
    fun providePostRepository(api: ApiService, dao: CampusDao): PostRepository {
        return PostRepository(api, dao)
    }
}
