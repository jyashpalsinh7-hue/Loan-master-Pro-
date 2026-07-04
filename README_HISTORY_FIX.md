# History Card Fix
- Replaced the simple `historyCount` in `HomeScreen` with `historyItems: List<CalculationHistory>`.
- Refactored `RecentCalculationsBanner` to display a list of the 3 most recent calculations using `HistoryItemCard`.
- Updated `AppNavigation` to pass `historyList` into `HomeScreen`.
