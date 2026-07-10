package com.loanmaster.pro.feature.loaneligibility.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.core.formatter.formatMoney
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.loaneligibility.util.*

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, animationSpec = tween(150), label = "")
    val elevation by animateDpAsState(targetValue = if (isPressed) 2.dp else 8.dp, animationSpec = tween(150), label = "")
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(96.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation, RoundedCornerShape(24.dp), spotColor = Color(0xFF061633).copy(alpha = 0.5f), ambientColor = Color(0xFF061633).copy(alpha = 0.2f)),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = contentColor),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Surface(
                color = contentColor.copy(alpha = 0.15f),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis, letterSpacing = 0.3.sp)
        }
    }
}
