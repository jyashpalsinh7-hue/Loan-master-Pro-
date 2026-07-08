with open('app/src/main/java/com/loanmaster/pro/core/theme/Theme.kt', 'r') as f:
    content = f.read()

content = content.replace("val window = (view.context as Activity).window", """
            val context = view.context
            val activity = generateSequence(context) { if (it is android.content.ContextWrapper) it.baseContext else null }.firstOrNull { it is Activity } as? Activity
            val window = activity?.window
            if (window != null) {
""")
content = content.replace("WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false", """WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }""")

with open('app/src/main/java/com/loanmaster/pro/core/theme/Theme.kt', 'w') as f:
    f.write(content)
