package com.ddd.docscare.repository

import com.ddd.docscare.db.dao.RecentlyViewedItemDAO
import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import io.reactivex.Observable

class RecentlyViewLocalDataSource(private val dao: RecentlyViewedItemDAO) {
    fun selectTop5(): Observable<List<RecentlyViewedItemDTO>> =
            dao.selectTop5()

    fun insert(recentlyViewedItemDTO: RecentlyViewedItemDTO) =
            dao.insert(recentlyViewedItemDTO)
}