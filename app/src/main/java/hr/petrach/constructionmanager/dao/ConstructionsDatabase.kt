package hr.petrach.constructionmanager.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Contractor::class, Construction::class], version = 7)
@TypeConverters(DateConverter::class)
abstract class ConstructionsDatabase : RoomDatabase() {
    abstract fun workerDao() : ContractorDao
    abstract fun constructionDao() : ConstructionDao

    companion object {
        @Volatile private var INSTANCE: ConstructionsDatabase? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(ConstructionsDatabase::class.java) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ConstructionsDatabase::class.java,

            "constructions.db"
        ).fallbackToDestructiveMigration().build()
    }
}