import os
import glob
import re

base_dir = "app/src/main/java/com/loanmaster/pro"

for root, _, files in os.walk(base_dir):
    for f in files:
        if f.endswith(".kt"):
            path = os.path.join(root, f)
            with open(path, "r") as file:
                content = file.read()
            
            # calculate correct package based on path
            rel_path = os.path.relpath(path, "app/src/main/java")
            package_name = os.path.dirname(rel_path).replace(os.sep, '.')
            
            if package_name:
                package_decl = f"package {package_name}"
                if package_decl not in content:
                    # replace the existing package
                    content = re.sub(r'package\s+[a-zA-Z0-9_\.]+', package_decl, content, 1)
                    with open(path, "w") as file:
                        file.write(content)
                        print(f"Fixed package in {path} to {package_decl}")
