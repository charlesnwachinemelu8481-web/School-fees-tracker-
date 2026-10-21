package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Student
import com.example.ui.components.StatusBadge
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
import com.example.ui.theme.WarningRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    students: List<Student>,
    preselectedStudent: Student?,
    nextReceiptNumber: String,
    onSavePayment: (studentId: String, amount: Double, term: String, category: String, method: String, ref: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStudent by remember(preselectedStudent) {
        mutableStateOf(preselectedStudent ?: students.firstOrNull { it.balance > 0 } ?: students.firstOrNull())
    }

    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var studentSearchText by remember { mutableStateOf("") }

    var amountText by remember(selectedStudent) {
        mutableStateOf(
            if (selectedStudent != null && selectedStudent!!.balance > 0) {
                String.format(Locale.US, "%.2f", selectedStudent!!.balance)
            } else "500.00"
        )
    }

    val paymentMethods = listOf("Bank Transfer", "POS / Card", "Online Portal", "Cash", "Cheque")
    var selectedMethod by remember { mutableStateOf("Bank Transfer") }

    val terms = listOf("Term 1", "Term 2", "Term 3")
    var selectedTerm by remember { mutableStateOf("Term 1") }
    var termDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Full Clearance", "Tuition", "Books & Uniform", "STEM & Lab", "Sports & Activities", "Development Levy")
    var selectedCategory by remember { mutableStateOf("Full Clearance") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val todayDate = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
    }
    var paymentDate by remember { mutableStateOf(todayDate) }

    var referenceNo by remember {
        mutableStateOf("TXN-" + (100000 + (1..899999).random()))
    }
    var notes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column {
                Text(
                    text = "Record Fee Payment",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Issue an official payment credit and generate an institutional bursary receipt",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Form Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Receipt Number Preview Ribbon
                    Surface(
                        color = DeepTealContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = OnDeepTealContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Auto-Generated Receipt Number",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OnDeepTealContainer
                                )
                            }

                            Text(
                                text = nextReceiptNumber,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepTeal
                            )
                        }
                    }

                    // 1. Student Search & Select
                    Column {
                        Text(
                            text = "1. Select Student",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ExposedDropdownMenuBox(
                            expanded = studentDropdownExpanded,
                            onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedStudent?.let { "${it.fullName} (${it.admissionNo} • ${it.gradeClass})" } ?: "Select student...",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Student Name & ID") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = DeepTeal)
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("record_pay_student_select")
                            )

                            ExposedDropdownMenu(
                                expanded = studentDropdownExpanded,
                                onDismissRequest = { studentDropdownExpanded = false }
                            ) {
                                students.forEach { stu ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(stu.fullName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                                    Text("${stu.admissionNo} • ${stu.gradeClass}", fontSize = 12.sp, color = TextMuted)
                                                }
                                                Text(
                                                    text = "Bal: $${String.format("%.0f", stu.balance)}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (stu.balance > 0) WarningRed else SuccessGreen
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedStudent = stu
                                            studentDropdownExpanded = false
                                            amountText = String.format(Locale.US, "%.2f", stu.balance.coerceAtLeast(100.0))
                                        }
                                    )
                                }
                            }
                        }

                        // Selected student summary chip
                        selectedStudent?.let { stu ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = BackgroundSlate,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total Expected: $${String.format("%.2f", stu.totalFee)}", fontSize = 12.sp, color = TextSecondary)
                                        Text("Already Paid: $${String.format("%.2f", stu.amountPaid)}", fontSize = 12.sp, color = SuccessGreen)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Current Balance", fontSize = 11.sp, color = TextMuted)
                                        Text(
                                            "$${String.format("%.2f", stu.balance)}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (stu.balance > 0) WarningRed else SuccessGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = BorderSlate)

                    // 2. Amount and Quick Presets
                    Column {
                        Text(
                            text = "2. Payment Amount ($)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Amount Received ($)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("record_pay_amount_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Amount preset pills
                        selectedStudent?.let { stu ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        amountText = String.format(Locale.US, "%.2f", stu.balance)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Full ($${String.format("%.0f", stu.balance)})", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        amountText = String.format(Locale.US, "%.2f", stu.balance / 2.0)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("50% ($${String.format("%.0f", stu.balance / 2.0)})", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        amountText = "850.00"
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tuition ($850)", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = BorderSlate)

                    // 3. Payment Method
                    Column {
                        Text(
                            text = "3. Payment Method",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            paymentMethods.take(3).forEach { method ->
                                FilterChip(
                                    selected = selectedMethod == method,
                                    onClick = { selectedMethod = method },
                                    label = { Text(method, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DeepTealContainer,
                                        selectedLabelColor = OnDeepTealContainer
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            paymentMethods.drop(3).forEach { method ->
                                FilterChip(
                                    selected = selectedMethod == method,
                                    onClick = { selectedMethod = method },
                                    label = { Text(method, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DeepTealContainer,
                                        selectedLabelColor = OnDeepTealContainer
                                    )
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = BorderSlate)

                    // 4. Term & Category Allocation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Term Dropdown
                        ExposedDropdownMenuBox(
                            expanded = termDropdownExpanded,
                            onExpandedChange = { termDropdownExpanded = !termDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedTerm,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Academic Term") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = termDropdownExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = termDropdownExpanded,
                                onDismissRequest = { termDropdownExpanded = false }
                            ) {
                                terms.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t) },
                                        onClick = {
                                            selectedTerm = t
                                            termDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = categoryDropdownExpanded,
                            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Allocation Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = categoryDropdownExpanded,
                                onDismissRequest = { categoryDropdownExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            categoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 5. Date & Reference
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = paymentDate,
                            onValueChange = { paymentDate = it },
                            label = { Text("Payment Date") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = DeepTeal) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = referenceNo,
                            onValueChange = { referenceNo = it },
                            label = { Text("Txn / Bank Ref No") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 6. Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Bursar Notes / Memo (Optional)") },
                        placeholder = { Text("e.g., Parent confirmation, counter deposit note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 7. Save Payment Button
                    Button(
                        onClick = {
                            val student = selectedStudent
                            val amountVal = amountText.toDoubleOrNull() ?: 0.0
                            if (student != null && amountVal > 0) {
                                onSavePayment(
                                    student.id,
                                    amountVal,
                                    selectedTerm,
                                    selectedCategory,
                                    selectedMethod,
                                    referenceNo,
                                    notes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                        shape = RoundedCornerShape(10.dp),
                        enabled = selectedStudent != null && (amountText.toDoubleOrNull() ?: 0.0) > 0.0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_payment_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Payment & Issue Receipt",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
