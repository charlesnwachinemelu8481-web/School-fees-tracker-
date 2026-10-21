package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentRecord
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepTeal
import com.example.ui.theme.DeepTealContainer
import com.example.ui.theme.OnDeepTealContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PaymentHistoryScreen(
    payments: List<PaymentRecord>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedMethod: String,
    onMethodChange: (String) -> Unit,
    selectedDateRange: String,
    onDateRangeChange: (String) -> Unit,
    onViewReceipt: (PaymentRecord) -> Unit,
    onExportCsv: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val methodOptions = listOf("All Methods", "Bank Transfer", "POS / Card", "Online Portal", "Cash", "Cheque")
    val dateRangeOptions = listOf("All Time", "Today", "This Week")

    val totalFilteredAmount = payments.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Export Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Payment Transaction Log",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${payments.size} receipts recorded • Total: $${String.format("%,.2f", totalFilteredAmount)}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = {
                        val csv = onExportCsv()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csv)
                            putExtra(Intent.EXTRA_TITLE, "FeeTrack_Ledger_Export.csv")
                            type = "text/csv"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export Financial Ledger"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("export_payments_btn")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Filter Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search by receipt number, student name, reference...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = DeepTeal)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_search_input")
                    )

                    // Date range filter
                    Text(
                        text = "Date Filter:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dateRangeOptions.forEach { range ->
                            FilterChip(
                                selected = selectedDateRange == range,
                                onClick = { onDateRangeChange(range) },
                                label = { Text(range, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepTealContainer,
                                    selectedLabelColor = OnDeepTealContainer
                                )
                            )
                        }
                    }

                    // Payment Method filter
                    Text(
                        text = "Payment Method:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(methodOptions) { method ->
                            FilterChip(
                                selected = selectedMethod == method,
                                onClick = { onMethodChange(method) },
                                label = { Text(method, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepTealContainer,
                                    selectedLabelColor = OnDeepTealContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // Transaction Ledger Table / Cards
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 840.dp
                if (isWide) {
                    // Full Table on Tablet
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceCardSubtle)
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("RECEIPT #", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.4f))
                                Text("DATE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.1f))
                                Text("STUDENT & CLASS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(2f))
                                Text("METHOD", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.2f))
                                Text("CATEGORY", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.4f))
                                Text("AMOUNT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.2f))
                                Text("ACTION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                            }

                            HorizontalDivider(color = BorderSlate)

                            if (payments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No payment transactions found.", color = TextMuted, fontSize = 14.sp)
                                }
                            } else {
                                payments.forEachIndexed { index, payment ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onViewReceipt(payment) }
                                            .background(if (index % 2 == 1) SurfaceCardSubtle.copy(alpha = 0.35f) else Color.Transparent)
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                            .testTag("payment_row_${payment.receiptNo}"),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = payment.receiptNo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = DeepTeal,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.weight(1.4f)
                                        )

                                        Text(
                                            text = payment.paymentDate,
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.weight(1.1f)
                                        )

                                        Column(modifier = Modifier.weight(2f)) {
                                            Text(payment.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text(payment.studentClass, fontSize = 11.sp, color = TextMuted)
                                        }

                                        Text(
                                            text = payment.paymentMethod,
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.weight(1.2f)
                                        )

                                        Text(
                                            text = payment.category,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            modifier = Modifier.weight(1.4f)
                                        )

                                        Text(
                                            text = "+$${String.format("%,.2f", payment.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SuccessGreen,
                                            modifier = Modifier.weight(1.2f)
                                        )

                                        Box(modifier = Modifier.weight(1f)) {
                                            OutlinedButton(
                                                onClick = { onViewReceipt(payment) },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Receipt", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = BorderSlate.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                } else {
                    // Mobile Cards
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        payments.forEach { payment ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onViewReceipt(payment) }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(payment.receiptNo, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepTeal)
                                            Text(payment.paymentDate, fontSize = 11.sp, color = TextMuted)
                                        }
                                        Text(
                                            "+$${String.format("%,.2f", payment.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = SuccessGreen
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = BorderSlate)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(payment.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                            Text("${payment.studentClass} • ${payment.category}", fontSize = 12.sp, color = TextSecondary)
                                        }
                                        OutlinedButton(
                                            onClick = { onViewReceipt(payment) },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Receipt", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
