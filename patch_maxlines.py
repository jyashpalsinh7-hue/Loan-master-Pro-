import sys, os, glob

for root, _, files in os.walk("app/src/main/java/com/loanmaster/pro/"):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            original = content
            content = content.replace("maxLines = 1, overflow = TextOverflow.Ellipsis", "minLines = 1, maxLines = 2, overflow = TextOverflow.Ellipsis")
            content = content.replace("maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis", "minLines = 1, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis")
            
            if content != original:
                with open(path, "w") as f:
                    f.write(content)
                print(f"Patched {path}")
