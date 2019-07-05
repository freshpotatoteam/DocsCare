package com.ddd.docscare.db.dao

import androidx.room.*
import com.ddd.docscare.db.dto.FileItemDTO
import io.reactivex.Observable

@Dao
interface FileItemDAO {

    @Query("SELECT * FROM file_item WHERE category = :category")
    fun selectByCategory(category: String): Observable<List<FileItemDTO>>

    @Query("SELECT * FROM file_item WHERE title LIKE '%' || :value  || '%'")
    fun search(value: String): Observable<List<FileItemDTO>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fileItemDTO: FileItemDTO)

    @Delete
    fun delete(fileItemDTO: FileItemDTO)
}