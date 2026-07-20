import os
import re

for root, dirs, files in os.walk('app/src'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r') as f:
                content = f.read()
            
            # Find and replace Modifier.width(X.dp) and Modifier.height(X.dp) with widthIn / heightIn
            new_content = re.sub(r'\.width\(([0-9]+)\.dp\)', r'.widthIn(min = \1.dp)', content)
            
            if new_content != content:
                with open(path, 'w') as f:
                    f.write(new_content)
