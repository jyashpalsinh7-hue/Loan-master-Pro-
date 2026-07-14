import os

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

if "default_tag" not in content:
    content = content.replace('<application', '<attribution android:tag="default_tag" android:label="@string/app_name" />\n    <application\n        android:name=".LoanMasterApp"')
    with open(file_path, "w") as f:
        f.write(content)
    print("Manifest updated")
else:
    print("Already updated")
