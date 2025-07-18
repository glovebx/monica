package com.teobaranga.monica.data

import androidx.room.RoomDatabase
import com.teobaranga.monica.configuration.domain.ConfigurationDataStore
import com.teobaranga.monica.configuration.domain.ConfigurationItem
import com.teobaranga.monica.journal.data.local.JournalDatabaseOwner
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

internal const val DATABASE_NAME = "monica"

@ContributesTo(AppScope::class)
interface DatabaseComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun providesMonicaDatabase(
        databaseBuilder: RoomDatabase.Builder<MonicaDatabase>,
        databaseDeleter: DatabaseDeleter,
        configurationDataStore: ConfigurationDataStore,
    ): MonicaDatabase {
        runBlocking {
            if (configurationDataStore.get(ConfigurationItem.ShouldClearDatabaseOnNextLaunch)) {
                configurationDataStore.set(ConfigurationItem.ShouldClearDatabaseOnNextLaunch, false)
                databaseDeleter.deleteDatabase(DATABASE_NAME)
            }
        }
        return databaseBuilder.build()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesRoomDatabase(database: MonicaDatabase): RoomDatabase = database

    @Provides
    @SingleIn(AppScope::class)
    fun providesJournalDatabaseOwner(database: MonicaDatabase): JournalDatabaseOwner = database
}
