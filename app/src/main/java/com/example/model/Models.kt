package com.example.model

enum class FeeStatus(val label: String) {
    PAID("Paid"),
    PARTIAL("Partial"),
    OWING("Owing")
}

data class Student(
    val id: String,
    val admissionNo: String,
    val fullName: String,
    val gradeClass: String,
    val guardianName: String,
    val guardianPhone: String,
    val guardianEmail: String,
    val avatarColorHex: Long = 0xFF0F766E,
    val totalFee: Double,
    val amountPaid: Double
) {
    val balance: Double
        get() = (totalFee - amountPaid).coerceAtLeast(0.0)

    val status: FeeStatus
        get() = when {
            amountPaid >= totalFee -> FeeStatus.PAID
            amountPaid > 0 -> FeeStatus.PARTIAL
            else -> FeeStatus.OWING
        }
}

data class FeeCategoryItem(
    val id: String,
    val name: String,
    val category: String, // Tuition, Books, Uniform, Lab & Tech, Sports, Development
    val amount: Double,
    val term: String // Term 1, Term 2, Term 3
)

data class FeeStructure(
    val gradeClass: String,
    val term: String,
    val items: List<FeeCategoryItem>
) {
    val totalAmount: Double
        get() = items.sumOf { it.amount }
}

data class PaymentRecord(
    val id: String,
    val receiptNo: String,
    val studentId: String,
    val studentName: String,
    val studentClass: String,
    val amount: Double,
    val term: String,
    val category: String,
    val paymentMethod: String, // Bank Transfer, Cash, POS / Card, Cheque, Online Portal
    val paymentDate: String,
    val referenceNo: String,
    val recordedBy: String,
    val notes: String = ""
)

data class AdminUser(
    val name: String,
    val email: String,
    val role: String,
    val initials: String
)

enum class ScreenNav(val title: String) {
    LOGIN("Sign In"),
    DASHBOARD("Dashboard"),
    STUDENTS("Students List"),
    STUDENT_PROFILE("Student Profile"),
    RECORD_PAYMENT("Record Payment"),
    FEE_STRUCTURE("Fee Structure"),
    PAYMENT_HISTORY("Payment History"),
    REPORTS("Financial Reports")
}

data class DashboardStats(
    val totalExpected: Double,
    val totalCollected: Double,
    val outstandingBalance: Double,
    val studentsInDefault: Int,
    val totalStudents: Int,
    val collectionRate: Float // 0f .. 1f
)

data class ClassCollectionStat(
    val gradeClass: String,
    val expected: Double,
    val collected: Double,
    val outstanding: Double
)

data class PaymentMethodStat(
    val method: String,
    val amount: Double,
    val count: Int,
    val percentage: Float
)
