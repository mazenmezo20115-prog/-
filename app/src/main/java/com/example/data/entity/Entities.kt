package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "لاعب",
    val number: Int = 1,
    val position: String = "لاعب",
    val roleCategory: String = "PLAYER",
    val isStarting: Boolean = true,
    val pitchXRatio: Float = 0.5f,
    val pitchYRatio: Float = 0.5f
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inviteCode: String = "",
    val senderName: String = "لاعب",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "team_settings")
data class TeamSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val teamName: String = "",
    val stadiumName: String = "",
    val managerName: String = "",
    val inviteCode: String = "5A-8821",
    val themeColorHex: Long = 0xFF64748B, // لون الثيم الافتراضي (داكن هادئ)
    val bgColorHex: Long = 0xFF0B0F14     // خلفية الملعب الداكنة القاتمة
)
