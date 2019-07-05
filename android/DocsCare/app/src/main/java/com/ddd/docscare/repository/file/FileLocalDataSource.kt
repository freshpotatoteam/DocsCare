package com.ddd.docscare.repository.file

import com.ddd.docscare.db.dao.FileItemDAO
import com.ddd.docscare.db.dto.FileItemDTO
import io.reactivex.Observable

class FileLocalDataSource(private val dao: FileItemDAO) {

    fun search(value: String): Observable<List<FileItemDTO>> = dao.search(value)
    fun selectByCategory(category: String): Observable<List<FileItemDTO>> =
            dao.selectByCategory(category)

    fun insert(fileItemDTO: FileItemDTO) = dao.insert(fileItemDTO)
    fun delete(fileItemDTO: FileItemDTO) = dao.delete(fileItemDTO)
}