import os

file_path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

import_statement = "import com.google.android.gms.ads.MobileAds\n"
if "import com.google.android.gms.ads.MobileAds" not in content:
    content = content.replace("import android.os.Bundle", import_statement + "import android.os.Bundle")

init_statement = """        super.onCreate(savedInstanceState)
        
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}
"""
if "MobileAds.initialize" not in content:
    content = content.replace("        super.onCreate(savedInstanceState)", init_statement)

with open(file_path, "w") as f:
    f.write(content)
