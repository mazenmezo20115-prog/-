package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ChatMessageDao
import com.example.data.dao.PlayerDao
import com.example.data.dao.TeamSettingsDao
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.TeamSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlayerEntity::class,
        ChatMessageEntity::class,
        TeamSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun teamSettingsDao(): TeamSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "malabna_gloomy_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val playerDao = database.playerDao()
            val settingsDao = database.teamSettingsDao()

            // إعدادات فارغة تماماً مع لون داكن وثيم مبدئي
            val initialSettings = TeamSettingsEntity(
                id = 1,
                teamName = "",
                stadiumName = "",
                managerName = "",
                inviteCode = "5A-8821",
                themeColorHex = 0xFF64748B,
                bgColorHex = 0xFF0A0E13
            )
            settingsDao.insertOrUpdate(initialSettings)

            // 5 لاعبين فقط بعنوان "لاعب" وبدون أي دوائر خضراء
            val initialPlayers = listOf(
                PlayerEntity(name = "لاعب", number = 1, position = "لاعب", pitchXRatio = 0.50f, pitchYRatio = 0.86f),
                PlayerEntity(name = "لاعب", number = 2, position = "لاعب", pitchXRatio = 0.72f, pitchYRatio = 0.64f),
                PlayerEntity(name = "لاعب", number = 3, position = "لاعب", pitchXRatio = 0.28f, pitchYRatio = 0.64f),
                PlayerEntity(name = "لاعب", number = 4, position = "لاعب", pitchXRatio = 0.50f, pitchYRatio = 0.42f),
                PlayerEntity(name = "لاعب", number = 5, position = "لاعب", pitchXRatio = 0.50f, pitchYRatio = 0.18f)
            )
            playerDao.insertPlayers(initialPlayers)
        }
    }
}
