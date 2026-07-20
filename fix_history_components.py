import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ParamRow(cardData.param1Label, cardData.param1Value)
                    ParamRow(cardData.param2Label, cardData.param2Value)
                    ParamRow(cardData.param3Label, cardData.param3Value)
                }"""

replacement = """                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    com.loanmaster.pro.feature.history.ParamRow(cardData.param1Label, cardData.param1Value)
                    com.loanmaster.pro.feature.history.ParamRow(cardData.param2Label, cardData.param2Value)
                    com.loanmaster.pro.feature.history.ParamRow(cardData.param3Label, cardData.param3Value)
                }"""

content = content.replace(target, replacement)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
