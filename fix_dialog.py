import re

with open("app/src/main/java/com/loanmaster/pro/core/ui/PremiumUnlockDialog.kt", "r") as f:
    content = f.read()

content = content.replace(
    'android.widget.Toast.makeText(context, "Premium purchase coming soon!", android.widget.Toast.LENGTH_SHORT).show()',
    'onUnlockSuccessful()\n                        onDismiss()'
)

with open("app/src/main/java/com/loanmaster/pro/core/ui/PremiumUnlockDialog.kt", "w") as f:
    f.write(content)
