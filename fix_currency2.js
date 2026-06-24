const fs = require('fs');
const files = [
    'app/src/main/java/com/example/CurrencyConverterScreen.kt',
    'app/src/main/java/com/example/FdCalculatorScreen.kt',
    'app/src/main/java/com/example/GstCalculatorScreen.kt',
    'app/src/main/java/com/example/LoanComparisonScreen.kt',
    'app/src/main/java/com/example/LoanEligibilityScreen.kt',
    'app/src/main/java/com/example/PrepaymentCalculatorScreen.kt',
    'app/src/main/java/com/example/RecommendationBottomSheet.kt',
    'app/src/main/java/com/example/EmiCalculatorScreen.kt',
    'app/src/main/java/com/example/RdCalculatorScreen.kt',
    'app/src/main/java/com/example/SipCalculatorScreen.kt'
];

for (const file of files) {
    if (!fs.existsSync(file)) continue;
    let content = fs.readFileSync(file, 'utf8');

    // Make sure we inject currencySym into the main composable
    // we use /s to match across newlines
    let match = content.match(/@Composable\s+(?:@OptIn\([^\)]+\)\s+)?fun\s+\w+\([^)]+\)\s*\{/s) || content.match(/@Composable\s+fun\s+\w+\([^)]+\)\s*\{/s);
    if (match) {
        let insertPos = match.index + match[0].length;
        if (!content.includes('val currencySym = extractCurrencySymbol(LocalCurrency.current)')) {
             content = content.slice(0, insertPos) + '\n    val currencySym = extractCurrencySymbol(LocalCurrency.current)' + content.slice(insertPos);
        }
    } else {
        console.log("No match found in", file);
    }
    
    // Also we need to fix localFormatMoney in LoanComparisonScreen
    if (file.includes('LoanComparisonScreen')) {
        content = content.replace(/private fun localFormatMoney\(amount: Double\): String \{/g, `private fun localFormatMoney(amount: Double, currencySym: String): String {`);
    }

    fs.writeFileSync(file, content, 'utf8');
}
