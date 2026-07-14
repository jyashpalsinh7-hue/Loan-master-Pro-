import os

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

# Remove old attribution if exists
import re
content = re.sub(r'<attribution android:tag="default_tag" android:label="@string/app_name" />\n\s*', '', content)

if 'attributionTags' not in content:
    # Add attribution tag and attribute
    content = content.replace('<application', '<attribution android:tag="null" android:label="@string/app_name" />\n    <application android:attributionTag="null"')
    with open(file_path, "w") as f:
        f.write(content)
    print("Manifest updated")
else:
    print("Already updated")
