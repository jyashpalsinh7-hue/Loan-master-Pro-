package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ExportUtils {
    fun exportToPdf(context: Context, title: String, content: List<Pair<String, String>>) {
        try {
            val document = PdfDocument()
            val pageWidth = 595f
            val pageHeight = 842f
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth.toInt(), pageHeight.toInt(), 1).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            
            // Premium Dark Theme Colors
            val bgDark = Color.rgb(10, 15, 36) // Very dark blue/slate
            val cardDark = Color.rgb(25, 33, 60) // Slightly lighter for cards
            val textPrimary = Color.WHITE
            val textSecondary = Color.rgb(156, 163, 175) // Cool gray
            val accentGreen = Color.rgb(16, 185, 129) // Emerald
            val accentBlue = Color.rgb(59, 130, 246) // Blue
            val accentPurple = Color.rgb(139, 92, 246) // Violet
            val accentOrange = Color.rgb(245, 158, 11) // Amber
            val borderLight = Color.rgb(45, 55, 72) // Subtle border

            var pageNumber = 1

            fun drawBackgroundAndHeader() {
                // Main Background
                paint.shader = null
                paint.color = bgDark
                paint.style = Paint.Style.FILL
                canvas.drawRect(0f, 0f, pageWidth, pageHeight, paint)

                // Header Gradient
                paint.shader = LinearGradient(0f, 0f, pageWidth, 180f, 
                    intArrayOf(Color.rgb(30, 58, 138), Color.rgb(88, 28, 135)), 
                    null, Shader.TileMode.CLAMP)
                
                // Draw curved header
                val path = Path()
                path.moveTo(0f, 0f)
                path.lineTo(pageWidth, 0f)
                path.lineTo(pageWidth, 120f)
                path.quadTo(pageWidth / 2, 180f, 0f, 120f)
                path.close()
                canvas.drawPath(path, paint)
                paint.shader = null

                // Title
                paint.color = textPrimary
                paint.textSize = 28f
                paint.isFakeBoldText = true
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(title, pageWidth / 2, 60f, paint)
                
                // Date Subtitle
                paint.textSize = 12f
                paint.color = Color.rgb(209, 213, 219)
                paint.isFakeBoldText = false
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                val dateStr = LocalDateTime.now().format(formatter)
                canvas.drawText("Generated on $dateStr", pageWidth / 2, 85f, paint)
                paint.textAlign = Paint.Align.LEFT
                
                // Subtle Watermark
                paint.color = Color.argb(15, 255, 255, 255) // Very transparent white
                paint.textSize = 80f
                paint.isFakeBoldText = true
                paint.textAlign = Paint.Align.CENTER
                canvas.save()
                canvas.rotate(-45f, pageWidth / 2, pageHeight / 2)
                canvas.drawText("FINCALC REPORT", pageWidth / 2, pageHeight / 2, paint)
                canvas.restore()
                paint.textAlign = Paint.Align.LEFT
            }
            
            drawBackgroundAndHeader()
            
            var yPosition = 180f
            
            fun checkPageBreak(requiredSpace: Float) {
                if (yPosition + requiredSpace > pageHeight - 80f) {
                    // Draw Footer before breaking
                    paint.color = textSecondary
                    paint.textSize = 10f
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawText("Page $pageNumber | FinCalc Pro", pageWidth / 2, pageHeight - 40f, paint)
                    paint.textAlign = Paint.Align.LEFT
                    
                    document.finishPage(page)
                    
                    pageNumber++
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    drawBackgroundAndHeader()
                    yPosition = 180f
                }
            }
            
            for ((key, value) in content) {
                if (key.isEmpty() && value.isEmpty()) {
                    checkPageBreak(30f)
                    yPosition += 15f
                    // Draw a subtle dashed-like separator or a glowing line
                    paint.shader = LinearGradient(50f, yPosition, pageWidth - 50f, yPosition,
                        intArrayOf(bgDark, borderLight, bgDark), null, Shader.TileMode.CLAMP)
                    paint.strokeWidth = 1.5f
                    canvas.drawLine(50f, yPosition, pageWidth - 50f, yPosition, paint)
                    paint.shader = null
                    paint.strokeWidth = 0f
                    yPosition += 25f
                    continue
                }
                
                if (value.isEmpty()) {
                    // Section header
                    checkPageBreak(50f)
                    yPosition += 10f
                    paint.color = accentBlue
                    paint.isFakeBoldText = true
                    paint.textSize = 18f
                    canvas.drawText(key.uppercase(), 50f, yPosition, paint)
                    
                    // Small underline for section header
                    paint.color = accentPurple
                    canvas.drawRoundRect(50f, yPosition + 8f, 100f, yPosition + 10f, 2f, 2f, paint)
                    
                    paint.isFakeBoldText = false
                    yPosition += 40f
                } else {
                    // Key-value inside a premium card
                    checkPageBreak(60f)
                    
                    val isHighlight = key.contains("Total", true) || key.contains("EMI", true) || key.contains("Amount", true)
                    
                    // Card Background
                    paint.color = cardDark
                    val rect = RectF(40f, yPosition - 25f, pageWidth - 40f, yPosition + 25f)
                    canvas.drawRoundRect(rect, 12f, 12f, paint)
                    
                    // Left Accent Bar for visual interest
                    paint.color = if (isHighlight) accentGreen else accentBlue
                    val accentRect = RectF(40f, yPosition - 25f, 46f, yPosition + 25f)
                    val radii = floatArrayOf(12f, 12f, 0f, 0f, 0f, 0f, 12f, 12f)
                    val accentPath = Path().apply { addRoundRect(accentRect, radii, Path.Direction.CW) }
                    canvas.drawPath(accentPath, paint)
                    
                    // Key Text
                    paint.color = textSecondary
                    paint.textSize = 14f
                    canvas.drawText(key, 65f, yPosition + 5f, paint)
                    
                    // Value Text
                    paint.color = if (isHighlight) accentGreen else textPrimary
                    paint.isFakeBoldText = isHighlight
                    paint.textSize = if (isHighlight) 16f else 15f
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(value, pageWidth - 60f, yPosition + 5f, paint)
                    
                    paint.isFakeBoldText = false
                    paint.textAlign = Paint.Align.LEFT
                    
                    yPosition += 65f // Spacing between cards
                }
            }
            
            // Footer on last page
            paint.color = textSecondary
            paint.textSize = 10f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("Page $pageNumber | FinCalc Pro", pageWidth / 2, pageHeight - 40f, paint)
            paint.textAlign = Paint.Align.LEFT
            
            document.finishPage(page)
            
            // Save to cache dir
            val file = File(context.cacheDir, "${title.replace(" ", "_")}_Report.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            
            // Share
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share PDF"))
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
        }
    }
}

