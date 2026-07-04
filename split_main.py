import re

with open('app/src/main/java/com/loanmaster/pro/MainActivity.kt', 'r') as f:
    lines = f.readlines()

def get_imports_and_package():
    res = []
    for line in lines:
        if line.startswith('package ') or line.startswith('import '):
            res.append(line)
    return "".join(res)

imports_block = get_imports_and_package()

def find_line_index(start_str):
    for i, line in enumerate(lines):
        if line.startswith(start_str):
            return i
    return -1

home_screen_idx = find_line_index("@Composable\n") # wait, might be multiple
def find_fun_index(fun_name):
    for i, line in enumerate(lines):
        if line.startswith(f"fun {fun_name}") or line.startswith(f"suspend fun {fun_name}"):
            # check previous line for annotations
            start_idx = i
            while start_idx > 0 and lines[start_idx-1].startswith('@'):
                start_idx -= 1
            return start_idx
    return -1

home_screen_idx = find_fun_index("HomeScreen")
app_top_bar_idx = find_fun_index("AppTopBar")
search_and_premium_idx = find_fun_index("SearchAndPremiumRow")
hero_banner_idx = find_fun_index("HeroBanner")
calculators_section_idx = find_fun_index("CalculatorsSectionHeader")
emi_calculator_card_idx = find_fun_index("EmiCalculatorCard")
standard_calculator_card_idx = find_fun_index("StandardCalculatorCard")
recent_calcs_idx = find_fun_index("RecentCalculationsBanner")
quick_tools_section_idx = find_fun_index("QuickToolsSection")
quick_tool_item_idx = find_fun_index("QuickToolItem")

# Extract HomeScreen
home_screen_content = "".join(lines[home_screen_idx:quick_tools_section_idx])
with open('app/src/main/java/com/loanmaster/pro/HomeScreen.kt', 'w') as f:
    f.write(imports_block + "\n" + home_screen_content)

# Extract QuickTools
quick_tools_content = "".join(lines[quick_tools_section_idx:])
with open('app/src/main/java/com/loanmaster/pro/QuickToolsSection.kt', 'w') as f:
    f.write(imports_block + "\n" + quick_tools_content)

# Extract Navigation
nav_start = -1
nav_end = -1
for i, line in enumerate(lines):
    if "NavigationSuiteScaffold(" in line:
        nav_start = i
        break

# find the matching closing brace for NavigationSuiteScaffold
# It's at the end of MainActivity.kt before setContent closes
for i in range(nav_start, len(lines)):
    if "                }\n" == lines[i] and "            }\n" == lines[i+1]:
        nav_end = i + 1
        break

if nav_end == -1:
    # Just guess around line 321
    nav_end = home_screen_idx - 1

nav_content = "".join(lines[nav_start:nav_end])

app_nav_content = f"""{imports_block}

@Composable
fun AppNavigation(
    historyViewModel: HistoryViewModel,
    loanSummaryViewModel: LoanSummaryViewModel,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    navController: androidx.navigation.NavHostController = rememberNavController()
) {{
    val activeRoute by mainViewModel.activeBottomNavItem.collectAsStateWithLifecycle()
    val selectedHistory by mainViewModel.selectedHistory.collectAsStateWithLifecycle()
    
{nav_content}
}}
"""
with open('app/src/main/java/com/loanmaster/pro/AppNavigation.kt', 'w') as f:
    f.write(app_nav_content)

# Rewrite MainActivity.kt
main_activity_start = "".join(lines[:nav_start])
main_activity_end = """
                    AppNavigation(
                        historyViewModel = historyViewModel,
                        loanSummaryViewModel = loanSummaryViewModel,
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}
"""

with open('app/src/main/java/com/loanmaster/pro/MainActivity.kt', 'w') as f:
    f.write(main_activity_start + main_activity_end)

