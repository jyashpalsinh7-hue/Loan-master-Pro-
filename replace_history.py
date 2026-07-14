import sys
import re

file_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                        } else if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search history...", color = TextSecondary) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }"""

replacement = """                        } else if (isSearchActive) {
                            val focusRequester = remember { FocusRequester() }
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search history...", color = TextSecondary) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                            )
                        }"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("HistoryScreen updated")
else:
    print("Target not found in HistoryScreen.kt")
