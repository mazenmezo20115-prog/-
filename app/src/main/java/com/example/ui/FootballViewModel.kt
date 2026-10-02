package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.TeamSettingsEntity
import com.example.data.repository.FootballRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppSection(val labelAr: String) {
    PITCH("الفرقة"),
    CHAT("الشات"),
    SETTINGS("الإعدادات")
}

class FootballViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FootballRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FootballRepository(database)
    }

    // 3 أقسام فقط: الفرقة، الشات، الإعدادات
    private val _currentSection = MutableStateFlow(AppSection.PITCH)
    val currentSection: StateFlow<AppSection> = _currentSection.asStateFlow()

    fun setSection(section: AppSection) {
        _currentSection.value = section
    }

    // إعدادات الفريق والثيم
    val teamSettings: StateFlow<TeamSettingsEntity> = repository.teamSettings
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TeamSettingsEntity()
        )

    fun updateTeamSettings(updated: TeamSettingsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSettings(updated)
        }
    }

    fun updateThemeColors(themeColor: Long, bgColor: Long) {
        val current = teamSettings.value
        updateTeamSettings(current.copy(themeColorHex = themeColor, bgColorHex = bgColor))
    }

    // لاعبو الخماسي
    val allPlayers: StateFlow<List<PlayerEntity>> = repository.allPlayers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onPlayerDragged(playerId: Int, newXRatio: Float, newYRatio: Float) {
        val clampedX = newXRatio.coerceIn(0.12f, 0.88f)
        val clampedY = newYRatio.coerceIn(0.12f, 0.90f)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlayerPosition(playerId, clampedX, clampedY)
        }
    }

    fun updatePlayerName(playerId: Int, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlayerName(playerId, newName)
        }
    }

    fun addPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = allPlayers.value.size
            val newNumber = count + 1
            val newPlayer = PlayerEntity(
                name = "لاعب",
                number = newNumber,
                position = "لاعب",
                pitchXRatio = 0.50f,
                pitchYRatio = 0.50f
            )
            repository.savePlayer(newPlayer)
        }
    }

    fun deletePlayer(playerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlayer(playerId)
        }
    }

    // الشات
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentChatMessages: StateFlow<List<ChatMessageEntity>> = teamSettings
        .flatMapLatest { settings ->
            repository.getChatMessages(settings.inviteCode)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val currentCode = teamSettings.value.inviteCode
        val sender = teamSettings.value.managerName.ifBlank { "لاعب" }
        val msg = ChatMessageEntity(
            inviteCode = currentCode,
            senderName = sender,
            message = text.trim()
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendChatMessage(msg)
        }
    }

    fun joinTeamWithCode(newCode: String) {
        val cleanCode = newCode.trim().uppercase()
        if (cleanCode.isNotBlank()) {
            val updated = teamSettings.value.copy(inviteCode = cleanCode)
            updateTeamSettings(updated)
        }
    }
}
