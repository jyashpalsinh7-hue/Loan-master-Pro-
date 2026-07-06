import glob

files = glob.glob("app/src/main/java/com/loanmaster/pro/feature/**/*.kt", recursive=True)
for path in files:
    if "Screen.kt" in path:
        with open(path, "r") as f:
            content = f.read()
        
        if "import androidx.compose.runtime.getValue" not in content:
            content = content.replace("import androidx.compose.runtime.*\n", "import androidx.compose.runtime.*\nimport androidx.compose.runtime.getValue\n")
            if "import androidx.compose.runtime.getValue" not in content:
                content = content.replace("import androidx.compose.runtime.", "import androidx.compose.runtime.getValue\nimport androidx.compose.runtime.")
        
        if "import androidx.lifecycle.compose.collectAsStateWithLifecycle" not in content:
            content = content.replace("import androidx.compose.runtime.getValue", "import androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.compose.runtime.getValue")
            
        with open(path, "w") as f:
            f.write(content)

