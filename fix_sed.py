import os

for root, dirs, files in os.walk('app/src'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r') as f:
                content = f.read()
            if '.heightIn(min = .height(' in content:
                # E.g. .heightIn(min = .height(60.dp))
                import re
                content = re.sub(r'\.heightIn\(min = \.height\(([0-9]+)\.dp\)\)', r'.heightIn(min = \1.dp)', content)
                with open(path, 'w') as f:
                    f.write(content)
