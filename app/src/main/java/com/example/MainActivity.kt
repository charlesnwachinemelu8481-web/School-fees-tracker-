package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.model.ScreenNav
import com.example.ui.components.AppResponsiveShell
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeeStructureScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PaymentHistoryScreen
import com.example.ui.screens.RecordPaymentScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.StudentProfileScreen
import com.example.ui.screens.StudentsListScreen
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.FeeTrackTheme
import com.example.viewmodel.FeeTrackViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FeeTrackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FeeTrackTheme {
                val uiState by viewModel.uiState.collectAsState()
                val students by viewModel.filteredStudents.collectAsState()
                val allStudents by viewModel.students.collectAsState()
                val payments by viewModel.filteredPayments.collectAsState()
                val allPayments by viewModel.payments.collectAsState()
                val feeStructures by viewModel.feeStructures.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState.snackbarMessage) {
                    uiState.snackbarMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.dismissSnackbar()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(BackgroundSlate)
                    ) {
                        if (!uiState.isLoggedIn) {
                            LoginScreen(
                                onLoginSuccess = { email, name, role ->
                                    viewModel.login(email, name, role)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // BackHandler for secondary screens
                            BackHandler(enabled = uiState.currentScreen != ScreenNav.DASHBOARD) {
                                when (uiState.currentScreen) {
                                    ScreenNav.STUDENT_PROFILE -> viewModel.navigateTo(ScreenNav.STUDENTS)
                                    ScreenNav.RECORD_PAYMENT -> viewModel.navigateTo(ScreenNav.DASHBOARD)
                                    ScreenNav.FEE_STRUCTURE -> viewModel.navigateTo(ScreenNav.DASHBOARD)
                                    ScreenNav.PAYMENT_HISTORY -> viewModel.navigateTo(ScreenNav.DASHBOARD)
                                    ScreenNav.REPORTS -> viewModel.navigateTo(ScreenNav.DASHBOARD)
                                    ScreenNav.STUDENTS -> viewModel.navigateTo(ScreenNav.DASHBOARD)
                                    else -> {}
                                }
                            }

                            AppResponsiveShell(
                                currentScreen = uiState.currentScreen,
                                currentUser = uiState.currentUser,
                                onNavigate = { screen -> viewModel.navigateTo(screen) },
                                onLogout = { viewModel.logout() }
                            ) {
                                when (uiState.currentScreen) {
                                    ScreenNav.LOGIN -> {
                                        LoginScreen(
                                            onLoginSuccess = { email, name, role ->
                                                viewModel.login(email, name, role)
                                            }
                                        )
                                    }

                                    ScreenNav.DASHBOARD -> {
                                        DashboardScreen(
                                            stats = viewModel.getDashboardStats(),
                                            recentPayments = allPayments,
                                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                                            onRecordPaymentClick = { viewModel.openRecordPayment(null) },
                                            onViewReceipt = { record -> viewModel.viewReceipt(record) }
                                        )
                                    }

                                    ScreenNav.STUDENTS -> {
                                        StudentsListScreen(
                                            students = students,
                                            searchQuery = uiState.studentSearchQuery,
                                            onSearchChange = { q -> viewModel.setStudentSearchQuery(q) },
                                            selectedClass = uiState.studentClassFilter,
                                            onClassFilterChange = { c -> viewModel.setStudentClassFilter(c) },
                                            selectedStatus = uiState.studentStatusFilter,
                                            onStatusFilterChange = { s -> viewModel.setStudentStatusFilter(s) },
                                            onStudentClick = { student -> viewModel.selectStudentProfile(student) },
                                            onRecordPaymentClick = { student -> viewModel.openRecordPayment(student) },
                                            onAddStudent = { name, admNo, grade, guardian, phone, email, fee ->
                                                viewModel.addStudent(name, admNo, grade, guardian, phone, email, fee)
                                            }
                                        )
                                    }

                                    ScreenNav.STUDENT_PROFILE -> {
                                        val activeStudent = uiState.selectedStudent
                                        val items = activeStudent?.let { feeStructures[it.gradeClass] } ?: emptyList()
                                        StudentProfileScreen(
                                            student = activeStudent,
                                            feeItems = items,
                                            payments = allPayments,
                                            onBack = { viewModel.navigateTo(ScreenNav.STUDENTS) },
                                            onRecordPayment = { stu -> viewModel.openRecordPayment(stu) },
                                            onViewReceipt = { record -> viewModel.viewReceipt(record) }
                                        )
                                    }

                                    ScreenNav.RECORD_PAYMENT -> {
                                        RecordPaymentScreen(
                                            students = allStudents,
                                            preselectedStudent = uiState.selectedStudentForPayment,
                                            nextReceiptNumber = viewModel.getNextReceiptPreview(),
                                            onSavePayment = { studentId, amount, term, category, method, ref, notes ->
                                                viewModel.submitPayment(studentId, amount, term, category, method, ref, notes)
                                            }
                                        )
                                    }

                                    ScreenNav.FEE_STRUCTURE -> {
                                        FeeStructureScreen(
                                            currentStructures = feeStructures,
                                            onSaveStructure = { gradeClass, items ->
                                                viewModel.updateFeeStructure(gradeClass, items)
                                            }
                                        )
                                    }

                                    ScreenNav.PAYMENT_HISTORY -> {
                                        PaymentHistoryScreen(
                                            payments = payments,
                                            searchQuery = uiState.paymentSearchQuery,
                                            onSearchChange = { q -> viewModel.setPaymentSearchQuery(q) },
                                            selectedMethod = uiState.paymentMethodFilter,
                                            onMethodChange = { m -> viewModel.setPaymentMethodFilter(m) },
                                            selectedDateRange = uiState.paymentDateFilter,
                                            onDateRangeChange = { r -> viewModel.setPaymentDateFilter(r) },
                                            onViewReceipt = { record -> viewModel.viewReceipt(record) },
                                            onExportCsv = { viewModel.getCsvExport() }
                                        )
                                    }

                                    ScreenNav.REPORTS -> {
                                        val defaulting = allStudents
                                            .filter { it.balance > 0 }
                                            .sortedByDescending { it.balance }

                                        ReportsScreen(
                                            classStats = viewModel.getClassCollectionStats(),
                                            methodStats = viewModel.getPaymentMethodStats(),
                                            defaultingStudents = defaulting,
                                            onSelectStudent = { stu -> viewModel.selectStudentProfile(stu) }
                                        )
                                    }
                                }
                            }
                        }

                        // Official Receipt Modal Dialog
                        if (uiState.showReceiptDialog && uiState.lastRecordedReceipt != null) {
                            ReceiptDialog(
                                record = uiState.lastRecordedReceipt!!,
                                onDismiss = { viewModel.dismissReceipt() }
                            )
                        }
                    }
                }
            }
        }
    }
}
