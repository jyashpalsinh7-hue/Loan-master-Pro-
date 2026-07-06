import re

path1 = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(path1, "r") as f:
    content1 = f.read()

content1 = content1.replace("viewModel::updateSearchQuery", "viewModel.updateSearchQuery")
content1 = content1.replace("viewModel::toggleQuickToolsExpanded", "viewModel.toggleQuickToolsExpanded")

with open(path1, "w") as f:
    f.write(content1)

