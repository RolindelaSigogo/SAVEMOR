class BudgetTrackerDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "BudgetTracker.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create User table
        db.execSQL("""
            CREATE TABLE User (
                userId INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            )
        """)

        // Create Category table
        db.execSQL("""
            CREATE TABLE Category (
                categoryId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                userId INTEGER,
                FOREIGN KEY (userId) REFERENCES User(userId)
            )
        """)

        // Create ExpenseEntry table
        db.execSQL("""
            CREATE TABLE ExpenseEntry (
                entryId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                categoryId INTEGER NOT NULL,
                date TEXT NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                description TEXT,
                amount REAL NOT NULL,
                FOREIGN KEY (userId) REFERENCES User(userId),
                FOREIGN KEY (categoryId) REFERENCES Category(categoryId)
            )
        """)

        // Create ExpensePhoto table
        db.execSQL("""
            CREATE TABLE ExpensePhoto (
                photoId INTEGER PRIMARY KEY AUTOINCREMENT,
                entryId INTEGER NOT NULL,
                photoUri TEXT NOT NULL,
                FOREIGN KEY (entryId) REFERENCES ExpenseEntry(entryId)
            )
        """)

        // Create BudgetGoal table
        db.execSQL("""
            CREATE TABLE BudgetGoal (
                goalId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                categoryId INTEGER NOT NULL,
                month TEXT NOT NULL,
                minAmount REAL,
                maxAmount REAL,
                FOREIGN KEY (userId) REFERENCES User(userId),
                FOREIGN KEY (categoryId) REFERENCES Category(categoryId)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS BudgetGoal")
        db.execSQL("DROP TABLE IF EXISTS ExpensePhoto")
        db.execSQL("DROP TABLE IF EXISTS ExpenseEntry")
        db.execSQL("DROP TABLE IF EXISTS Category")
        db.execSQL("DROP TABLE IF EXISTS User")
        onCreate(db)
    }
}

