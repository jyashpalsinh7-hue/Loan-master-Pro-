const fs = require('fs');
const path = require('path');

function walk(dir, fileList = []) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const filePath = path.join(dir, file);
    if (fs.statSync(filePath).isDirectory()) {
      walk(filePath, fileList);
    } else if (filePath.endsWith('.kt')) {
      fileList.push(filePath);
    }
  }
  return fileList;
}

const ktFiles = walk('app/src/main/java/com/example');

ktFiles.forEach(filePath => {
  let content = fs.readFileSync(filePath, 'utf8');
  let original = content;

  content = content.replace(/Icons\.Rounded\.TrendingUp/g, 'Icons.AutoMirrored.Rounded.TrendingUp');
  content = content.replace(/Icons\.Rounded\.TrendingDown/g, 'Icons.AutoMirrored.Rounded.TrendingDown');
  content = content.replace(/Icons\.Rounded\.ArrowBack/g, 'Icons.AutoMirrored.Rounded.ArrowBack');
  content = content.replace(/Icons\.Rounded\.CompareArrows/g, 'Icons.AutoMirrored.Rounded.CompareArrows');
  content = content.replace(/Icons\.Rounded\.ReceiptLong/g, 'Icons.AutoMirrored.Rounded.ReceiptLong');
  
  content = content.replace(/Locale\(\"en\", \"US\"\)/g, 'Locale.Builder().setLanguage("en").setRegion("US").build()');
  content = content.replace(/Locale\(\"en\", \"IN\"\)/g, 'Locale.Builder().setLanguage("en").setRegion("IN").build()');
  
  content = content.replace(/java\.util\.Locale\(\"en\", \"IN\"\)/g, 'java.util.Locale.Builder().setLanguage("en").setRegion("IN").build()');
  content = content.replace(/java\.util\.Locale\(\"en\", \"US\"\)/g, 'java.util.Locale.Builder().setLanguage("en").setRegion("US").build()');

  if (content !== original) {
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`Updated ${filePath}`);
  }
});
