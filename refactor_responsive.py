import os
import re

color_replacements = {
    "ResponsiveUtils.BgColor": "BackgroundDark",
    "ResponsiveUtils.SurfaceColor": "SurfaceDark",
    "ResponsiveUtils.PrimaryAccent": "AccentYellow",
    "ResponsiveUtils.SecondaryAccent": "AccentBlue",
    "ResponsiveUtils.CardStroke": "CardStroke",
    "ResponsiveUtils.TextPrimary": "TextPrimary",
    "ResponsiveUtils.TextSecondary": "TextSecondary",
    "ResponsiveUtils.CardShape": "RoundedCornerShape(LoanMasterTheme.components.cardRadius)",
    "ResponsiveUtils.ButtonShape": "RoundedCornerShape(LoanMasterTheme.components.buttonHeight / 2)",
    "ResponsiveUtils.optimalContentWidth()": "optimalContentWidth()"
}

directory = "app/src/main/java/com/loanmaster/pro"

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                text = f.read()
            
            new_text = text
            for old, new in color_replacements.items():
                new_text = new_text.replace(old, new)
            
            if "import com.loanmaster.pro.ResponsiveUtils" in new_text:
                new_text = new_text.replace("import com.loanmaster.pro.ResponsiveUtils\n", "")
                
            if new_text != text:
                with open(filepath, "w") as f:
                    f.write(new_text)
                print(f"Updated {filepath}")
