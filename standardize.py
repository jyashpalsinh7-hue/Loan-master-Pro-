import os
import re

feature_dir = "app/src/main/java/com/loanmaster/pro/feature/"
features_to_process = ["home", "loansummary", "prepayment", "currency", "loaneligibility", "history", "settings", "compare"]

for feature in features_to_process:
    path = os.path.join(feature_dir, feature)
    if not os.path.isdir(path):
        continue
    
    vm_file = None
    for file in os.listdir(path):
        if "ViewModel.kt" in file:
            vm_file = os.path.join(path, file)
            break
            
    if vm_file:
        # Check if UiState file already exists
        has_uistate = any(f.endswith("UiState.kt") for f in os.listdir(path))
        if has_uistate:
            print(f"Skipping {feature} because UiState already exists.")
            continue
            
        with open(vm_file, "r") as f:
            content = f.read()
            
        # Match from "data class ...UiState" or "data class ...State" to the start of "class ...ViewModel"
        # Since there might be sealed classes etc. in between, we can just grab everything from the first data/sealed class 
        # up to "class ...ViewModel"
        
        # Package and imports:
        pkg_imports = re.search(r'(package [\s\S]*?(?:import [\s\S]*?\n)*\n)', content)
        pkg_imports_str = pkg_imports.group(1) if pkg_imports else ""
        
        # Classes to extract:
        classes_match = re.search(r'((?:data class|sealed class|sealed interface|enum class)[\s\S]*?)(?=\n@HiltViewModel|\nclass \w+ViewModel|\nclass \w+ :|\nopen class)', content)
        
        if classes_match:
            classes_str = classes_match.group(1).strip()
            if "UiState" in classes_str or "Event" in classes_str or "State" in classes_str:
                print(f"Extracting for {feature}")
                feature_cap = feature.capitalize()
                
                # if there is a prefix like MainViewModel, maybe the UiState is HomeUiState
                ui_state_file_name = f"{feature_cap}UiState.kt"
                if "MainViewModel" in vm_file:
                    ui_state_file_name = "HomeUiState.kt"
                elif "LoanSummary" in vm_file:
                    ui_state_file_name = "LoanSummaryUiState.kt"
                elif "Prepayment" in vm_file:
                    ui_state_file_name = "PrepaymentUiState.kt"
                elif "LoanEligibility" in vm_file:
                    ui_state_file_name = "LoanEligibilityUiState.kt"
                elif "LoanComparison" in vm_file:
                    ui_state_file_name = "LoanComparisonUiState.kt"
                    
                ui_state_path = os.path.join(path, ui_state_file_name)
                
                # Write UiState.kt
                with open(ui_state_path, "w") as f:
                    f.write(pkg_imports_str)
                    f.write("\n")
                    f.write(classes_str)
                    f.write("\n")
                
                # Remove extracted classes from ViewModel.kt
                new_vm_content = content.replace(classes_match.group(1), "")
                with open(vm_file, "w") as f:
                    f.write(new_vm_content)
        else:
            print(f"No classes found to extract for {feature}")

