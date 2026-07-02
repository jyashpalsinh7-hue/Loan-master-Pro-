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

  if (content.includes('import androidx.compose.material.icons.rounded.*') && !content.includes('import androidx.compose.material.icons.automirrored.rounded.*')) {
    content = content.replace(
      'import androidx.compose.material.icons.rounded.*',
      'import androidx.compose.material.icons.rounded.*\nimport androidx.compose.material.icons.automirrored.rounded.*'
    );
  }

  // Handle specific imports like TrendingUp or CompareArrows if they were explicitly imported
  content = content.replace(/import androidx\.compose\.material\.icons\.rounded\.TrendingUp/g, 'import androidx.compose.material.icons.automirrored.rounded.TrendingUp');
  content = content.replace(/import androidx\.compose\.material\.icons\.rounded\.TrendingDown/g, 'import androidx.compose.material.icons.automirrored.rounded.TrendingDown');
  content = content.replace(/import androidx\.compose\.material\.icons\.rounded\.ArrowBack/g, 'import androidx.compose.material.icons.automirrored.rounded.ArrowBack');
  content = content.replace(/import androidx\.compose\.material\.icons\.rounded\.CompareArrows/g, 'import androidx.compose.material.icons.automirrored.rounded.CompareArrows');
  content = content.replace(/import androidx\.compose\.material\.icons\.rounded\.ReceiptLong/g, 'import androidx.compose.material.icons.automirrored.rounded.ReceiptLong');

  if (content !== original) {
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`Updated imports in ${filePath}`);
  }
});
