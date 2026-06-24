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

    // Replace ${currencySym} with ${globalCurrencySymbol}
    content = content.replace(/\$\{currencySym\}/g, '${com.example.globalCurrencySymbol}');
    
    // Replace currencySym + with com.example.globalCurrencySymbol +
    content = content.replace(/currencySym \+/g, 'com.example.globalCurrencySymbol +');
    // Replace replace(currencySym, "") with replace(com.example.globalCurrencySymbol, "")
    content = content.replace(/\.replace\(currencySym, ""\)/g, '.replace(com.example.globalCurrencySymbol, "")');

    // Replace localFormatMoney(amount: Double, currencySym: String = currencySym)
    // back to localFormatMoney(amount: Double) or localFormatMoney(amount: Double, currencySym: String = com.example.globalCurrencySymbol)
    content = content.replace(/localFormatMoney\(amount: Double, currencySym: String(.*?)\)/g, 'localFormatMoney(amount: Double, currencySym: String = com.example.globalCurrencySymbol)');
    
    // formatMoney(foo, currencySym) -> formatMoney(foo) // wait, formatMoney is already going to use globalCurrencySymbol by default if I edit SharedUI, but I can just leave formatMoney(foo, com.example.globalCurrencySymbol)
    content = content.replace(/, currencySym\)/g, ', com.example.globalCurrencySymbol)');
    
    // Remote the val currencySym line
    content = content.replace(/\s*val currencySym = extractCurrencySymbol\(LocalCurrency\.current\)/g, '');

    fs.writeFileSync(file, content, 'utf8');
}
