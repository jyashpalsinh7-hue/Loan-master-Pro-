import os
import re

screens_dir = 'app/src/main/java/com/loanmaster/pro/feature'

def wrap_with_responsive(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if 'ResponsiveScreenWrapper' in content and filepath.endswith('HistoryScreen.kt') == False:
        # Already wrapped or handled
        pass

    # For screens using Scaffold directly and not ResponsiveScreenWrapper
    if 'Scaffold(' in content and 'ResponsiveScreenWrapper' not in content:
        print(f"Needs wrapping: {filepath}")

for root, dirs, files in os.walk(screens_dir):
    for file in files:
        if file.endswith('Screen.kt'):
            wrap_with_responsive(os.path.join(root, file))
