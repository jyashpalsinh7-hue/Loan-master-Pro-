import re

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

foir_replacement = """            // 6. FOIR Visual Gauge
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(LoanMasterTheme.components.cardRadius),
                border = androidx.compose.foundation.BorderStroke(1.dp, surfaceColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(LoanMasterTheme.spacing.md)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Your Debt-to-Income Ratio (FOIR)", color = textColor, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                    Text("(Total EMIs / Total Income)", color = textSecondary, style = LoanMasterTheme.typography.label, modifier = Modifier.padding(top = 2.dp))
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.lg))
                    
                    Box(modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp)) {
                        val limit = (currentFoir.toFloat() / 100f).coerceIn(0f, 1f)
                        
                        // Percentage label above marker
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                             val offsetX = (maxWidth * limit) - 16.dp
                             Text(
                                 "${currentFoir.toInt()}%", 
                                 color = bgColor, 
                                 style = LoanMasterTheme.typography.label, 
                                 fontWeight = FontWeight.Bold,
                                 modifier = Modifier
                                    .offset(x = offsetX)
                                    .background(neonGreen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                             )
                        }
                        
                        // The track and thumb
                        Canvas(modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp).align(Alignment.BottomCenter).padding(bottom = 10.dp)) {
                            val w = size.width
                            val h = size.height
                            val trackY = h / 2
                            
                            // Track line
                            drawLine(color = Color.DarkGray, start = Offset(0f, trackY), end = Offset(w, trackY), strokeWidth = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            
                            // Ticks
                            for (i in 0..5) {
                                val tickX = w * (i * 0.2f)
                                drawLine(color = Color.DarkGray, start = Offset(tickX, trackY - 6f), end = Offset(tickX, trackY + 6f), strokeWidth = 2f)
                            }
                            
                            // Thumb
                            val markerX = limit * w
                            drawCircle(color = neonGreen, radius = 8.dp.toPx(), center = Offset(markerX, trackY))
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("20%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("40%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("60%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("80%", color = textSecondary, style = LoanMasterTheme.typography.label)
                        Text("100%", color = textSecondary, style = LoanMasterTheme.typography.label)
                    }

                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.md))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(neonGreen))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Low Risk (Safe)", color = textSecondary, style = LoanMasterTheme.typography.label)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(warningYellow))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("Moderate Risk", color = textSecondary, style = LoanMasterTheme.typography.label)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dangerRed))
                            Spacer(modifier = Modifier.widthIn(min = 4.dp))
                            Text("High Risk", color = textSecondary, style = LoanMasterTheme.typography.label)
                        }
                    }
                }
            }"""

# Find the old FOIR section and replace it
# We'll use regex to replace from "// 6. FOIR Visual Gauge" up to right before "// 7. Hero Results Dashboard"

pattern = r"// 6\. FOIR Visual Gauge.*?// 7\. Hero Results Dashboard"
content = re.sub(pattern, foir_replacement + "\n\n            // 7. Hero Results Dashboard", content, flags=re.DOTALL)

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
