import re

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    text = f.read()

# Replace any lingering `com.loanmaster.pro.WindowWidthSizeClass.COMPACT`
text = text.replace("com.loanmaster.pro.WindowWidthSizeClass.COMPACT", "androidx.window.core.layout.WindowWidthSizeClass.COMPACT")
text = text.replace("com.loanmaster.pro.WindowWidthSizeClass.MEDIUM", "androidx.window.core.layout.WindowWidthSizeClass.MEDIUM")
text = text.replace("com.loanmaster.pro.WindowWidthSizeClass.EXPANDED", "androidx.window.core.layout.WindowWidthSizeClass.EXPANDED")

# Also add import if missing
if "import androidx.window.core.layout.WindowWidthSizeClass" not in text:
    text = text.replace("import android.os.Bundle", "import androidx.window.core.layout.WindowWidthSizeClass\nimport android.os.Bundle")

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(text)
