import os

for root, dirs, files in os.walk('app/src'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r') as f:
                content = f.read()
            if 'remember { mutableStateOf' in content:
                content = content.replace('remember { mutableStateOf', 'androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf')
                with open(path, 'w') as f:
                    f.write(content)
