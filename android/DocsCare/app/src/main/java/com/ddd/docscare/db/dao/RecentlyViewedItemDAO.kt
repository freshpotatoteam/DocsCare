package com.ddd.docscare.db.dao

import androidx.room.*
import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import io.reactivex.Observable

@Dao
interface RecentlyViewedItemDAO {

    @Query("SELECT * from recently_viewed_item ORDER BY date DESC LIMIT 5")
    fun selectTop5(): Observable<List<RecentlyViewedItemDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentlyViewedItemDTO: RecentlyViewedItemDTO)

    @Delete
    fun delete(recentlyViewedItemDTO: RecentlyViewedItemDTO)
}