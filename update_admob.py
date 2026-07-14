import os
import re

file_path = "gradle/libs.versions.toml"
with open(file_path, "r") as f:
    content = f.read()

if "playServicesAds" not in content:
    content = content.replace("[versions]\n", "[versions]\nplayServicesAds = \"23.2.0\"\n")
    content = content.replace("[libraries]\n", "[libraries]\nplay-services-ads = { group = \"com.google.android.gms\", name = \"play-services-ads\", version.ref = \"playServicesAds\" }\n")
    with open(file_path, "w") as f:
        f.write(content)

file_path = "app/build.gradle.kts"
with open(file_path, "r") as f:
    content = f.read()
if "play-services-ads" not in content:
    content = content.replace("dependencies {", "dependencies {\n  implementation(libs.play.services.ads)")
    with open(file_path, "w") as f:
        f.write(content)

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()
if "com.google.android.gms.ads.APPLICATION_ID" not in content:
    content = content.replace("</application>", "  <meta-data\n        android:name=\"com.google.android.gms.ads.APPLICATION_ID\"\n        android:value=\"ca-app-pub-3940256099942544~3347511713\"/>\n    </application>")
    with open(file_path, "w") as f:
        f.write(content)

