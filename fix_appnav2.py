with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.runtime.remember\\n\\n@Composable\\nfun AppNavigation(", "@Composable\\nfun AppNavigation(")
content = content.replace("import androidx.compose.runtime.compositionLocalOf\\nimport androidx.compose.runtime.remember\\n\\n@Composable", "import androidx.compose.runtime.compositionLocalOf\\nimport androidx.compose.runtime.remember\\n\\n@Composable")

# Let's just put import androidx.compose.runtime.remember at the top
content = content.replace("import androidx.compose.runtime.remember\n\n@Composable\nfun AppNavigation(", "@Composable\nfun AppNavigation(")
if "import androidx.compose.runtime.remember" not in content[:500]:
    content = content.replace("import androidx.navigation.compose.NavHost", "import androidx.navigation.compose.NavHost\nimport androidx.compose.runtime.remember")

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "w") as f:
    f.write(content)
