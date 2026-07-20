import sys

filepath = 'app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """          }
        }
    )
    
    if (showUnlockDialog) {
        val dialogContext = androidx.compose.ui.platform.LocalContext.current
        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
            onDismiss = { showUnlockDialog = false },
            onUnlockSuccessful = { 
                
                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()
            }
        )
    }
  }
 }
}"""

replacement = """          }
        }
    )
    } // closes Box
    
    if (showUnlockDialog) {
        val dialogContext = androidx.compose.ui.platform.LocalContext.current
        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
            onDismiss = { showUnlockDialog = false },
            onUnlockSuccessful = { 
                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()
            }
        )
    }
} // closes RdScreen"""

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed RdScreen end")
else:
    print("Target not found. Doing manual replace.")
    # Fallback: find ") \n \n if (showUnlockDialog)"
