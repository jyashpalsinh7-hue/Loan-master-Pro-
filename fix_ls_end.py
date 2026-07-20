import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """                }
            }
        }        }
    }

    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onSave = { loan ->
                viewModel.addLoan(loan)
                showAddLoanDialog = false
            }
        )
    }
}
    }"""

replacement = """                }
            }
        }        }
        
        FloatingActionButton(
            onClick = { showAddLoanDialog = true },
            containerColor = accentYellow,
            contentColor = bgDark,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(LoanMasterTheme.spacing.lg)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add")
        }
    } // closes Box

    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onSave = { loan ->
                viewModel.addLoan(loan)
                showAddLoanDialog = false
            }
        )
    }
}"""

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed LoanSummaryScreen end.")
else:
    print("Target not found.")

