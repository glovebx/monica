package com.teobaranga.monica.journal.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.teobaranga.monica.core.data.local.OrderBy
import com.teobaranga.monica.core.data.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
abstract class JournalDao {

    fun getJournalEntries(
        orderBy: OrderBy? = null,
        limit: Int? = null,
        offset: Int? = null,
    ): Flow<List<JournalEntryEntity>> {
        val query = buildString {
            append("SELECT * FROM journal_entries")
            if (orderBy != null) {
                append(" ")
                append("ORDER BY ${orderBy.columnName} ${if (orderBy.isAscending) "ASC" else "DESC"}")
            }
            if (limit != null) {
                append(" ")
                append("LIMIT $limit")
            }
            if (offset != null) {
                append(" ")
                append("OFFSET $offset")
            }
        }
        return getJournalEntries(SimpleSQLiteQuery(query))
    }

    @RawQuery(observedEntities = [JournalEntryEntity::class])
    protected abstract fun getJournalEntries(query: SupportSQLiteQuery): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE syncStatus = :status")
    abstract suspend fun getByStatus(status: SyncStatus): List<JournalEntryEntity>

    @Query("UPDATE journal_entries SET syncStatus = :syncStatus WHERE id = :entryId")
    abstract suspend fun setSyncStatus(entryId: Int, syncStatus: SyncStatus)

    @Query("SELECT id FROM journal_entries")
    abstract fun getJournalEntryIds(): Flow<List<Int>>

    @Query(
        value = """
        SELECT * FROM journal_entries
        WHERE id = :id
    """,
    )
    abstract fun getJournalEntry(id: Int): Flow<JournalEntryEntity>

    @Upsert
    abstract suspend fun upsertJournalEntries(entities: List<JournalEntryEntity>)

    @Upsert
    abstract suspend fun upsertJournalEntry(entry: JournalEntryEntity)

    @Transaction
    open suspend fun sync(entityId: Int, entry: JournalEntryEntity) {
        delete(listOf(entityId))
        upsertJournalEntries(listOf(entry))
    }

    @Query("SELECT max(id) FROM journal_entries")
    abstract suspend fun getMaxId(): Int

    @Query("SELECT (SELECT COUNT(*) FROM journal_entries) == 0")
    abstract suspend fun isEmpty(): Boolean

    @Query("DELETE FROM journal_entries WHERE id in (:entityIds)")
    abstract suspend fun delete(entityIds: List<Int>)
}
