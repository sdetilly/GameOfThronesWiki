package com.tillylabs.gameofthroneswiki.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tillylabs.gameofthroneswiki.database.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY name ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getBooksPaginated(
        limit: Int,
        offset: Int,
    ): List<BookEntity>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBooksCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query("SELECT * FROM books WHERE url = :url")
    suspend fun getBookByUrl(url: String): BookEntity?

    @Query("SELECT MAX(lastUpdated) FROM books")
    suspend fun getLastUpdatedTimestamp(): Long?
}
