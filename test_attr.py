import sys

path = "app/src/main/AndroidManifest.xml"
with open(path, "r") as f:
    content = f.read()

content = content.replace("<application", '<attribution android:tag="" android:label="@string/app_name" />\n    <application')

with open(path, "w") as f:
    f.write(content)
