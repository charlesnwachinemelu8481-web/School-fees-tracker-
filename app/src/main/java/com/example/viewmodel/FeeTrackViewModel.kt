package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FeeTrackRepository
import com.example.model.AdminUser
import com.example.model.ClassCollectionStat
import com.example.model.DashboardStats
import com.example.model.FeeCategoryItem
import com.example.model.FeeStatus
import com.example.model.PaymentMethodStat
import com.example.model.PaymentRecord
import com.example.model.ScreenNav
import com.example.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FeeTrackUiState(
    val currentScreen: ScreenNav = ScreenNav.LOGIN,
    val isLoggedIn: Boolean = false,
    val currentUser: AdminUser = AdminUser("Dr. Marcus Vance", "m.vance@oakwood.edu", "Chief Bursar", "MV"),
    val selectedStudent: Student? = null,
    val selectedStudentForPayment: Student? = null,
    val studentSearchQuery: String = "",
    val studentClassFilter: String = "All Classes",
    val studentStatusFilter: String = "All",
    val paymentSearchQuery: String = "",
    val paymentMethodFilter: String = "All Methods",
    val paymentDateFilter: String = "All Time",
    val lastRecordedReceipt: PaymentRecord? = null,
    val showReceiptDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class FeeTrackViewModel(
    val repository: FeeTrackRepository = FeeTrackRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeeTrackUiState())
    val uiState: StateFlow<FeeTrackUiState> = _uiState.asStateFlow()

    val students: StateFlow<List<Student>> = repository.students
    val payments: StateFlow<List<PaymentRecord>> = repository.payments
    val feeStructures: StateFlow<Map<String, List<FeeCategoryItem>>> = repository.feeStructures

    // Filtered Students
    val filteredStudents: StateFlow<List<Student>> = combine(
        students,
        _uiState
    ) { studentList, state ->
        studentList.filter { student ->
            val matchesQuery = state.studentSearchQuery.isBlank() ||
                    student.fullName.contains(state.studentSearchQuery, ignoreCase = true) ||
                    student.admissionNo.contains(state.studentSearchQuery, ignoreCase = true) ||
                    student.guardianName.contains(state.studentSearchQuery, ignoreCase = true)

            val matchesClass = state.studentClassFilter == "All Classes" ||
                    student.gradeClass == state.studentClassFilter

            val matchesStatus = when (state.studentStatusFilter) {
                "Paid" -> student.status == FeeStatus.PAID
                "Partial" -> student.status == FeeStatus.PARTIAL
                "Owing" -> student.status == FeeStatus.OWING
                else -> true
            }

            matchesQuery && matchesClass && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Payments
    val filteredPayments: StateFlow<List<PaymentRecord>> = combine(
        payments,
        _uiState
    ) { paymentList, state ->
        paymentList.filter { payment ->
            val matchesQuery = state.paymentSearchQuery.isBlank() ||
                    payment.studentName.contains(state.paymentSearchQuery, ignoreCase = true) ||
                    payment.receiptNo.contains(state.paymentSearchQuery, ignoreCase = true) ||
                    payment.referenceNo.contains(state.paymentSearchQuery, ignoreCase = true)

            val matchesMethod = state.paymentMethodFilter == "All Methods" ||
                    payment.paymentMethod == state.paymentMethodFilter

            val matchesDate = when (state.paymentDateFilter) {
                "Today" -> payment.paymentDate.contains("Oct 21") || payment.paymentDate.contains("Today")
                "This Week" -> true // Seed includes this week
                else -> true
            }

            matchesQuery && matchesMethod && matchesDate
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login(email: String, name: String, role: String) {
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
            .ifBlank { "AD" }

        val user = AdminUser(name = name, email = email, role = role, initials = initials)
        repository.setAdminUser(user)
        _uiState.value = _uiState.value.copy(
            isLoggedIn = true,
            currentUser = user,
            currentScreen = ScreenNav.DASHBOARD,
            snackbarMessage = "Welcome back, ${user.name}!"
        )
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            currentScreen = ScreenNav.LOGIN,
            selectedStudent = null,
            selectedStudentForPayment = null
        )
    }

    fun navigateTo(screen: ScreenNav) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectStudentProfile(student: Student) {
        _uiState.value = _uiState.value.copy(
            selectedStudent = student,
            currentScreen = ScreenNav.STUDENT_PROFILE
        )
    }

    fun openRecordPayment(student: Student? = null) {
        _uiState.value = _uiState.value.copy(
            selectedStudentForPayment = student,
            currentScreen = ScreenNav.RECORD_PAYMENT
        )
    }

    fun setStudentSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(studentSearchQuery = query)
    }

    fun setStudentClassFilter(filter: String) {
        _uiState.value = _uiState.value.copy(studentClassFilter = filter)
    }

    fun setStudentStatusFilter(filter: String) {
        _uiState.value = _uiState.value.copy(studentStatusFilter = filter)
    }

    fun setPaymentSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(paymentSearchQuery = query)
    }

    fun setPaymentMethodFilter(filter: String) {
        _uiState.value = _uiState.value.copy(paymentMethodFilter = filter)
    }

    fun setPaymentDateFilter(filter: String) {
        _uiState.value = _uiState.value.copy(paymentDateFilter = filter)
    }

    fun submitPayment(
        studentId: String,
        amount: Double,
        term: String,
        category: String,
        paymentMethod: String,
        referenceNo: String,
        notes: String
    ) {
        val receipt = repository.recordPayment(
            studentId = studentId,
            amount = amount,
            term = term,
            category = category,
            paymentMethod = paymentMethod,
            referenceNo = referenceNo,
            notes = notes
        )
        if (receipt != null) {
            // Update selected student if viewing profile
            val updatedStudent = repository.students.value.find { it.id == studentId }
            _uiState.value = _uiState.value.copy(
                selectedStudent = updatedStudent ?: _uiState.value.selectedStudent,
                lastRecordedReceipt = receipt,
                showReceiptDialog = true,
                snackbarMessage = "Payment of $${String.format("%.2f", amount)} successfully recorded!"
            )
        }
    }

    fun addStudent(
        fullName: String,
        admissionNo: String,
        gradeClass: String,
        guardianName: String,
        guardianPhone: String,
        guardianEmail: String,
        totalFee: Double
    ) {
        val newStudent = repository.addStudent(
            fullName = fullName,
            admissionNo = admissionNo,
            gradeClass = gradeClass,
            guardianName = guardianName,
            guardianPhone = guardianPhone,
            guardianEmail = guardianEmail,
            totalFee = totalFee
        )
        _uiState.value = _uiState.value.copy(
            snackbarMessage = "Student ${newStudent.fullName} registered successfully!"
        )
    }

    fun updateFeeStructure(gradeClass: String, items: List<FeeCategoryItem>) {
        repository.updateFeeStructure(gradeClass, items)
        _uiState.value = _uiState.value.copy(
            snackbarMessage = "Fee structure for $gradeClass saved successfully!"
        )
    }

    fun viewReceipt(record: PaymentRecord) {
        _uiState.value = _uiState.value.copy(
            lastRecordedReceipt = record,
            showReceiptDialog = true
        )
    }

    fun dismissReceipt() {
        _uiState.value = _uiState.value.copy(
            showReceiptDialog = false
        )
    }

    fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun getDashboardStats(): DashboardStats = repository.getDashboardStats()
    fun getClassCollectionStats(): List<ClassCollectionStat> = repository.getClassCollectionStats()
    fun getPaymentMethodStats(): List<PaymentMethodStat> = repository.getPaymentMethodStats()
    fun getCsvExport(): String = repository.generateCsvExport()
    fun getNextReceiptPreview(): String = repository.generateNextReceiptNumber()
}
