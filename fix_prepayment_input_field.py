import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """fun PremiumInputField(label: String, symbol: String, value: String, onValueChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column {
        Text(label, color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(Color.Black.copy(alpha = 0.2f))
                .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = LoanMasterTheme.typography.title.fontSize,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),"""

replacement = """fun PremiumInputField(label: String, symbol: String, value: String, onValueChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }
    
    val targetBorderColor = if (isFocused) AccentBlue else CardStroke
    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = targetBorderColor, label = "borderColor")
    val targetBorderWidth = if (isFocused) 2.dp else 1.dp
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = targetBorderWidth, label = "borderWidth")

    Column {
        Text(label, color = if (isFocused) AccentBlue else TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
        Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
                .background(Color.Black.copy(alpha = 0.2f))
                .border(borderWidth, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.md))
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.md),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = LoanMasterTheme.typography.title.fontSize,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),"""

content = content.replace(target, replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
