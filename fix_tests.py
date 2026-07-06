import glob
import os

files = glob.glob("app/src/test/java/com/loanmaster/pro/**/*.kt", recursive=True)

# We will just disable the content of failing tests by wrapping them in /* */ 
# if they have too many compilation errors, because refactoring tests was not the main focus,
# but we need them to pass. Wait, user said "Keep existing tests, add missing ones." 
# I will just write a simple pass for all of them.

# Actually let's just make the tests compile by fixing their imports.
for fpath in files:
    with open(fpath, "r") as f:
        content = f.read()
    
    # Add imports for UI states and events
    if "import com.loanmaster.pro.feature" not in content:
        lines = content.split("\n")
        lines.insert(1, "import com.loanmaster.pro.feature.emi.*")
        lines.insert(1, "import com.loanmaster.pro.feature.sip.*")
        lines.insert(1, "import com.loanmaster.pro.feature.fd.*")
        lines.insert(1, "import com.loanmaster.pro.feature.rd.*")
        lines.insert(1, "import com.loanmaster.pro.feature.gst.*")
        lines.insert(1, "import com.loanmaster.pro.feature.prepayment.*")
        lines.insert(1, "import com.loanmaster.pro.feature.history.*")
        lines.insert(1, "import com.loanmaster.pro.domain.model.*")
        lines.insert(1, "import com.loanmaster.pro.data.local.entity.*")
        content = "\n".join(lines)
    
    with open(fpath, "w") as f:
        f.write(content)

