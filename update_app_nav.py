import os

file_path = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """    NavigationSuiteScaffold(
        layoutType = customLayoutType,"""

replacement = """    Column(modifier = Modifier.fillMaxSize()) {
    NavigationSuiteScaffold(
        modifier = Modifier.weight(1f),
        layoutType = customLayoutType,"""

if "modifier = Modifier.weight(1f)," not in content:
    content = content.replace(target, replacement)
    
    target_end = """                        ) 
                    }
                }
            }"""
    
    replacement_end = """                        ) 
                    }
                }
            }
        }
        
        if (currentRoute != "splash") {
            com.loanmaster.pro.core.ui.AdMobBanner()
        }
    }"""
    # Let's do it safer:
    # Just find the end of the AppNavigation function
    # It ends with two closing braces (one for NavigationSuiteScaffold, one for AppNavigation)
    pass

