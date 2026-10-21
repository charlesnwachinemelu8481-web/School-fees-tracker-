package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClassCollectionStat
import com.example.model.PaymentMethodStat
import com.example.model.Student
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepTeal
import com.example.ui.theme.DeepTealContainer
import com.example.ui.theme.OnDeepTealContainer
import com.example.ui.theme.OnSuccessGreen
import com.example.ui.theme.OnWarningRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardSubtle
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningRed
import com.example.ui.theme.WarningRedContainer

@Composable
fun ReportsScreen(
    classStats: List<ClassCollectionStat>,
    methodStats: List<PaymentMethodStat>,
    defaultingStudents: List<Student>,
    onSelectStudent: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val totalExpectedAll = classStats.sumOf { it.expected }
    val totalCollectedAll = classStats.sumOf { it.collected }
    val totalOutstandingAll = classStats.sumOf { it.outstanding }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Executive Financial Reports",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Class-level collection metrics, revenue streams, and default management",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = {
                        val reportText = buildString {
                            append("FeeTrack Institutional Financial Report\n")
                            append("Academic Session 2026/2027 • Term 1\n\n")
                            append("Total Expected Revenue: $${String.format("%,.2f", totalExpectedAll)}\n")
                            append("Total Collected to Date: $${String.format("%,.2f", totalCollectedAll)}\n")
                            append("Outstanding Default Balance: $${String.format("%,.2f", totalOutstandingAll)}\n\n")
                            append("Class Breakdown:\n")
                            classStats.forEach {
                                append("${it.gradeClass}: Expected $${it.expected.toInt()}, Collected $${it.collected.toInt()} (${if (it.expected > 0) (it.collected / it.expected * 100).toInt() else 0}%)\n")
                            }
                        }
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Financial Report"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Report", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // 1. Bar Chart: Collections by Class
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = DeepTeal, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Collections by Class",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Expected vs Collected fees across grades",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Chart Legend
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(DeepTeal))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Collected", fontSize = 11.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(BorderSlate))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Expected", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Custom Canvas Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val maxVal = (classStats.maxOfOrNull { it.expected } ?: 3000.0).toFloat().coerceAtLeast(100f)
                            val bottomY = h - 35f
                            val chartHeight = bottomY - 20f

                            // Draw baseline
                            drawLine(
                                color = BorderSlate,
                                start = Offset(0f, bottomY),
                                end = Offset(w, bottomY),
                                strokeWidth = 2f
                            )

                            val count = classStats.size.coerceAtLeast(1)
                            val barGroupWidth = w / count
                            val singleBarWidth = (barGroupWidth * 0.32f).coerceAtMost(28f)

                            classStats.forEachIndexed { i, stat ->
                                val groupCenterX = i * barGroupWidth + (barGroupWidth / 2f)

                                val expBarH = ((stat.expected.toFloat() / maxVal) * chartHeight).coerceAtLeast(4f)
                                val colBarH = ((stat.collected.toFloat() / maxVal) * chartHeight).coerceAtLeast(4f)

                                val expX = groupCenterX - singleBarWidth - 2f
                                val colX = groupCenterX + 2f

                                // Expected bar (light grey/slate)
                                drawRect(
                                    color = BorderSlate,
                                    topLeft = Offset(expX, bottomY - expBarH),
                                    size = Size(singleBarWidth, expBarH)
                                )

                                // Collected bar (Deep Teal)
                                drawRect(
                                    color = DeepTeal,
                                    topLeft = Offset(colX, bottomY - colBarH),
                                    size = Size(singleBarWidth, colBarH)
                                )

                                // Class label
                                val label = stat.gradeClass.replace("Grade ", "G")
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.parseColor("#475569")
                                    textSize = 28f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    isAntiAlias = true
                                }
                                drawContext.canvas.nativeCanvas.drawText(
                                    label,
                                    groupCenterX,
                                    bottomY + 28f,
                                    paint
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Pie / Donut Chart: Payment Methods Distribution
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = DeepTeal, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Revenue Distribution by Payment Method",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Breakdown of channels used by parents & guardians",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val methodColors = listOf(
                        DeepTeal,
                        TealAccent,
                        Color(0xFF3B82F6),
                        Color(0xFF10B981),
                        Color(0xFFF59E0B),
                        Color(0xFF8B5CF6)
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isWide = maxWidth >= 600.dp
                        if (isWide) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Donut Canvas
                                DonutChartCanvas(
                                    methodStats = methodStats,
                                    methodColors = methodColors,
                                    modifier = Modifier
                                        .size(190.dp)
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.width(24.dp))

                                // Legend
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    methodStats.forEachIndexed { index, stat ->
                                        val color = methodColors.getOrElse(index) { DeepTeal }
                                        MethodLegendItem(stat = stat, color = color)
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                DonutChartCanvas(
                                    methodStats = methodStats,
                                    methodColors = methodColors,
                                    modifier = Modifier.size(180.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    methodStats.forEachIndexed { index, stat ->
                                        val color = methodColors.getOrElse(index) { DeepTeal }
                                        MethodLegendItem(stat = stat, color = color)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Outstanding Balance Leaderboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(WarningRedContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = OnWarningRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Outstanding Balance Leaderboard",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Top accounts requiring bursary follow-up and payment notices",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Surface(
                            color = WarningRedContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${defaultingStudents.size} In Default",
                                color = OnWarningRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSlate)
                    Spacer(modifier = Modifier.height(10.dp))

                    defaultingStudents.take(8).forEachIndexed { rank, student ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectStudent(student) }
                                .padding(vertical = 10.dp)
                                .testTag("leaderboard_row_${student.id}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = if (rank < 3) WarningRedContainer else SurfaceCardSubtle,
                                    shape = CircleShape,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${rank + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (rank < 3) OnWarningRed else TextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = student.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${student.admissionNo} • ${student.gradeClass} • Guardian: ${student.guardianName}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%,.2f", student.balance)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = WarningRed
                                    )
                                    Text(
                                        text = "Paid: $${String.format("%.0f", student.amountPaid)}",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        val smsIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Dear ${student.guardianName}, this is a gentle fee reminder from Oakwood Academy Bursary regarding ${student.fullName} (${student.gradeClass}). Current outstanding balance: $${String.format("%.2f", student.balance)}. Please clear prior to term exams. Thank you."
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(smsIntent, "Send Reminder Notice"))
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Notice", fontSize = 11.sp)
                                }
                            }
                        }
                        HorizontalDivider(color = BorderSlate.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChartCanvas(
    methodStats: List<PaymentMethodStat>,
    methodColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = methodStats.sumOf { it.amount }.toFloat().coerceAtLeast(1f)
        var startAngle = -90f
        val strokeW = size.width * 0.18f

        methodStats.forEachIndexed { index, stat ->
            val sweep = (stat.amount.toFloat() / total) * 360f
            val color = methodColors.getOrElse(index) { DeepTeal }

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep - 1.5f,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun MethodLegendItem(
    stat: PaymentMethodStat,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stat.method,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "(${stat.count} txns)",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$${String.format("%,.2f", stat.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${(stat.percentage * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
