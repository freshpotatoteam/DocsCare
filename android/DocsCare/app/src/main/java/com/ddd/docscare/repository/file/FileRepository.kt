package com.ddd.docscare.repository.file

import com.ddd.docscare.db.dto.FileItemDTO
import io.reactivex.Observable

class FileRepository(private val dataSource: FileLocalDataSource) {

    fun search(value: String): Observable<List<FileItemDTO>> = dataSource.search(value)
    fun selectByCategory(category: String): Observable<List<FileItemDTO>> =
        dataSource.selectByCategory(category)

    fun insert(fileItemDTO: FileItemDTO) = dataSource.insert(fileItemDTO)
    fun delete(fileItemDTO: FileItemDTO) = dataSource.delete(fileItemDTO)
}