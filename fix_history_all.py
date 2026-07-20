import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

# Fix unresolved imports
target1 = """import com.loanmaster.pro.feature.history.components.EmptyHistoryIllustration
import com.loanmaster.pro.feature.history.components.HistoryItemCard"""
replacement1 = ""

content = content.replace(target1, replacement1)

# Fix ParamRow calls
target2 = """                    com.loanmaster.pro.feature.history.ParamRow(cardData.param1Label, cardData.param1Value)
                    com.loanmaster.pro.feature.history.ParamRow(cardData.param2Label, cardData.param2Value)
                    com.loanmaster.pro.feature.history.ParamRow(cardData.param3Label, cardData.param3Value)"""
replacement2 = """                    ParamRow(cardData.param1Label, cardData.param1Value)
                    ParamRow(cardData.param2Label, cardData.param2Value)
                    ParamRow(cardData.param3Label, cardData.param3Value)"""

content = content.replace(target2, replacement2)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
