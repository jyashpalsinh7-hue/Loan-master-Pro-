with open('app/src/main/java/com/loanmaster/pro/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace("synchronized(Any()) {\n        val instance = androidx.room.Room.databaseBuilder(\n            context.applicationContext,\n            LoanMasterDatabase::class.java,\n            \"loan_master_database\"\n        ).fallbackToDestructiveMigration(dropAllTables = true).build()\n        APP_DATABASE_INSTANCE = instance\n        instance\n    }", "synchronized(LoanMasterDatabase::class.java) {\n        APP_DATABASE_INSTANCE ?: androidx.room.Room.databaseBuilder(\n            context.applicationContext,\n            LoanMasterDatabase::class.java,\n            \"loan_master_database\"\n        ).fallbackToDestructiveMigration().build().also { APP_DATABASE_INSTANCE = it }\n    }")

with open('app/src/main/java/com/loanmaster/pro/MainActivity.kt', 'w') as f:
    f.write(content)
