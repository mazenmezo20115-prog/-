package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppSection
import com.example.ui.FootballViewModel
import com.example.ui.components.GlassCard
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.pitch.PitchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // تفعيل الاتجاه العربي من اليمين إلى اليسار
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme {
                    val viewModel: FootballViewModel = viewModel()
                    GloomyFootballApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun GloomyFootballApp(viewModel: FootballViewModel) {
    val currentSection by viewModel.currentSection.collectAsState()
    val teamSettings by viewModel.teamSettings.collectAsState()

    BackHandler(enabled = currentSection != AppSection.PITCH) {
        viewModel.setSection(AppSection.PITCH)
    }

    val dynamicBgColor = Color(teamSettings.bgColorHex)
    val dynamicThemeColor = Color(teamSettings.themeColorHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        dynamicBgColor,
                        dynamicBgColor.copy(alpha = 0.92f),
                        Color(0xFF03070A)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            // تمت إزالة أي شريط أو وسوم علوية تماماً كما طُلب
            bottomBar = {
                MinimalBottomNavDock(
                    currentSection = currentSection,
                    onSectionSelected = { viewModel.setSection(it) },
                    themeColor = dynamicThemeColor
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentSection,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "SectionTransition"
                ) { section ->
                    when (section) {
                        AppSection.PITCH -> PitchScreen(viewModel = viewModel)
                        AppSection.CHAT -> ChatScreen(viewModel = viewModel)
                        AppSection.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

/**
 * شريط سفلي زجاجي بثلاثة أقسام فقط: الفرقة، الشات، الإعدادات
 */
@Composable
fun MinimalBottomNavDock(
    currentSection: AppSection,
    onSectionSelected: (AppSection) -> Unit,
    themeColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x730F172A),
            borderColor = Color(0x26FFFFFF),
            shape = RoundedCornerShape(26.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MinimalNavItem(
                    section = AppSection.PITCH,
                    icon = Icons.Default.SportsSoccer,
                    isSelected = currentSection == AppSection.PITCH,
                    themeColor = themeColor,
                    onClick = { onSectionSelected(AppSection.PITCH) },
                    testTag = "nav_pitch"
                )

                MinimalNavItem(
                    section = AppSection.CHAT,
                    icon = Icons.Default.Groups,
                    isSelected = currentSection == AppSection.CHAT,
                    themeColor = themeColor,
                    onClick = { onSectionSelected(AppSection.CHAT) },
                    testTag = "nav_chat"
                )

                MinimalNavItem(
                    section = AppSection.SETTINGS,
                    icon = Icons.Default.Tune,
                    isSelected = currentSection == AppSection.SETTINGS,
                    themeColor = themeColor,
                    onClick = { onSectionSelected(AppSection.SETTINGS) },
                    testTag = "nav_settings"
                )
            }
        }
    }
}

@Composable
fun MinimalNavItem(
    section: AppSection,
    icon: ImageVector,
    isSelected: Boolean,
    themeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) themeColor.copy(alpha = 0.22f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = section.labelAr,
                tint = if (isSelected) themeColor else Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = section.labelAr,
                color = if (isSelected) Color.White else Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
