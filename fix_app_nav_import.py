path = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(path, "r") as f:
    content = f.read()

if "import androidx.compose.runtime.getValue" not in content:
    content = content.replace("import androidx.compose.runtime.*\n", "import androidx.compose.runtime.*\nimport androidx.compose.runtime.getValue\n")
    if "import androidx.compose.runtime.getValue" not in content:
        content = content.replace("import androidx.compose.runtime.", "import androidx.compose.runtime.getValue\nimport androidx.compose.runtime.")

with open(path, "w") as f:
    f.write(content)
