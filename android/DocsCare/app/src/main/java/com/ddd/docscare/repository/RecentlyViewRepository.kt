package com.ddd.docscare.repository

import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import io.reactivex.Observable

class RecentlyViewRepository(private val dataSource: RecentlyViewLocalDataSource) {
    fun selectTop5(): Observable<List<RecentlyViewedItemDTO>> =
            dataSource.selectTop5()

    fun insert(recentlyViewedItemDTO: RecentlyViewedItemDTO) =
            dataSource.insert(recentlyViewedItemDTO)
}