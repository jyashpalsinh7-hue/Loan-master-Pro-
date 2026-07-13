import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """@Composable
private fun CustomInput(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, prefix: String = "", suffix: String = "") {
    Column {
        Text(label, color = TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.buttonHeight).background(Color.Transparent).border(1.dp, StrokeNavy, RoundedCornerShape(LoanMasterTheme.spacing.sm)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                if (prefix.isNotEmpty()) {
                    Text(prefix, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f).fillMaxHeight().wrapContentHeight(Alignment.CenterVertically)
                )"""

replacement = """@Composable
private fun CustomInput(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, prefix: String = "", suffix: String = "") {
    var isFocused by remember { mutableStateOf(false) }
    
    val targetBorderColor = if (isFocused) AccentGreen else StrokeNavy
    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = targetBorderColor, label = "borderColor")
    val targetBorderWidth = if (isFocused) 2.dp else 1.dp
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = targetBorderWidth, label = "borderWidth")

    Column {
        Text(label, color = if (isFocused) AccentGreen else TextSec, fontSize = LoanMasterTheme.typography.label.fontSize, lineHeight = LoanMasterTheme.typography.body.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.xs))
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = LoanMasterTheme.components.buttonHeight).background(Color.Transparent).border(borderWidth, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.sm)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = LoanMasterTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
                if (prefix.isNotEmpty()) {
                    Text(prefix, color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f).fillMaxHeight().wrapContentHeight(Alignment.CenterVertically).androidx.compose.ui.focus.onFocusChanged { isFocused = it.isFocused }
                )"""

content = content.replace(target, replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
