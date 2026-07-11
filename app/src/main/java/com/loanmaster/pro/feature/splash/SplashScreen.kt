package com.loanmaster.pro.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit
) {
    val navyBg = Color(0xFF04153B)
    val premiumGold = Color(0xFFF5B82E)
    val goldAccent = Color(0xFFFFD700)
    val blueAccent = Color(0xFF2D7FF9)
    val lightBlueAccent = Color(0xFF5BA4FF)

    // Animatable states
    val bgAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.85f) }
    val glowAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(40f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val loaderAlpha = remember { Animatable(0f) }

    // Continuous animations
    val infiniteTransition = rememberInfiniteTransition(label = "continuous")
    
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )
    
    val circleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CircleRotation"
    )

    val logoFloatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoFloat"
    )

    val loaderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LoaderRotation"
    )

    LaunchedEffect(Unit) {
        // 0-400 ms: Background fades in
        bgAlpha.animateTo(1f, animationSpec = tween(400))
        
        // 400-900 ms: Logo scales from 0.85x to 1.0x, Logo glow appears
        launch {
            logoScale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            glowAlpha.animateTo(0.25f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        delay(500)
        
        // 900-1300 ms: App title fades in from below
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(400))
        }
        launch {
            titleOffsetY.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        delay(400)
        
        // 1300-1600 ms: Subtitle fades in
        launch {
            subtitleAlpha.animateTo(1f, animationSpec = tween(300))
        }
        launch {
            loaderAlpha.animateTo(1f, animationSpec = tween(300))
        }
        delay(300)
        
        // 1600-3000 ms: Wait
        delay(1400)
        
        // 3000 ms: Navigate
        onNavigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(navyBg)
            .alpha(bgAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        // Top-left concentric circles
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = circleRotation
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        ) {
            val strokeWidth = 1.5.dp.toPx()
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = 160.dp.toPx(),
                center = Offset(0f, 0f),
                style = Stroke(width = strokeWidth)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 240.dp.toPx(),
                center = Offset(0f, 0f),
                style = Stroke(width = strokeWidth)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = 330.dp.toPx(),
                center = Offset(0f, 0f),
                style = Stroke(width = strokeWidth)
            )
        }

        // Bottom wave dots pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val dotRadius = 1.2.dp.toPx()
            val pointsCount = 120
            val linesCount = 8
            
            for (line in 0 until linesCount) {
                val yOffsetBase = height * 0.82f + (line * 16.dp.toPx())
                val amplitude = 32.dp.toPx() + (line * 6.dp.toPx())
                val speedMultiplier = 1f + (line * 0.15f)
                val phaseOffset = line * 0.4f
                
                val pathColor = blueAccent.copy(alpha = 0.4f - (line * 0.04f).coerceAtLeast(0.05f))
                
                for (i in 0..pointsCount) {
                    val x = (i.toFloat() / pointsCount) * width
                    val angle = (x / width) * 4 * Math.PI.toFloat() + (wavePhase * speedMultiplier) + phaseOffset
                    val y = yOffsetBase + sin(angle.toDouble()).toFloat() * amplitude
                    
                    drawCircle(
                        color = pathColor,
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(224.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        translationY = logoFloatOffset.dp.toPx()
                    },
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                lightBlueAccent.copy(alpha = glowAlpha.value),
                                Color.Transparent
                            )
                        ),
                        radius = size.width / 1.1f
                    )
                }
                
                // Custom Logo Drawing
                Canvas(modifier = Modifier.size(175.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val center = Offset(canvasWidth / 2, canvasHeight / 2)
                    
                    // Outer split circle
                    val strokeW = 7.dp.toPx()
                    
                    // Left/Top Gold Arc
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(premiumGold, goldAccent),
                            start = Offset(0f, 0f),
                            end = Offset(canvasWidth, canvasHeight)
                        ),
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                    
                    // Right/Bottom Blue Arc
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(lightBlueAccent, blueAccent),
                            start = Offset(canvasWidth, 0f),
                            end = Offset(0f, canvasHeight)
                        ),
                        startAngle = 40f,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                    
                    // Bars
                    val barWidth = 15.5.dp.toPx()
                    val barSpacing = 9.dp.toPx()
                    val startX = center.x - (barWidth * 2) - (barSpacing * 1.5f)
                    val baseLine = center.y + 44.dp.toPx()
                    
                    val heights = listOf(33.dp.toPx(), 49.dp.toPx(), 71.dp.toPx(), 93.dp.toPx())
                    
                    for (i in 0..3) {
                        val h = heights[i]
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(lightBlueAccent, blueAccent),
                                startY = baseLine - h,
                                endY = baseLine
                            ),
                            topLeft = Offset(startX + (i * (barWidth + barSpacing)), baseLine - h),
                            size = Size(barWidth, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                    
                    // Upward Curved Arrow
                    val arrowPath = androidx.compose.ui.graphics.Path().apply {
                        val startArrowX = startX - 11.dp.toPx()
                        val startArrowY = baseLine + 11.dp.toPx()
                        val endArrowX = startX + (4 * (barWidth + barSpacing)) + 11.dp.toPx()
                        val endArrowY = baseLine - 99.dp.toPx()
                        
                        moveTo(startArrowX, startArrowY)
                        quadraticBezierTo(
                            center.x + 11.dp.toPx(), baseLine + 11.dp.toPx(),
                            endArrowX, endArrowY
                        )
                    }
                    
                    drawPath(
                        path = arrowPath,
                        brush = Brush.linearGradient(
                            colors = listOf(premiumGold, goldAccent)
                        ),
                        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Arrow Head
                    val headPath = androidx.compose.ui.graphics.Path().apply {
                        val tipX = startX + (4 * (barWidth + barSpacing)) + 13.dp.toPx()
                        val tipY = baseLine - 101.dp.toPx()
                        moveTo(tipX, tipY)
                        lineTo(tipX - 27.5.dp.toPx(), tipY + 5.5.dp.toPx())
                        lineTo(tipX - 13.dp.toPx(), tipY + 24.dp.toPx())
                        close()
                    }
                    
                    drawPath(
                        path = headPath,
                        brush = Brush.linearGradient(
                            colors = listOf(premiumGold, goldAccent)
                        )
                    )
                }
                
                // Rupee Symbol
                Text(
                    text = "₹",
                    color = premiumGold,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.offset(y = (-22).dp, x = (-11).dp)
                )
            }
            
            Spacer(modifier = Modifier.height(36.dp))
            
            // Title
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("LoanMaster ")
                    }
                    withStyle(style = SpanStyle(color = premiumGold)) {
                        append("Pro")
                    }
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    translationY = titleOffsetY.value.dp.toPx()
                }
            )
            
            Spacer(modifier = Modifier.height(18.dp))
            
            // Divider with glowing dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(subtitleAlpha.value)
            ) {
                Box(modifier = Modifier.width(66.dp).height(1.dp).background(premiumGold.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(premiumGold, shape = androidx.compose.foundation.shape.CircleShape)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(premiumGold, Color.Transparent)
                            ),
                            radius = size.width * 2
                        )
                        drawCircle(
                            color = premiumGold,
                            radius = size.width / 2
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(66.dp).height(1.dp).background(premiumGold.copy(alpha = 0.5f)))
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            // Subtitle
            Text(
                text = "Smart Finance Calculator",
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 17.sp,
                letterSpacing = 1.2.sp,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
            
            Spacer(modifier = Modifier.height(72.dp))
            
            // Premium Loader
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = loaderRotation
                            alpha = loaderAlpha.value
                        }
                ) {
                    // Glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(premiumGold.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        radius = size.width / 1.5f
                    )
                    
                    // Arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.Transparent, premiumGold.copy(alpha = 0.5f), goldAccent)
                        ),
                        startAngle = -90f,
                        sweepAngle = 300f,
                        useCenter = false,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                // Inner dot for premium feel
                Canvas(
                    modifier = Modifier
                        .size(6.dp)
                        .graphicsLayer {
                            alpha = loaderAlpha.value
                        }
                ) {
                    drawCircle(color = premiumGold.copy(alpha = 0.5f))
                }
            }
        }
    }
}
