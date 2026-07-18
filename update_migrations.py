import sys

path = "app/src/main/java/com/loanmaster/pro/data/local/room/DatabaseMigrations.kt"
with open(path, "r") as f:
    content = f.read()

new_migration = """val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No-op: verified via schema comparison that no structural
        // changes occurred between schema version 5 and 6.
    }
}
"""

if "val MIGRATION_5_6" not in content:
    content = content + "\n" + new_migration
    with open(path, "w") as f:
        f.write(content)
    print("Added MIGRATION_5_6")
else:
    print("MIGRATION_5_6 already exists")
