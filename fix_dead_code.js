const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/MainActivity.kt', 'utf8');
content = content.replace(/@Composable\nfun AppBottomBar[\s\S]*?\n\}\n/m, '');
fs.writeFileSync('app/src/main/java/com/example/MainActivity.kt', content, 'utf8');
