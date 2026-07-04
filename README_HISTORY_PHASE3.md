# History Feature Phase 3
- Added `saveCurrentCalculation` and `loadFromHistory` functions in `EmiCalculatorViewModel`.
- Injected `HistoryRepository` via constructor using `EmiCalculatorViewModelFactory`.
- Replaced auto-saving logic in `EmiCalculatorScreen` with a manual "Save" button in the AppBar.
