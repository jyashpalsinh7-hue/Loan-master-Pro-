import os

file_path = "app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """    val handleValueChange: (String) -> Unit = remember(onValueChange) {
        { newValue ->
            val sanitized = newValue.replace("-", "").replace(",", "")
            onValueChange(sanitized)
        }
    }

    val hasError = finalErrorMessage != null
    val strokeColor = if (hasError) colors.error else colors.outlineVariant
    val textColor = if (hasError) colors.error else colors.onSurface
    val iconColor = if (hasError) colors.error else iconTint
    val cursorColor = if (hasError) colors.error else colors.primary

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = colors.onSurfaceVariant, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.85f)
            if (infoText != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info about $label",
                    tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(LoanMasterTheme.spacing.md)
                        .clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) { showInfoDialog = true }
                )
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, strokeColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.gridGutter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f))
                    androidx.compose.foundation.layout.Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
                    androidx.compose.foundation.text.BasicTextField(
                        value = value,
                        onValueChange = handleValueChange,
                        readOnly = readOnly,
                        enabled = onClick == null,
                        textStyle = TextStyle(color = textColor, fontSize = LoanMasterTheme.typography.body.fontSize),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(cursorColor),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    ) { innerTextField ->"""

replacement = """    val handleValueChange: (String) -> Unit = remember(onValueChange) {
        { newValue ->
            val sanitized = newValue.replace("-", "").replace(",", "")
            onValueChange(sanitized)
        }
    }

    var isFocused by remember { mutableStateOf(false) }

    val hasError = finalErrorMessage != null
    val targetStrokeColor = if (hasError) colors.error else if (isFocused) colors.primary else colors.outlineVariant
    val textColor = if (hasError) colors.error else colors.onSurface
    val targetIconColor = if (hasError) colors.error else if (isFocused) colors.primary else iconTint
    val cursorColor = if (hasError) colors.error else colors.primary
    val targetBorderWidth = if (isFocused) 2.dp else 1.dp

    val strokeColor by androidx.compose.animation.animateColorAsState(targetValue = targetStrokeColor, label = "strokeColor")
    val iconColor by androidx.compose.animation.animateColorAsState(targetValue = targetIconColor, label = "iconColor")
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = targetBorderWidth, label = "borderWidth")

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = if (isFocused) colors.primary else colors.onSurfaceVariant, fontSize = LoanMasterTheme.typography.body.fontSize.value.sp * 0.85f)
            if (infoText != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info about $label",
                    tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(LoanMasterTheme.spacing.md)
                        .clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) { showInfoDialog = true }
                )
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(LoanMasterTheme.spacing.md),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(borderWidth, strokeColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.gridGutter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(LoanMasterTheme.components.iconMedium.value.dp * 0.8f))
                    androidx.compose.foundation.layout.Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.gridGutter))
                    androidx.compose.foundation.text.BasicTextField(
                        value = value,
                        onValueChange = handleValueChange,
                        readOnly = readOnly,
                        enabled = onClick == null,
                        textStyle = TextStyle(color = textColor, fontSize = LoanMasterTheme.typography.body.fontSize),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(cursorColor),
                        modifier = Modifier.weight(1f).onFocusChanged { isFocused = it.isFocused },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    ) { innerTextField ->"""

content = content.replace(target, replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
