import os
import re

directory = "app/src/main/java/com/loanmaster/pro"

replacements = [
    (r"ResponsiveUtils\.horizontalPadding\([^)]+\)", "LoanMasterTheme.spacing.screenPadding"),
    (r"ResponsiveUtils\.verticalPadding\([^)]+\)", "LoanMasterTheme.spacing.lg"),
    (r"ResponsiveUtils\.iconSize\([^)]+\)", "LoanMasterTheme.components.iconMedium"),
    (r"ResponsiveUtils\.titleFontSize\([^)]+\)", "LoanMasterTheme.typography.title.fontSize"),
    (r"ResponsiveUtils\.subtitleFontSize\([^)]+\)", "LoanMasterTheme.typography.title.fontSize"),
    (r"ResponsiveUtils\.bodyFontSize\([^)]+\)", "LoanMasterTheme.typography.body.fontSize"),
    (r"ResponsiveUtils\.labelFontSize\([^)]+\)", "LoanMasterTheme.typography.label.fontSize"),
    (r"ResponsiveUtils\.heroValueFontSize\([^)]+\)", "LoanMasterTheme.typography.display.fontSize"),
    (r"ResponsiveUtils\.cardSpacing\([^)]+\)", "LoanMasterTheme.spacing.screenPadding"),
]

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                text = f.read()
            
            new_text = text
            for pattern, repl in replacements:
                new_text = re.sub(pattern, repl, new_text)
            
            if new_text != text:
                with open(filepath, "w") as f:
                    f.write(new_text)
                print(f"Updated {filepath}")
