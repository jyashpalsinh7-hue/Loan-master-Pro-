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

    // Add val currencySym = extractCurrencySymbol(LocalCurrency.current) inside the main Composable
    // Also replace hardcoded "₹" with ${currencySym} and formatMoney calls without currencySym

    // We can do this simply by replacing formatMoney(...) with formatMoney(..., currencySym) 
    // And "₹" with "${currencySym}"
    
    // First, let's inject currencySym at the top of the main composable.
    // To do this, we can search for the first `@Composable\nfun <Name>(...) {`
    // and insert `val currencySym = extractCurrencySymbol(LocalCurrency.current)`
    
    let match = content.match(/@Composable\s+fun\s+\w+\(.*?\)\s*\{/);
    if (match) {
        let insertPos = match.index + match[0].length;
        // Make sure it's not already there
        if (!content.includes('val currencySym = extractCurrencySymbol(LocalCurrency.current)')) {
             content = content.slice(0, insertPos) + '\n    val currencySym = extractCurrencySymbol(LocalCurrency.current)' + content.slice(insertPos);
        }
    }
    
    // Convert "₹" directly to ${currencySym}
    // But what if it's inside a string that doesn't use $ yet?
    // Let's replace "₹" with ${currencySym} globally, but only in strings (which it is)
    // Be careful with replacing '₹' inside 'replace("₹", "")'
    content = content.replace(/"₹"/g, 'currencySym');
    content = content.replace(/₹/g, '${currencySym}');
    
    // Remove "replace("${currencySym}", "")" because it's replacing nothing, well wait, 
    // replacing currencySym with ""
    content = content.replace(/\.replace\("\$\{currencySym\}", ""\)/g, '.replace(currencySym, "")');

    // Also replace `formatMoney(foo)` with `formatMoney(foo, currencySym)`
    // where formatMoney only has 1 argument
    // Regex: formatMoney( followed by anything that doesn't have a comma, then )
    // A bit tricky because of nested parentheses `formatMoney(a + b(c))`
    // Let's just do an advanced replace
    
    fs.writeFileSync(file, content, 'utf8');
}
