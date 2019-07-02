package com.ddd.docscare.repository.folder

import com.ddd.docscare.db.dto.FolderItemDTO
import io.reactivex.Maybe
import io.reactivex.Observable

class FolderRepository(private val folderLocalDataSource: FolderLocalDataSource) {

    fun select(): Observable<List<FolderItemDTO>> = folderLocalDataSource.select()
    fun selectByPath(path: String): Maybe<FolderItemDTO> = folderLocalDataSource.selectByPath(path)
    fun insert(folderItemDTO: FolderItemDTO) = folderLocalDataSource.insert(folderItemDTO)
    fun delete(folderItemDTO: FolderItemDTO) = folderLocalDataSource.delete(folderItemDTO)
}