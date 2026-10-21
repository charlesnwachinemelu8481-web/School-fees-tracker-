package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DashboardStats
import com.example.model.PaymentRecord
import com.example.model.ScreenNav
import com.example.ui.components.CollectionProgressBar
import com.example.ui.components.StatCard
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepTeal
import com.example.ui.theme.DeepTealContainer
import com.example.ui.theme.OnDeepTealContainer
import com.example.ui.theme.OnSuccessGreen
import com.example.ui.theme.OnWarningAmber
import com.example.ui.theme.OnWarningRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningRed

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    recentPayments: List<PaymentRecord>,
    onNavigate: (ScreenNav) -> Unit,
    onRecordPaymentClick: () -> Unit,
    onViewReceipt: (PaymentRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Greeting & Quick Action Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Financial Overview",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Current Academic Session 2026/2027 • Term 1",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onRecordPaymentClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("dashboard_record_payment_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Payment", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Stat Cards (Tablet-first responsive grid)
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 800.dp
                if (isWide) {
                    // 4 columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        StatCard(
                            title = "Total Expected",
                            value = "$${String.format("%,.2f", stats.totalExpected)}",
                            subtext = "${stats.totalStudents} Enrolled Students",
                            icon = Icons.Default.MonetizationOn,
                            accentColor = DeepTeal,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Collected",
                            value = "$${String.format("%,.2f", stats.totalCollected)}",
                            subtext = "${(stats.collectionRate * 100).toInt()}% realization rate",
                            icon = Icons.Default.TrendingUp,
                            accentColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Outstanding Balance",
                            value = "$${String.format("%,.2f", stats.outstandingBalance)}",
                            subtext = "Immediate receivables",
                            icon = Icons.Default.AccountBalanceWallet,
                            accentColor = WarningRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Students in Default",
                            value = "${stats.studentsInDefault}",
                            subtext = "Owing or partial balance",
                            icon = Icons.Default.Warning,
                            accentColor = WarningAmber,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(ScreenNav.STUDENTS) }
                        )
                    }
                } else {
                    // 2x2 grid for compact / foldables
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Total Expected",
                                value = "$${String.format("%,.2f", stats.totalExpected)}",
                                subtext = "${stats.totalStudents} Students",
                                icon = Icons.Default.MonetizationOn,
                                accentColor = DeepTeal,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Total Collected",
                                value = "$${String.format("%,.2f", stats.totalCollected)}",
                                subtext = "${(stats.collectionRate * 100).toInt()}% collected",
                                icon = Icons.Default.TrendingUp,
                                accentColor = SuccessGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Outstanding Balance",
                                value = "$${String.format("%,.2f", stats.outstandingBalance)}",
                                subtext = "Immediate receivables",
                                icon = Icons.Default.AccountBalanceWallet,
                                accentColor = WarningRed,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Students in Default",
                                value = "${stats.studentsInDefault}",
                                subtext = "Owing or partial",
                                icon = Icons.Default.Warning,
                                accentColor = WarningAmber,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(ScreenNav.STUDENTS) }
                            )
                        }
                    }
                }
            }
        }

        // Collection Rate Progress Bar Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    CollectionProgressBar(
                        rate = stats.collectionRate,
                        target = 0.90f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Collected: $${String.format("%,.2f", stats.totalCollected)} of $${String.format("%,.2f", stats.totalExpected)}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "Remaining: $${String.format("%,.2f", stats.outstandingBalance)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningRed
                        )
                    }
                }
            }
        }

        // Recent Payments Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = DeepTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recent Payments Feed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                OutlinedButton(
                    onClick = { onNavigate(ScreenNav.PAYMENT_HISTORY) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Full Ledger", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Recent Payments Items (Hybrid Card/Table Rows)
        items(recentPayments.take(6)) { payment ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewReceipt(payment) }
                    .testTag("recent_payment_${payment.receiptNo}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeepTealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = OnDeepTealContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = payment.studentName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = payment.studentClass,
                                    fontSize = 12.sp,
                                    color = DeepTeal,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(" • ", fontSize = 12.sp, color = TextMuted)
                                Text(
                                    text = payment.paymentMethod,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(" • ", fontSize = 12.sp, color = TextMuted)
                                Text(
                                    text = payment.receiptNo,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+$${String.format("%.2f", payment.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SuccessGreen
                        )
                        Text(
                            text = payment.paymentDate,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
