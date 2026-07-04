import os
import re

directory = "app/src/main/java/com/loanmaster/pro"

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                text = f.read()
            
            new_text = text
            # Remove from AppTextField parameters
            new_text = re.sub(r"\s*sizeClass:\s*WindowWidthSizeClass[^,]*,\n?", "", new_text)
            new_text = re.sub(r",\s*sizeClass\s*=\s*sizeClass", "", new_text)
            new_text = re.sub(r"sizeClass\s*=\s*sizeClass,\s*", "", new_text)
            
            if new_text != text:
                with open(filepath, "w") as f:
                    f.write(new_text)
                print(f"Updated {filepath}")
