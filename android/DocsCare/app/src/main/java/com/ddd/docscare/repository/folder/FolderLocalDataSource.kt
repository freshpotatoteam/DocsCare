package com.ddd.docscare.repository.folder

import com.ddd.docscare.db.dao.FolderItemDAO
import com.ddd.docscare.db.dto.FolderItemDTO
import io.reactivex.Maybe
import io.reactivex.Observable

class FolderLocalDataSource(private val dao: FolderItemDAO) {

    fun select(): Observable<List<FolderItemDTO>> = dao.select()
    fun selectByPath(path: String): Maybe<FolderItemDTO> = dao.selectByPath(path)
    fun insert(folderItemDTO: FolderItemDTO) = dao.insert(folderItemDTO)
    fun delete(folderItemDTO: FolderItemDTO) = dao.delete(folderItemDTO)
}