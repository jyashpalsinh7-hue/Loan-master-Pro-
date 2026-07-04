import re

with open("app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt", "r") as f:
    content = f.read()

start_idx = content.find("fun PremiumChartSection(")
end_idx = content.find("fun MarketRateCard(")

if start_idx != -1 and end_idx != -1:
    old_section = content[start_idx:end_idx]
    
    new_section = """fun PremiumChartSection(viewModel: CurrencyViewModel, exchangeRate: Double, baseCurrency: String, targetCurrency: String) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab = uiState.selectedTab
    
    val dataPoints = uiState.chartPoints
    val minVal = uiState.chartMinVal
    val maxVal = uiState.chartMaxVal
    val range = if (maxVal > minVal) maxVal - minVal else exchangeRate * 0.01
    val paddedMin = minVal - range * 0.1
    val paddedMax = maxVal + range * 0.1
    val paddedRange = if (paddedMax > paddedMin) paddedMax - paddedMin else 1.0
    
    val trendIsUp = uiState.chartTrendPercent >= 0
    val trendColor = if (trendIsUp) Color(0xFF4ADE80) else Color(0xFFFF5252)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ResponsiveUtils.CardShape)
            .background(CurrSurfaceColor)
            .border(1.dp, CurrCardStrokeColor.copy(alpha = 0.3f), ResponsiveUtils.CardShape)
            .padding(vertical = LoanMasterTheme.spacing.lg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoanMasterTheme.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isChartLoading) {
                CircularProgressIndicator(color = CurrSecondaryAccent, modifier = Modifier.size(LoanMasterTheme.components.iconSmall))
            } else {
                Text(
                    text = "${String.format(Locale.US, "%+.2f", uiState.chartTrendPercent)}%",
                    color = trendColor,
                    fontSize = LoanMasterTheme.typography.title.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(LoanMasterTheme.spacing.sm))
                    .background(CurrBgColor)
                    .padding(LoanMasterTheme.spacing.xs)
            ) {
                listOf("1D", "1W", "1M", "1Y", "5Y").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(LoanMasterTheme.spacing.xs))
                            .background(if (isSelected) CurrSurfaceColor else Color.Transparent)
                            .clickable { viewModel.onEvent(CurrencyEvent.TabSelected(tab)) }
                            .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.xs)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else ResponsiveUtils.TextSecondary,
                            fontSize = LoanMasterTheme.typography.label.fontSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(LoanMasterTheme.spacing.lg))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (dataPoints.size > 1) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    val path = Path()
                    var lastX = 0f
                    var lastY = 0f
                    
                    dataPoints.forEachIndexed { index, point ->
                        val x = (index.toFloat() / (dataPoints.size - 1)) * width
                        val y = height - ((point - paddedMin) / paddedRange * height).toFloat()
                        
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                        lastX = x
                        lastY = y
                    }
                    
                    val gradient = Brush.verticalGradient(
                        colors = listOf(trendColor.copy(alpha = 0.3f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                    
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(lastX, height)
                        lineTo(0f, height)
                        close()
                    }
                    
                    drawPath(path = fillPath, brush = gradient)
                    drawPath(
                        path = path,
                        color = trendColor,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
"""
    content = content[:start_idx] + new_section + content[end_idx:]

with open("app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt", "w") as f:
    f.write(content)

