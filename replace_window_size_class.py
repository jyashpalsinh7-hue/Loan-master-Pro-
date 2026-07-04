import os
import re

directory = "app/src/main/java/com/loanmaster/pro"

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                text = f.read()
            
            new_text = text
            # Replace alias
            new_text = new_text.replace("import androidx.window.core.layout.WindowWidthSizeClass as WindowWidthSizeClassCore\n", "")
            new_text = new_text.replace("WindowWidthSizeClassCore.COMPACT", "WindowWidthSizeClass.COMPACT")
            new_text = new_text.replace("WindowWidthSizeClassCore.MEDIUM", "WindowWidthSizeClass.MEDIUM")
            new_text = new_text.replace("WindowWidthSizeClassCore.EXPANDED", "WindowWidthSizeClass.EXPANDED")
            new_text = new_text.replace("WindowWidthSizeClassCore", "WindowWidthSizeClass")
            
            # Replace custom import if any
            new_text = new_text.replace("import com.loanmaster.pro.WindowWidthSizeClass\n", "import androidx.window.core.layout.WindowWidthSizeClass\n")
            
            # Replace usages of Compact, Medium, Expanded
            new_text = new_text.replace("WindowWidthSizeClass.Compact", "WindowWidthSizeClass.COMPACT")
            new_text = new_text.replace("WindowWidthSizeClass.Medium", "WindowWidthSizeClass.MEDIUM")
            new_text = new_text.replace("WindowWidthSizeClass.Expanded", "WindowWidthSizeClass.EXPANDED")
            
            # In ResponsiveUtils.kt, remove the enum and rememberWindowWidthSizeClass
            if "ResponsiveUtils.kt" in filepath:
                new_text = re.sub(r"enum class WindowWidthSizeClass \{ Compact, Medium, Expanded \}\s*", "", new_text)
                new_text = re.sub(r"/\*\*.*?\*/\s*@Composable\s*fun rememberWindowWidthSizeClass\(\): WindowWidthSizeClass \{.*?\}\s*", "", new_text, flags=re.DOTALL)
                
            if new_text != text:
                # Add import if needed
                if "WindowWidthSizeClass." in new_text and "import androidx.window.core.layout.WindowWidthSizeClass" not in new_text and "package com.loanmaster.pro" in new_text:
                    # insert after package
                    new_text = re.sub(r"(package [^\n]+\n)", r"\1\nimport androidx.window.core.layout.WindowWidthSizeClass\n", new_text, count=1)
                with open(filepath, "w") as f:
                    f.write(new_text)
                print(f"Updated {filepath}")
