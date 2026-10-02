package com.example.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FootballViewModel
import com.example.ui.components.GlassCard

data class ColorOption(
    val name: String,
    val primaryColor: Long,
    val backgroundColor: Long
)

@Composable
fun SettingsScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.teamSettings.collectAsState()
    val players by viewModel.allPlayers.collectAsState()
    val context = LocalContext.current

    var teamNameInput by remember(settings.teamName) { mutableStateOf(settings.teamName) }
    var stadiumInput by remember(settings.stadiumName) { mutableStateOf(settings.stadiumName) }
    var managerInput by remember(settings.managerName) { mutableStateOf(settings.managerName) }

    val colorOptions = listOf(
        ColorOption("رمادي داكن", 0xFF64748B, 0xFF0A0F14),
        ColorOption("أزرق ليلي", 0xFF38BDF8, 0xFF08121E),
        ColorOption("زمردي غامق", 0xFF10B981, 0xFF04140D),
        ColorOption("عودي قاتم", 0xFFF43F5E, 0xFF18080C)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // بطاقة المدخلات الفارغة
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x33101921),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // اسم الفريق
                OutlinedTextField(
                    value = teamNameInput,
                    onValueChange = { teamNameInput = it },
                    label = { Text("اسم الفريق") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(settings.themeColorHex),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // اسم الملعب
                OutlinedTextField(
                    value = stadiumInput,
                    onValueChange = { stadiumInput = it },
                    label = { Text("اسم الملعب") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(settings.themeColorHex),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // اسم الكابتن
                OutlinedTextField(
                    value = managerInput,
                    onValueChange = { managerInput = it },
                    label = { Text("اسم الكابتن") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(settings.themeColorHex),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // زر الحفظ
                Button(
                    onClick = {
                        viewModel.updateTeamSettings(
                            settings.copy(
                                teamName = teamNameInput.trim(),
                                stadiumName = stadiumInput.trim(),
                                managerName = managerInput.trim()
                            )
                        )
                        Toast.makeText(context, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(settings.themeColorHex)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // خاصية مشاركة التشكيلة وحالة الكابتن مع جميع أعضاء الفريق لمزامنتها
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x33101921),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "مزامنة ومشاركة التشكيلة",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "مشاركة التشكيلة النهائية وحالة الكابتن مع كافة أعضاء الفريق عبر كود الفريق وتطبيقات المراسلة.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(settings.themeColorHex).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(settings.themeColorHex).copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val squadCount = players.size
                            val teamDisplayName = teamNameInput.ifBlank { "فريق الخماسي" }
                            val venueDisplayName = stadiumInput.ifBlank { "الملعب الخماسي" }
                            val captainDisplayName = managerInput.ifBlank { "الكابتن" }
                            val inviteCode = settings.inviteCode

                            val shareText = """
                                ⚽ تشكيلة مباراة الخماسي المعتمدة
                                📋 الفريق: $teamDisplayName
                                🏟️ الملعب: $venueDisplayName
                                👑 الكابتن: $captainDisplayName (دور المنظم نشط)
                                👥 عدد اللاعبين الحالي: $squadCount لاعبين
                                🔑 كود الفريق للمزامنة والشات: $inviteCode
                                -------------------------------------
                                افتح تطبيق ملعبنا وانضم بالكود لمشاهدة التشكيلة في الوقت الفعلي!
                            """.trimIndent()

                            // نسخ للحافظة
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("تشكيلة الفريق", shareText))

                            // فتح نافذة المشاركة في النظام
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val chooserIntent = Intent.createChooser(sendIntent, "مشاركة التشكيلة مع الفريق")
                            context.startActivity(chooserIntent)

                            Toast.makeText(context, "تم نسخ التشكيلة وفتح نافذة المشاركة", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة التشكيلة",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مشاركة التشكيلة وبيانات الفريق",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // قسم اختيار الألوان وتغيير مظهر وخلفية التطبيق
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x33101921),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "لون المظهر والخلفية",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorOptions.forEach { opt ->
                        val isSelected = settings.themeColorHex == opt.primaryColor
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                viewModel.updateThemeColors(opt.primaryColor, opt.backgroundColor)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(opt.primaryColor))
                                    .border(
                                        if (isSelected) 2.5.dp else 1.dp,
                                        if (isSelected) Color.White else Color(0x40FFFFFF),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = opt.name,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
