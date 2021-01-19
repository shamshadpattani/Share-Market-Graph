

package com.example.stockchart.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.data.room.EntityDescriptions.DB_NAME
import com.example.stockchart.data.room.dao.InvestDao
import com.example.stockchart.data.room.dao.StockInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Database(
    entities = [
        Info::class,
    MyInvestDB::class
    ],
    version = DatabaseMigrations.DB_VERSION,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getInstance(context: Context): StockDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        StockDatabase::class.java,
                    DB_NAME
                    )
                  .fallbackToDestructiveMigration()
                   .build()

                INSTANCE = instance
                return instance
            }
        }

    }

    fun clearTables() {
        GlobalScope.launch(Dispatchers.IO) {
            this@StockDatabase.clearAllTables()
        }
    }

    abstract fun saveInfoDao(): StockInfoDao
    abstract fun saveInvestDao(): InvestDao
}
