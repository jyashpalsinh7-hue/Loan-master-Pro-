import os

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

if "android.permission.ACCESS_NETWORK_STATE" not in content:
    content = content.replace('<uses-permission android:name="android.permission.INTERNET" />', 
                              '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />')
    with open(file_path, "w") as f:
        f.write(content)
    print("Manifest updated")
else:
    print("Already updated")
