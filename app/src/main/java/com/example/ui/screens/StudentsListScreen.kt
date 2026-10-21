package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FeeStatus
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

@Composable
fun StudentsListScreen(
    students: List<Student>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedClass: String,
    onClassFilterChange: (String) -> Unit,
    selectedStatus: String,
    onStatusFilterChange: (String) -> Unit,
    onStudentClick: (Student) -> Unit,
    onRecordPaymentClick: (Student) -> Unit,
    onAddStudent: (String, String, String, String, String, String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddStudentDialog by remember { mutableStateOf(false) }

    val classOptions = listOf(
        "All Classes", "Grade 7A", "Grade 7B", "Grade 8A", "Grade 8B",
        "Grade 9A", "Grade 10 Sci", "Grade 11 Arts", "Grade 12 Sci"
    )
    val statusOptions = listOf("All", "Paid", "Partial", "Owing")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Student Fee Directory",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${students.size} students match criteria",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showAddStudentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_student_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Student", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Search & Filters Card
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
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search by student name, admission number, guardian...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = DeepTeal)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_search_input")
                    )

                    // Class Chips
                    Text(
                        text = "Filter by Class:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(classOptions) { cls ->
                            FilterChip(
                                selected = selectedClass == cls,
                                onClick = { onClassFilterChange(cls) },
                                label = { Text(cls, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepTealContainer,
                                    selectedLabelColor = OnDeepTealContainer
                                )
                            )
                        }
                    }

                    // Status Chips
                    Text(
                        text = "Filter by Payment Status:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        statusOptions.forEach { status ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = { onStatusFilterChange(status) },
                                label = { Text(status, fontSize = 12.sp) },
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

        // Table & Card Hybrid Layout
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 840.dp
                if (isWide) {
                    // Wide Tablet Table View
                    TableView(
                        students = students,
                        onStudentClick = onStudentClick,
                        onRecordPaymentClick = onRecordPaymentClick
                    )
                } else {
                    // Compact / Phone Card View
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        students.forEach { student ->
                            StudentCardItem(
                                student = student,
                                onStudentClick = { onStudentClick(student) },
                                onRecordPaymentClick = { onRecordPaymentClick(student) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { name, admNo, grade, guardian, phone, email, fee ->
                onAddStudent(name, admNo, grade, guardian, phone, email, fee)
                showAddStudentDialog = false
            }
        )
    }
}

@Composable
private fun TableView(
    students: List<Student>,
    onStudentClick: (Student) -> Unit,
    onRecordPaymentClick: (Student) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Table Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCardSubtle)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("STUDENT & ID", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(2f))
                Text("CLASS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Text("TOTAL FEE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.2f))
                Text("PAID", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.2f))
                Text("BALANCE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.2f))
                Text("STATUS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.1f))
                Text("ACTIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.6f))
            }

            HorizontalDivider(color = BorderSlate)

            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No students found matching your search.", color = TextMuted, fontSize = 14.sp)
                }
            } else {
                students.forEachIndexed { index, student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStudentClick(student) }
                            .background(if (index % 2 == 1) SurfaceCardSubtle.copy(alpha = 0.35f) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("student_row_${student.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Student Name & ID
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(2f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(student.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.fullName.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = student.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = student.admissionNo,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Class
                        Text(
                            text = student.gradeClass,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )

                        // Total Fee
                        Text(
                            text = "$${String.format("%,.2f", student.totalFee)}",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Paid
                        Text(
                            text = "$${String.format("%,.2f", student.amountPaid)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreen,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Balance
                        Text(
                            text = "$${String.format("%,.2f", student.balance)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (student.balance > 0) WarningRed else TextMuted,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Status Badge
                        Box(modifier = Modifier.weight(1.1f)) {
                            StatusBadge(status = student.status)
                        }

                        // Actions
                        Row(
                            modifier = Modifier.weight(1.6f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onStudentClick(student) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Profile", fontSize = 11.sp)
                            }

                            if (student.balance > 0) {
                                Button(
                                    onClick = { onRecordPaymentClick(student) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Pay", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = BorderSlate.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun StudentCardItem(
    student: Student,
    onStudentClick: () -> Unit,
    onRecordPaymentClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStudentClick() }
            .testTag("student_card_${student.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(student.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.fullName.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = student.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${student.admissionNo} • ${student.gradeClass}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                StatusBadge(status = student.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderSlate)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Fee", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format("%,.2f", student.totalFee)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Column {
                    Text("Paid", fontSize = 11.sp, color = TextMuted)
                    Text("$${String.format("%,.2f", student.amountPaid)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance", fontSize = 11.sp, color = TextMuted)
                    Text(
                        "$${String.format("%,.2f", student.balance)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (student.balance > 0) WarningRed else SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onStudentClick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Profile", fontSize = 12.sp)
                }

                if (student.balance > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onRecordPaymentClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Record Pay", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStudentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, admNo: String, grade: String, guardian: String, phone: String, email: String, fee: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var admNo by remember { mutableStateOf("ADM-2026-" + (100 + (1..899).random())) }
    var grade by remember { mutableStateOf("Grade 10 Sci") }
    var guardian by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var feeStr by remember { mutableStateOf("1460.00") }

    val classes = listOf("Grade 7A", "Grade 7B", "Grade 8A", "Grade 8B", "Grade 9A", "Grade 10 Sci", "Grade 11 Arts", "Grade 12 Sci")
    var classExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text("Register New Student", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text("Add a new student to the fee register", fontSize = 12.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = admNo,
                        onValueChange = { admNo = it },
                        label = { Text("Admission No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    ExposedDropdownMenuBox(
                        expanded = classExpanded,
                        onExpandedChange = { classExpanded = !classExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = grade,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Class") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = classExpanded,
                            onDismissRequest = { classExpanded = false }
                        ) {
                            classes.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        grade = c
                                        classExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = guardian,
                    onValueChange = { guardian = it },
                    label = { Text("Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = feeStr,
                        onValueChange = { feeStr = it },
                        label = { Text("Fee ($)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val feeVal = feeStr.toDoubleOrNull() ?: 1320.0
                            if (name.isNotBlank()) {
                                onConfirm(name, admNo, grade, guardian, phone, email, feeVal)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                    ) {
                        Text("Save Student")
                    }
                }
            }
        }
    }
}
