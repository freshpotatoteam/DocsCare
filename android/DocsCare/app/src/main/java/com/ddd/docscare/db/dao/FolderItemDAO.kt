package com.ddd.docscare.db.dao

import androidx.room.*
import com.ddd.docscare.db.dto.FolderItemDTO
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface FolderItemDAO {

    @Query("SELECT * FROM folder_item ORDER BY id ASC")
    fun select(): Observable<List<FolderItemDTO>>

    @Query("SELECT * FROM folder_item WHERE path = :path")
    fun selectByPath(path: String): Maybe<FolderItemDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(folderItemDTO: FolderItemDTO)

    @Delete
    fun delete(folderItemDTO: FolderItemDTO)
}