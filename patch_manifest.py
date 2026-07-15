import sys

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

target = """    <application"""
replacement = """    <attribution android:tag="AdMob" android:label="@string/app_name" />
    <application"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched manifest")
else:
    print("Target not found")
