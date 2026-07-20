import sys

path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

target = ").fallbackToDestructiveMigration().addMigrations(com.loanmaster.pro.data.local.room.MIGRATION_3_4, com.loanmaster.pro.data.local.room.MIGRATION_4_5).build().also { APP_DATABASE_INSTANCE = it }"
replacement = ").addMigrations(com.loanmaster.pro.data.local.room.MIGRATION_3_4, com.loanmaster.pro.data.local.room.MIGRATION_4_5, com.loanmaster.pro.data.local.room.MIGRATION_5_6).build().also { APP_DATABASE_INSTANCE = it }"

if target in content:
    content = content.replace(target, replacement)
    with open(path, "w") as f:
        f.write(content)
    print("Replaced target")
else:
    print("Target not found")
