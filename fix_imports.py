with open('app/src/main/java/com/loanmaster/pro/SharedUI.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.foundation.layout.fillMaxSize" not in content:
    content = content.replace("import androidx.compose.foundation.layout.fillMaxWidth", "import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.fillMaxSize")

with open('app/src/main/java/com/loanmaster/pro/SharedUI.kt', 'w') as f:
    f.write(content)
