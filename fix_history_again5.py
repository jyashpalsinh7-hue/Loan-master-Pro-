import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """                }
            }
        }
    }
}

@Composable
fun EmptyHistoryIllustration"""

replacement = """                }
            }
        }
        }
    }
}

@Composable
fun EmptyHistoryIllustration"""

content = content.replace(target, replacement)

with open(filename, 'w') as f:
    f.write(content)
print("Done")
