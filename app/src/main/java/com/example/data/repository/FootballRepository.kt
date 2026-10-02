package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.TeamSettingsEntity
import kotlinx.coroutines.flow.Flow

class FootballRepository(private val database: AppDatabase) {
    private val playerDao = database.playerDao()
    private val chatDao = database.chatMessageDao()
    private val settingsDao = database.teamSettingsDao()

    val allPlayers: Flow<List<PlayerEntity>> = playerDao.getAllPlayers()

    suspend fun updatePlayerPosition(id: Int, x: Float, y: Float) {
        playerDao.updatePlayerPosition(id, x, y)
    }

    suspend fun updatePlayerName(id: Int, name: String) {
        playerDao.updatePlayerName(id, name)
    }

    suspend fun savePlayer(player: PlayerEntity) {
        if (player.id == 0) {
            playerDao.insertPlayer(player)
        } else {
            playerDao.updatePlayer(player)
        }
    }

    suspend fun deletePlayer(id: Int) {
        playerDao.deletePlayer(id)
    }

    fun getChatMessages(inviteCode: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessages(inviteCode)
    }

    suspend fun sendChatMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
    }

    val teamSettings: Flow<TeamSettingsEntity?> = settingsDao.getSettings()

    suspend fun updateSettings(settings: TeamSettingsEntity) {
        settingsDao.insertOrUpdate(settings)
    }
}
