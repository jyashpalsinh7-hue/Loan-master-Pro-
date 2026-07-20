import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

content = content.replace('var selectedFilter by remember { mutableStateOf("All") }', 'var selectedFilter by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("All") }')
content = content.replace('var searchQuery by remember { mutableStateOf("") }', 'var searchQuery by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }')
content = content.replace('var isSearchActive by remember { mutableStateOf(false) }', 'var isSearchActive by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }')

with open(filename, 'w') as f:
    f.write(content)
print("Done")
