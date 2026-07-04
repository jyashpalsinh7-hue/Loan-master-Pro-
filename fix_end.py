import re
with open("app/src/main/java/com/loanmaster/pro/SharedUI.kt", "r") as f:
    text = f.read()

target = """            }
        }
    }
}

@Composable"""

replacement = """            }
        }
    }
    }
}

@Composable"""

text = text.replace(target, replacement)
with open("app/src/main/java/com/loanmaster/pro/SharedUI.kt", "w") as f:
    f.write(text)
