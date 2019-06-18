package com.ddd.docscare.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ddd.docscare.db.DocsDatabase.Companion.DATABASE_VERSION
import com.ddd.docscare.db.dao.RecentlyViewedItemDAO
import com.ddd.docscare.db.dto.RecentlyViewedItemDTO
import java.util.concurrent.Executors

@Database(entities = [RecentlyViewedItemDTO::class], version = DATABASE_VERSION)
abstract class DocsDatabase: RoomDatabase() {

    abstract fun recentlyViewedItemDAO(): RecentlyViewedItemDAO

    companion object {
        const val DATABASE_VERSION = 1
        private var INSTANCE: DocsDatabase? = null

        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()
        fun ioThread(f: () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

        fun getInstance(context: Context): DocsDatabase {
            return INSTANCE ?: synchronized(DocsDatabase::class) {
                INSTANCE ?: Room.databaseBuilder(context.applicationContext, DocsDatabase::class.java, "docs_care.db")
                    .build().also {
                        INSTANCE = it
                    }
            }
        }

        fun destoryInstance() {
            INSTANCE = null
        }
    }
}