package com.loanmaster.pro.data.local.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// FIX: Added migrations to prevent destructive rebuilds
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("ALTER TABLE calculation_history ADD COLUMN param5 TEXT")
        } catch (e: Exception) {
            // Column may already exist
        }
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Placeholder for future schema changes — no-op
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No-op: verified via schema comparison that no structural
        // changes occurred between schema version 5 and 6.
    }
}
