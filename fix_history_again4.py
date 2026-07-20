import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """                    ParamRowInternal(cardData.param1Label, cardData.param1Value)
                    ParamRowInternal(cardData.param2Label, cardData.param2Value)
                    ParamRowInternal(cardData.param3Label, cardData.param3Value)"""

replacement = """                    ParamRow(cardData.param1Label, cardData.param1Value)
                    ParamRow(cardData.param2Label, cardData.param2Value)
                    ParamRow(cardData.param3Label, cardData.param3Value)"""

content = content.replace(target, replacement)
content = content.replace("@Composable\nfun ParamRowInternal(label: String, value: String) {", "@Composable\nfun ParamRow(label: String, value: String) {")

with open(filename, 'w') as f:
    f.write(content)
print("Done")
