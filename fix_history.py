import re

with open("app/src/main/java/com/loanmaster/pro/HistoryViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("repository.allHistory", "repository.getAllHistory()")
content = content.replace("repository.insert(", "repository.saveHistory(")
content = content.replace("repository.clearAll()", "repository.deleteAllHistory()")

with open("app/src/main/java/com/loanmaster/pro/HistoryViewModel.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/loanmaster/pro/HistoryRepository.kt", "r") as f:
    content = f.read()

content = re.sub(r'\s*val allHistory: Flow<List<CalculationHistory>> = getAllHistory\(\)', '', content)
content = re.sub(r'\s*suspend fun insert\(history: CalculationHistory\): Long \{\n\s*return saveHistory\(history\)\n\s*\}', '', content)
content = re.sub(r'\s*suspend fun clearAll\(\) \{\n\s*deleteAllHistory\(\)\n\s*\}', '', content)

with open("app/src/main/java/com/loanmaster/pro/HistoryRepository.kt", "w") as f:
    f.write(content)

