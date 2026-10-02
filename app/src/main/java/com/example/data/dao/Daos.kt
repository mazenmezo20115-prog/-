package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.TeamSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY number ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("UPDATE players SET pitchXRatio = :x, pitchYRatio = :y WHERE id = :id")
    suspend fun updatePlayerPosition(id: Int, x: Float, y: Float)

    @Query("UPDATE players SET name = :name WHERE id = :id")
    suspend fun updatePlayerName(id: Int, name: String)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayer(id: Int)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE inviteCode = :inviteCode ORDER BY timestamp ASC")
    fun getMessages(inviteCode: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long
}

@Dao
interface TeamSettingsDao {
    @Query("SELECT * FROM team_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<TeamSettingsEntity?>

    @Query("SELECT * FROM team_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): TeamSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: TeamSettingsEntity)
}
