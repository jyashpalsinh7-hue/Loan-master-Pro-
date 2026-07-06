import os
import glob
import re

base_dir = "app/src/main/java/com/loanmaster/pro/feature"

features = {
    "emi": "Emi",
    "fd": "Fd",
    "home": "Home",
    "sip": "Sip",
    "rd": "Rd",
    "gst": "Gst",
    "currency": "Currency",
    "loaneligibility": "LoanEligibility",
    "prepayment": "Prepayment",
    "loansummary": "LoanSummary",
    "history": "History",
    "settings": "Settings",
    "compare": "Compare"
}

renames = []

for feat_dir, prefix in features.items():
    dir_path = os.path.join(base_dir, feat_dir)
    if not os.path.isdir(dir_path): continue
    
    # create components/
    comp_dir = os.path.join(dir_path, "components")
    if not os.path.exists(comp_dir):
        os.makedirs(comp_dir)
        
    for f in os.listdir(dir_path):
        if not f.endswith(".kt"): continue
        if f == "components": continue
        
        filepath = os.path.join(dir_path, f)
        
        # Determine the new name
        if "Screen" in f:
            new_name = prefix + "Screen.kt"
        elif "ViewModel" in f:
            new_name = prefix + "ViewModel.kt"
        elif "UiState" in f:
            new_name = prefix + "UiState.kt"
        else:
            continue
            
        new_path = os.path.join(dir_path, new_name)
        if filepath != new_path:
            old_class = f.replace(".kt", "")
            new_class = new_name.replace(".kt", "")
            renames.append((filepath, new_path, old_class, new_class))

# Perform class replacements across all .kt files
all_kt_files = glob.glob("app/src/main/java/com/loanmaster/pro/**/*.kt", recursive=True)

for filepath, new_path, old_class, new_class in renames:
    os.rename(filepath, new_path)
    
for kt_file in all_kt_files:
    if not os.path.exists(kt_file):
        # might have been renamed
        for filepath, new_path, old_class, new_class in renames:
            if kt_file == filepath:
                kt_file = new_path
                break
                
    if not os.path.exists(kt_file): continue
    
    with open(kt_file, "r") as f:
        content = f.read()
        
    original_content = content
    for _, _, old_class, new_class in renames:
        # Replace word boundaries
        content = re.sub(r'\b' + old_class + r'\b', new_class, content)
        
    if content != original_content:
        with open(kt_file, "w") as f:
            f.write(content)
            
