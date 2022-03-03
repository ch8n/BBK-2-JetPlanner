package io.github.ch8n.jetplanner.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.ch8n.jetplanner.data.local.database.AppDatabase
import io.github.ch8n.jetplanner.data.local.sources.TaskDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.instance(appContext)
    }

    @Singleton
    @Provides
    fun providesTaskDao(appDb: AppDatabase): TaskDao = appDb.taskDao()

}