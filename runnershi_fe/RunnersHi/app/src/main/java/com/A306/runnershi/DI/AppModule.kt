package com.A306.runnershi.DI

import android.content.Context
import androidx.room.Room
import com.A306.runnershi.DB.RunningDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDB(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDB::class.java,
        "runnershi_db"
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDB) = db.getRunDao()

}