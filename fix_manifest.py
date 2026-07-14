import os

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

if "com.google.android.gms.permission.AD_ID" not in content:
    content = content.replace('<uses-permission android:name="android.permission.INTERNET" />', 
                              '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>')
    with open(file_path, "w") as f:
        f.write(content)
    print("Manifest updated")
else:
    print("Already updated")
