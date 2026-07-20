import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

# Remove the trailing "    }\n}" we added
if content.endswith("    }\n}"):
    content = content[:-6]

# Insert } before @Composable\nprivate fun SipTopBar
target = "\n@Composable\nprivate fun SipTopBar"
replacement = "\n        }\n    }\n}\n@Composable\nprivate fun SipTopBar"

# Wait, how many braces does SipScreen need to close Scaffold?
# Original SipScreen ended with `    }\n}` for Scaffold and SipScreen.
# Then I added `    }\n}` at the end of the file. So the original `    }\n}` is still there!
# If the original `    }\n}` is still there before SipTopBar, I just need to add ONE `}` to close the ResponsiveScreenWrapper.
# Let's check what is before SipTopBar.
