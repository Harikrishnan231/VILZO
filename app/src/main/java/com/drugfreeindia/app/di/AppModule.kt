package com.drugfreeindia.app.di

import android.content.Context
import androidx.room.Room
import com.drugfreeindia.app.data.local.AppDatabase
import com.drugfreeindia.app.data.repository.ExploreRepositoryImpl
import com.drugfreeindia.app.data.repository.ImpactRepositoryImpl
import com.drugfreeindia.app.data.repository.StoryRepositoryImpl
import com.drugfreeindia.app.data.repository.SupportRepositoryImpl
import com.drugfreeindia.app.data.repository.UserRepositoryImpl
import com.drugfreeindia.app.domain.repository.ExploreRepository
import com.drugfreeindia.app.domain.repository.ImpactRepository
import com.drugfreeindia.app.domain.repository.StoryRepository
import com.drugfreeindia.app.domain.repository.SupportRepository
import com.drugfreeindia.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "drug_free_india.db"
        ).fallbackToDestructiveMigration().build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStoryRepository(impl: StoryRepositoryImpl): StoryRepository

    @Binds
    @Singleton
    abstract fun bindSupportRepository(impl: SupportRepositoryImpl): SupportRepository

    @Binds
    @Singleton
    abstract fun bindExploreRepository(impl: ExploreRepositoryImpl): ExploreRepository

    @Binds
    @Singleton
    abstract fun bindImpactRepository(impl: ImpactRepositoryImpl): ImpactRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
