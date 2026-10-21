package com.example

import com.example.data.FeeTrackRepository
import com.example.model.FeeStatus
import com.example.model.Student
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: FeeTrackRepository

    @Before
    fun setUp() {
        repository = FeeTrackRepository()
    }

    @Test
    fun studentStatus_isCalculatedCorrectly() {
        val studentOwing = Student("1", "ADM-001", "John Doe", "Grade 7A", "Jane", "123", "j@d.com", 0L, 1000.0, 0.0)
        assertEquals(FeeStatus.OWING, studentOwing.status)
        assertEquals(1000.0, studentOwing.balance, 0.01)

        val studentPartial = Student("2", "ADM-002", "Alice Smith", "Grade 8A", "Bob", "456", "b@s.com", 0L, 1000.0, 500.0)
        assertEquals(FeeStatus.PARTIAL, studentPartial.status)
        assertEquals(500.0, studentPartial.balance, 0.01)

        val studentPaid = Student("3", "ADM-003", "Charlie Brown", "Grade 9A", "Lucy", "789", "l@b.com", 0L, 1000.0, 1000.0)
        assertEquals(FeeStatus.PAID, studentPaid.status)
        assertEquals(0.0, studentPaid.balance, 0.01)
    }

    @Test
    fun recordPayment_updatesStudentBalanceAndGeneratesReceipt() {
        val studentsBefore = repository.students.value
        val testStudent = studentsBefore.first { it.balance > 0 }
        val initialPaid = testStudent.amountPaid
        val payAmount = 350.0

        val receipt = repository.recordPayment(
            studentId = testStudent.id,
            amount = payAmount,
            term = "Term 1",
            category = "Tuition",
            paymentMethod = "Bank Transfer",
            referenceNo = "TXN-TEST-123",
            notes = "Test payment"
        )

        assertNotNull(receipt)
        assertTrue(receipt!!.receiptNo.startsWith("REC-"))
        assertEquals(payAmount, receipt.amount, 0.01)

        val studentAfter = repository.students.value.first { it.id == testStudent.id }
        assertEquals(initialPaid + payAmount, studentAfter.amountPaid, 0.01)
    }

    @Test
    fun dashboardStats_areAccuratelyComputed() {
        val stats = repository.getDashboardStats()
        assertTrue(stats.totalExpected > 0)
        assertTrue(stats.totalCollected > 0)
        assertEquals(stats.totalExpected - stats.totalCollected, stats.outstandingBalance, 0.01)
        assertTrue(stats.collectionRate in 0.0f..1.0f)
    }

    @Test
    fun csvExport_containsHeaderAndTransactions() {
        val csv = repository.generateCsvExport()
        assertTrue(csv.startsWith("Receipt No,Date,Student Name"))
        assertTrue(csv.contains("REC-"))
    }
}
