import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """        if (maturityValue > 5000000) {
            items.add(FutureGoal("Premium Apartment", 15000000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Luxury SUV", 7500000.0, Icons.Rounded.DirectionsCar))
        } else if (maturityValue > 1500000) {
            items.add(FutureGoal("Home Downpayment", 2500000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Mahindra XUV700", 2200000.0, Icons.Rounded.DirectionsCar))
        } else {
            items.add(FutureGoal("Honda City", 1200000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("International Travel", 600000.0, Icons.Rounded.FlightTakeoff))
        }
        items.add(FutureGoal("Emergency Fund", Math.max(500000.0, maturityValue * 1.5), Icons.Rounded.AccountBalanceWallet))
        items.add(FutureGoal("Child Education", Math.max(1000000.0, maturityValue * 2.0), Icons.Rounded.School))"""

replacement = """        if (maturityValue > 5000000) {
            items.add(FutureGoal("Premium Apartment", 15000000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Luxury SUV", 7500000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("Emergency Fund", 2500000.0, Icons.Rounded.AccountBalanceWallet))
            items.add(FutureGoal("Child Education", 5000000.0, Icons.Rounded.School))
        } else if (maturityValue > 1500000) {
            items.add(FutureGoal("Home Downpayment", 2500000.0, Icons.Rounded.Home))
            items.add(FutureGoal("Mahindra XUV700", 2200000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("Emergency Fund", 1000000.0, Icons.Rounded.AccountBalanceWallet))
            items.add(FutureGoal("Child Education", 2000000.0, Icons.Rounded.School))
        } else {
            items.add(FutureGoal("Honda City", 1200000.0, Icons.Rounded.DirectionsCar))
            items.add(FutureGoal("International Travel", 600000.0, Icons.Rounded.FlightTakeoff))
            items.add(FutureGoal("Emergency Fund", 500000.0, Icons.Rounded.AccountBalanceWallet))
            items.add(FutureGoal("Child Education", 1000000.0, Icons.Rounded.School))
        }"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched sip screen")
else:
    print("Target not found")
