import glob

files = glob.glob("app/src/main/java/com/loanmaster/pro/feature/**/*UiState.kt", recursive=True)
for file in files:
    with open(file, "r") as f:
        content = f.read()
    
    if "import com.loanmaster.pro.domain.model.*" not in content:
        lines = content.split("\n")
        lines.insert(1, "import com.loanmaster.pro.domain.model.*")
        lines.insert(1, "import com.loanmaster.pro.data.local.entity.*")
        content = "\n".join(lines)
    
    with open(file, "w") as f:
        f.write(content)

