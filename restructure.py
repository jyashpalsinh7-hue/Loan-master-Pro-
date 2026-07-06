import os
import re

features = [
    "emi", "fd", "home", "sip", "rd", "gst", "currency",
    "loaneligibility", "prepayment", "loansummary", "history", "settings", "compare"
]

base_dir = "app/src/main/java/com/loanmaster/pro/feature"

for feature in features:
    feature_dir = os.path.join(base_dir, feature)
    components_dir = os.path.join(feature_dir, "components")
    if not os.path.exists(components_dir):
        os.makedirs(components_dir)
        print(f"Created {components_dir}")
        
    # Let's list what we have in this feature
    files = [f for f in os.listdir(feature_dir) if f.endswith(".kt")]
    print(f"Feature {feature}: {files}")
