package com.example.budgettracker

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: BudgetTrackerDBHelper
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = BudgetTrackerDBHelper(this)
        database = dbHelper.writableDatabase

        insertUser("john_doe", "securepass123")
        readUsers()

        val userId = 1
        val foodCategoryId = insertCategory("Food", userId)
        val transportCategoryId = insertCategory("Transport", userId)

        insertExpense(userId, foodCategoryId, "2024-01-10", "09:00", "10:00", "Breakfast", 15.0)
        insertExpense(userId, transportCategoryId, "2024-01-15", "08:00", "09:00", "Taxi ride", 20.0)
        insertExpense(userId, foodCategoryId, "2024-01-20", "12:00", "13:00", "Lunch", 10.0)

        getTotalSpentByCategory(userId, "2024-01-01", "2024-01-31")
    }

    private fun insertUser(username: String, password: String) {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        val newRowId = database.insert("User", null, values)
        if (newRowId == -1L) {
            Log.e("DB_INSERT", "Failed to insert user")
        } else {
            Log.d("DB_INSERT", "User inserted with ID: $newRowId")
        }
    }

    private fun readUsers() {
        val cursor = database.rawQuery("SELECT * FROM User", null)
        while (cursor.moveToNext()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            Log.d("DB_QUERY", "User: $username | Password: $password")
        }
        cursor.close()
    }

    private fun insertCategory(name: String, userId: Int): Int {
        val values = ContentValues().apply {
            put("name", name)
            put("userId", userId)
        }
        return database.insert("Category", null, values).toInt()
    }

    private fun insertExpense(
        userId: Int,
        categoryId: Int,
        date: String,
        startTime: String,
        endTime: String,
        description: String,
        amount: Double
    ) {
        val values = ContentValues().apply {
            put("userId", userId)
            put("categoryId", categoryId)
            put("date", date)
            put("startTime", startTime)
            put("endTime", endTime)
            put("description", description)
            put("amount", amount)
        }
        database.insert("ExpenseEntry", null, values)
    }

    // ✅ Feature: Get total spent per category for a user between two dates
    private fun getTotalSpentByCategory(userId: Int, startDate: String, endDate: String) {
        val query = """
            SELECT Category.name, SUM(ExpenseEntry.amount) AS total_spent
            FROM ExpenseEntry
            JOIN Category ON ExpenseEntry.categoryId = Category.categoryId
            WHERE ExpenseEntry.userId = ? AND date BETWEEN ? AND ?
            GROUP BY ExpenseEntry.categoryId
        """
        val cursor = database.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))
        while (cursor.moveToNext()) {
            val category = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent"))
            Log.d("CATEGORY_TOTAL", "$category: $total")
        }
        cursor.close()
    }
}
