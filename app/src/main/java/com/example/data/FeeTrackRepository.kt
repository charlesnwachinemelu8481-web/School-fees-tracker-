package com.example.data

import com.example.model.AdminUser
import com.example.model.ClassCollectionStat
import com.example.model.DashboardStats
import com.example.model.FeeCategoryItem
import com.example.model.FeeStatus
import com.example.model.FeeStructure
import com.example.model.PaymentMethodStat
import com.example.model.PaymentRecord
import com.example.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FeeTrackRepository {

    private val _currentUser = MutableStateFlow(
        AdminUser(
            name = "Dr. Marcus Vance",
            email = "m.vance@oakwood.edu",
            role = "Chief Bursar",
            initials = "MV"
        )
    )
    val currentUser: StateFlow<AdminUser> = _currentUser.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _payments = MutableStateFlow<List<PaymentRecord>>(emptyList())
    val payments: StateFlow<List<PaymentRecord>> = _payments.asStateFlow()

    private val _feeStructures = MutableStateFlow<Map<String, List<FeeCategoryItem>>>(emptyMap())
    val feeStructures: StateFlow<Map<String, List<FeeCategoryItem>>> = _feeStructures.asStateFlow()

    private var receiptCounter = 1042

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val defaultClasses = listOf(
            "Grade 7A", "Grade 7B", "Grade 8A", "Grade 8B",
            "Grade 9A", "Grade 10 Sci", "Grade 11 Arts", "Grade 12 Sci"
        )

        // Seed fee structures
        val structuresMap = mutableMapOf<String, List<FeeCategoryItem>>()
        defaultClasses.forEach { grade ->
            val baseTuition = if (grade.startsWith("Grade 10") || grade.startsWith("Grade 11") || grade.startsWith("Grade 12")) 950.0 else 800.0
            val labFee = if (grade.contains("Sci")) 180.0 else 90.0
            structuresMap[grade] = listOf(
                FeeCategoryItem(UUID.randomUUID().toString(), "Tuition Fee", "Tuition", baseTuition, "Term 1"),
                FeeCategoryItem(UUID.randomUUID().toString(), "Textbooks & Library", "Books", 160.0, "Term 1"),
                FeeCategoryItem(UUID.randomUUID().toString(), "School Uniform & Kit", "Uniform", 120.0, "Term 1"),
                FeeCategoryItem(UUID.randomUUID().toString(), "STEM & Science Lab", "Lab & Tech", labFee, "Term 1"),
                FeeCategoryItem(UUID.randomUUID().toString(), "Athletics & Clubs", "Sports", 70.0, "Term 1"),
                FeeCategoryItem(UUID.randomUUID().toString(), "Campus Tech & ICT", "Development", 80.0, "Term 1")
            )
        }
        _feeStructures.value = structuresMap

        // Seed students
        val initialStudents = listOf(
            Student("s1", "ADM-2026-081", "Amara Okafor", "Grade 10 Sci", "Chinedu Okafor", "+1 (555) 234-8901", "c.okafor@gmail.com", 0xFF0F766E, 1460.0, 1460.0),
            Student("s2", "ADM-2026-082", "Lucas Montgomery", "Grade 12 Sci", "Sarah Montgomery", "+1 (555) 345-6712", "sarah.m@outlook.com", 0xFF2563EB, 1460.0, 1000.0),
            Student("s3", "ADM-2026-083", "Aisha Al-Mansoor", "Grade 11 Arts", "Tariq Al-Mansoor", "+1 (555) 891-2345", "tariq.mansoor@corp.net", 0xFF7C3AED, 1370.0, 0.0),
            Student("s4", "ADM-2026-084", "Ethan Chen", "Grade 7A", "David Chen", "+1 (555) 432-7890", "dchen.tech@gmail.com", 0xFF059669, 1320.0, 1320.0),
            Student("s5", "ADM-2026-085", "Sophia Rodriguez", "Grade 8A", "Elena Rodriguez", "+1 (555) 908-1234", "elena.rodz@yahoo.com", 0xFFDB2777, 1320.0, 650.0),
            Student("s6", "ADM-2026-086", "Liam O'Connor", "Grade 9A", "Patrick O'Connor", "+1 (555) 678-9012", "paddy.oconnor@ireland.org", 0xFFD97706, 1320.0, 0.0),
            Student("s7", "ADM-2026-087", "Zara Fatima", "Grade 10 Sci", "Dr. Bilal Fatima", "+1 (555) 789-0123", "bfatima.md@hospital.org", 0xFF0891B2, 1460.0, 1460.0),
            Student("s8", "ADM-2026-088", "Kofi Mensah", "Grade 12 Sci", "Kwame Mensah", "+1 (555) 123-7654", "kwame.mensah@ghana.com", 0xFF4F46E5, 1460.0, 750.0),
            Student("s9", "ADM-2026-089", "Maya Patel", "Grade 7B", "Anand Patel", "+1 (555) 654-9871", "anand.patel@consulting.com", 0xFF10B981, 1320.0, 1320.0),
            Student("s10", "ADM-2026-090", "Noah Tanaka", "Grade 8B", "Kenji Tanaka", "+1 (555) 321-6549", "ktanaka@tokyo.jp", 0xFF9333EA, 1320.0, 400.0),
            Student("s11", "ADM-2026-091", "Chloe Dubois", "Grade 11 Arts", "Camille Dubois", "+1 (555) 987-1234", "dubois.c@paris.fr", 0xFFEA580C, 1370.0, 0.0),
            Student("s12", "ADM-2026-092", "Jayden Brooks", "Grade 9A", "Marcus Brooks", "+1 (555) 543-2198", "mbrooks@buildco.com", 0xFF16A34A, 1320.0, 1320.0),
            Student("s13", "ADM-2026-093", "Fatima Al-Hassan", "Grade 7A", "Hassan Ibrahim", "+1 (555) 876-5432", "hassan.ibr@domain.com", 0xFF0D9488, 1320.0, 800.0),
            Student("s14", "ADM-2026-094", "Oliver Bennett", "Grade 10 Sci", "Claire Bennett", "+1 (555) 456-7890", "claire.bennett@legal.com", 0xFF4338CA, 1460.0, 0.0),
            Student("s15", "ADM-2026-095", "Isabella Santos", "Grade 8A", "Mateo Santos", "+1 (555) 765-4321", "msantos@logistics.com", 0xFFC026D3, 1320.0, 1320.0),
            Student("s16", "ADM-2026-096", "David Sterling", "Grade 12 Sci", "Evelyn Sterling", "+1 (555) 234-5678", "e.sterling@firm.org", 0xFF0284C7, 1460.0, 960.0)
        )
        _students.value = initialStudents

        // Seed payments
        val initialPayments = listOf(
            PaymentRecord("p1", "REC-2026-1035", "s1", "Amara Okafor", "Grade 10 Sci", 1460.0, "Term 1", "Full Clearance", "Bank Transfer", "Oct 19, 2026", "TXN-8849102", "Dr. Marcus Vance", "Early payment discount applied"),
            PaymentRecord("p2", "REC-2026-1036", "s4", "Ethan Chen", "Grade 7A", 1320.0, "Term 1", "Full Clearance", "Online Portal", "Oct 18, 2026", "TXN-7731920", "Mrs. Elena Rostova", "Paid via Parent App"),
            PaymentRecord("p3", "REC-2026-1037", "s2", "Lucas Montgomery", "Grade 12 Sci", 1000.0, "Term 1", "Tuition & Books", "POS / Card", "Oct 17, 2026", "POS-44910", "Dr. Marcus Vance", "1st Installment confirmed"),
            PaymentRecord("p4", "REC-2026-1038", "s7", "Zara Fatima", "Grade 10 Sci", 1460.0, "Term 1", "Full Clearance", "Cheque", "Oct 16, 2026", "CHQ-002914", "Dr. Marcus Vance", "Standard Chartered cleared"),
            PaymentRecord("p5", "REC-2026-1039", "s5", "Sophia Rodriguez", "Grade 8A", 650.0, "Term 1", "Tuition", "Cash", "Oct 15, 2026", "CSH-99201", "Mrs. Elena Rostova", "Cash receipt issued at Bursary counter"),
            PaymentRecord("p6", "REC-2026-1040", "s9", "Maya Patel", "Grade 7B", 1320.0, "Term 1", "Full Clearance", "Bank Transfer", "Oct 14, 2026", "TXN-1102948", "Dr. Marcus Vance", "Electronic wire from HSBC"),
            PaymentRecord("p7", "REC-2026-1041", "s8", "Kofi Mensah", "Grade 12 Sci", 750.0, "Term 1", "Tuition", "POS / Card", "Oct 12, 2026", "POS-88219", "Mrs. Elena Rostova", "Partial payment; balance by month end"),
            PaymentRecord("p8", "REC-2026-1042", "s12", "Jayden Brooks", "Grade 9A", 1320.0, "Term 1", "Full Clearance", "Online Portal", "Oct 10, 2026", "TXN-9028172", "Mrs. Elena Rostova", "Instant web pay via Stripe")
        )
        _payments.value = initialPayments
    }

    fun setAdminUser(user: AdminUser) {
        _currentUser.value = user
    }

    fun generateNextReceiptNumber(): String {
        receiptCounter++
        val year = SimpleDateFormat("yyyy", Locale.US).format(Date())
        return "REC-$year-$receiptCounter"
    }

    fun recordPayment(
        studentId: String,
        amount: Double,
        term: String,
        category: String,
        paymentMethod: String,
        referenceNo: String,
        notes: String
    ): PaymentRecord? {
        val student = _students.value.find { it.id == studentId } ?: return null
        val receiptNo = generateNextReceiptNumber()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val todayStr = dateFormat.format(Date())

        val newRecord = PaymentRecord(
            id = UUID.randomUUID().toString(),
            receiptNo = receiptNo,
            studentId = student.id,
            studentName = student.fullName,
            studentClass = student.gradeClass,
            amount = amount,
            term = term,
            category = category,
            paymentMethod = paymentMethod,
            paymentDate = todayStr,
            referenceNo = referenceNo.ifBlank { "REF-${System.currentTimeMillis() % 1000000}" },
            recordedBy = _currentUser.value.name,
            notes = notes
        )

        // Update student paid amount
        _students.value = _students.value.map {
            if (it.id == studentId) {
                it.copy(amountPaid = (it.amountPaid + amount))
            } else {
                it
            }
        }

        // Add payment to list (at front)
        _payments.value = listOf(newRecord) + _payments.value
        return newRecord
    }

    fun addStudent(
        fullName: String,
        admissionNo: String,
        gradeClass: String,
        guardianName: String,
        guardianPhone: String,
        guardianEmail: String,
        totalFee: Double
    ): Student {
        val newStudent = Student(
            id = UUID.randomUUID().toString(),
            admissionNo = admissionNo,
            fullName = fullName,
            gradeClass = gradeClass,
            guardianName = guardianName,
            guardianPhone = guardianPhone,
            guardianEmail = guardianEmail,
            avatarColorHex = 0xFF0F766E,
            totalFee = totalFee,
            amountPaid = 0.0
        )
        _students.value = listOf(newStudent) + _students.value
        return newStudent
    }

    fun updateFeeStructure(
        gradeClass: String,
        items: List<FeeCategoryItem>
    ) {
        val updatedMap = _feeStructures.value.toMutableMap()
        updatedMap[gradeClass] = items
        _feeStructures.value = updatedMap

        // Update existing students total fee for this class if desired
        val newTotal = items.sumOf { it.amount }
        _students.value = _students.value.map {
            if (it.gradeClass == gradeClass) {
                it.copy(totalFee = newTotal)
            } else {
                it
            }
        }
    }

    fun getDashboardStats(): DashboardStats {
        val studentList = _students.value
        val totalExpected = studentList.sumOf { it.totalFee }
        val totalCollected = studentList.sumOf { it.amountPaid }
        val outstanding = (totalExpected - totalCollected).coerceAtLeast(0.0)
        val defaultCount = studentList.count { it.status == FeeStatus.OWING || it.status == FeeStatus.PARTIAL }
        val rate = if (totalExpected > 0) (totalCollected / totalExpected).toFloat() else 0f

        return DashboardStats(
            totalExpected = totalExpected,
            totalCollected = totalCollected,
            outstandingBalance = outstanding,
            studentsInDefault = defaultCount,
            totalStudents = studentList.size,
            collectionRate = rate
        )
    }

    fun getClassCollectionStats(): List<ClassCollectionStat> {
        val studentList = _students.value
        val classes = studentList.map { it.gradeClass }.distinct().sorted()
        return classes.map { grade ->
            val inClass = studentList.filter { it.gradeClass == grade }
            val exp = inClass.sumOf { it.totalFee }
            val col = inClass.sumOf { it.amountPaid }
            ClassCollectionStat(
                gradeClass = grade,
                expected = exp,
                collected = col,
                outstanding = (exp - col).coerceAtLeast(0.0)
            )
        }
    }

    fun getPaymentMethodStats(): List<PaymentMethodStat> {
        val paymentList = _payments.value
        val totalAmount = paymentList.sumOf { it.amount }
        val grouped = paymentList.groupBy { it.paymentMethod }

        val methods = listOf("Bank Transfer", "Online Portal", "POS / Card", "Cash", "Cheque")
        return methods.map { method ->
            val list = grouped[method] ?: emptyList()
            val amount = list.sumOf { it.amount }
            val pct = if (totalAmount > 0) (amount / totalAmount).toFloat() else 0f
            PaymentMethodStat(
                method = method,
                amount = amount,
                count = list.size,
                percentage = pct
            )
        }.filter { it.count > 0 || it.amount > 0 }
    }

    fun generateCsvExport(): String {
        val sb = StringBuilder()
        sb.append("Receipt No,Date,Student Name,Admission No,Class,Amount,Payment Method,Term,Category,Reference,Recorded By\n")
        _payments.value.forEach { p ->
            val student = _students.value.find { it.id == p.studentId }
            val admNo = student?.admissionNo ?: "N/A"
            sb.append("\"${p.receiptNo}\",\"${p.paymentDate}\",\"${p.studentName}\",\"${admNo}\",\"${p.studentClass}\",\"${p.amount}\",\"${p.paymentMethod}\",\"${p.term}\",\"${p.category}\",\"${p.referenceNo}\",\"${p.recordedBy}\"\n")
        }
        return sb.toString()
    }
}
