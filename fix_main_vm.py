import re

path = "app/src/main/java/com/loanmaster/pro/feature/home/MainViewModel.kt"
with open(path, "r") as f:
    content = f.read()

# Just replace from "fun clearSelectedHistory()" to end with ""
content = re.sub(r'    fun clearSelectedHistory\(\) \{[\s\S]*', '', content)
content += "}\n"

with open(path, "w") as f:
    f.write(content)

