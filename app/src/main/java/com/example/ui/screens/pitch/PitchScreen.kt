package com.example.ui.screens.pitch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PlayerEntity
import com.example.ui.FootballViewModel
import kotlin.math.roundToInt

@Composable
fun PitchScreen(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    val players by viewModel.allPlayers.collectAsState()
    var playerToDelete by remember { mutableStateOf<PlayerEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // شارة الكابتن أعلى منطقة الملعب لتأكيد دور الكابتن / المنظم النشط
        CaptainArmbandStatusHeader()

        Spacer(modifier = Modifier.height(10.dp))

        // رقعة ملعب الخماسي الداكن القاتم
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(22.dp))
        ) {
            // استخدام Ltr صراحة لرقعة الملعب لضمان تطابق حركة السحب مع اتجاه اللمس 100%
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Gloomy5AsidePitchCanvas(
                    players = players,
                    onPlayerDragged = { id, x, y -> viewModel.onPlayerDragged(id, x, y) },
                    onNameChanged = { id, newName -> viewModel.updatePlayerName(id, newName) },
                    onDeleteRequested = { player -> playerToDelete = player }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // زر إضافة لاعب
        Surface(
            color = Color(0x1AFFFFFF),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.addPlayer() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة لاعب",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "إضافة لاعب",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }

    // نافذة تأكيد حذف اللاعب
    playerToDelete?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            containerColor = Color(0xFF161F28),
            title = {
                Text(
                    text = "حذف اللاعب",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "هل تريد إزالة هذا اللاعب (${player.name}) من تشكيلة الملعب؟",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePlayer(player.id)
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("حذف", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text("إلغاء", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

/**
 * شارة الكابتن في أعلى رقعة الملعب لبيان دور الكابتن / المنظم النشط
 */
@Composable
fun CaptainArmbandStatusHeader() {
    Surface(
        color = Color(0x2E000000),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0x40FFD60A)),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFD60A))
                    .border(1.dp, Color(0x66000000), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "شارة الكابتن • دور المنظم نشط",
                color = Color(0xFFF1F5F9),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * رسم أرضية ملعب الخماسي الداكن القاتم (Gloomy 5-a-side Pitch)
 */
@Composable
fun Gloomy5AsidePitchCanvas(
    players: List<PlayerEntity>,
    onPlayerDragged: (id: Int, x: Float, y: Float) -> Unit,
    onNameChanged: (id: Int, newName: String) -> Unit,
    onDeleteRequested: (PlayerEntity) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // تدرج لوني داكن وقاتم لأرضية الملعب
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C1319),
                        Color(0xFF0F1820),
                        Color(0xFF090E14)
                    )
                )
            )

            val margin = 12f
            val pitchLeft = margin
            val pitchTop = margin
            val pitchRight = w - margin
            val pitchBottom = h - margin
            val pitchWidth = pitchRight - pitchLeft
            val pitchHeight = pitchBottom - pitchTop

            val linePaint = Color(0x38FFFFFF)
            val strokeWidth = 2.0f

            // خطوط الحدود
            drawRoundRect(
                color = linePaint,
                topLeft = Offset(pitchLeft, pitchTop),
                size = Size(pitchWidth, pitchHeight),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(strokeWidth)
            )

            // خط المنتصف
            val midY = pitchTop + pitchHeight / 2f
            drawLine(
                color = linePaint,
                start = Offset(pitchLeft, midY),
                end = Offset(pitchRight, midY),
                strokeWidth = strokeWidth
            )

            // دائرة السنتر
            val centerRadius = pitchWidth * 0.18f
            drawCircle(
                color = linePaint,
                center = Offset(w / 2f, midY),
                radius = centerRadius,
                style = Stroke(strokeWidth)
            )
            drawCircle(
                color = linePaint,
                center = Offset(w / 2f, midY),
                radius = 3.0f
            )

            // قوس منطقة الجزاء العلوية
            val dBoxRadiusTop = pitchWidth * 0.28f
            drawArc(
                color = linePaint,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w / 2f - dBoxRadiusTop, pitchTop - dBoxRadiusTop * 0.2f),
                size = Size(dBoxRadiusTop * 2, dBoxRadiusTop * 1.5f),
                style = Stroke(strokeWidth)
            )
            drawCircle(color = linePaint, center = Offset(w / 2f, pitchTop + dBoxRadiusTop * 0.75f), radius = 2.5f)

            // قوس منطقة الجزاء السفلية
            val dBoxRadiusBottom = pitchWidth * 0.28f
            drawArc(
                color = linePaint,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w / 2f - dBoxRadiusBottom, pitchBottom - dBoxRadiusBottom * 1.3f),
                size = Size(dBoxRadiusBottom * 2, dBoxRadiusBottom * 1.5f),
                style = Stroke(strokeWidth)
            )
            drawCircle(color = linePaint, center = Offset(w / 2f, pitchBottom - dBoxRadiusBottom * 0.75f), radius = 2.5f)
        }

        // عرض رموز اللاعبين مع حقل الاسم القابل للتعديل
        players.forEach { player ->
            EditableGloomyPlayerToken(
                player = player,
                pitchWidthPx = widthPx,
                pitchHeightPx = heightPx,
                onPositionChange = { newX, newY -> onPlayerDragged(player.id, newX, newY) },
                onNameChange = { newName -> onNameChanged(player.id, newName) },
                onDeleteClick = { onDeleteRequested(player) }
            )
        }
    }
}

/**
 * رمز لاعب قابل للسحب بدقة مع حقل نصي قابل للتعديل لتغيير اسم اللاعب
 */
@Composable
fun EditableGloomyPlayerToken(
    player: PlayerEntity,
    pitchWidthPx: Float,
    pitchHeightPx: Float,
    onPositionChange: (Float, Float) -> Unit,
    onNameChange: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    var posX by remember(player.pitchXRatio, pitchWidthPx) {
        mutableFloatStateOf(player.pitchXRatio * pitchWidthPx)
    }
    var posY by remember(player.pitchYRatio, pitchHeightPx) {
        mutableFloatStateOf(player.pitchYRatio * pitchHeightPx)
    }
    var playerName by remember(player.name) {
        mutableStateOf(player.name)
    }

    val tokenRadiusDp = 22.dp
    val density = LocalDensity.current
    val halfSizePx = with(density) { tokenRadiusDp.toPx() }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (posX - halfSizePx).roundToInt(),
                    y = (posY - halfSizePx).roundToInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // دائرة داكنة زجاجية برقم اللاعب - السحب محصور في الدائرة لحرية كتابة الاسم
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x59FFFFFF),
                                    Color(0x261E293B),
                                    Color(0x660F172A)
                                )
                            )
                        )
                        .border(1.2.dp, Color(0x59FFFFFF), CircleShape)
                        .pointerInput(player.id) {
                            detectDragGestures(
                                onDragEnd = {
                                    val newXRatio = (posX / pitchWidthPx).coerceIn(0.10f, 0.90f)
                                    val newYRatio = (posY / pitchHeightPx).coerceIn(0.10f, 0.92f)
                                    onPositionChange(newXRatio, newYRatio)
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                posX = (posX + dragAmount.x).coerceIn(halfSizePx, pitchWidthPx - halfSizePx)
                                posY = (posY + dragAmount.y).coerceIn(halfSizePx, pitchHeightPx - halfSizePx)
                            }
                        }
                ) {
                    Text(
                        text = "${player.number}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // حقل نصي قابل للتعديل المباشر والكتابة لتغيير اسم اللاعب
                Surface(
                    color = Color(0xDD000000),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.6.dp, Color(0x4DFFFFFF)),
                    modifier = Modifier.width(64.dp)
                ) {
                    BasicTextField(
                        value = playerName,
                        onValueChange = {
                            playerName = it
                            onNameChange(it.ifBlank { "لاعب" })
                        },
                        textStyle = TextStyle(
                            color = Color(0xFFF1F5F9),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // زر حذف / إزالة اللاعب أعلى الرمز
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-4).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xE6EF4444))
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف اللاعب",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
