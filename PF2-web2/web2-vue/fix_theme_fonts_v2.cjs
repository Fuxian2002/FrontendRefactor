const fs = require('fs');
const path = require('path');

const themePath = path.join(__dirname, 'src/assets/theme.css');

try {
    let content = fs.readFileSync(themePath, 'utf8');
    
    // Replace problematic fonts with Microsoft YaHei
    // We simply replace the font name with 'Microsoft YaHei' or remove it if 'Microsoft YaHei' is already next
    
    // Replace 'MiSans-Regular', 'Microsoft YaHei' -> 'Microsoft YaHei'
    content = content.replace(/'MiSans-Regular', 'Microsoft YaHei'/g, "'Microsoft YaHei'");
    
    // Replace 'MiSans-Medium', 'Microsoft YaHei' -> 'Microsoft YaHei'
    content = content.replace(/'MiSans-Medium', 'Microsoft YaHei'/g, "'Microsoft YaHei'");
    
    // Replace 'FRIZON', 'Microsoft YaHei' -> 'Microsoft YaHei'
    content = content.replace(/'FRIZON', 'Microsoft YaHei'/g, "'Microsoft YaHei'");
    
    // Replace 'AlimamaShuHeiTi-Bold', 'Microsoft YaHei' -> 'Microsoft YaHei'
    content = content.replace(/'AlimamaShuHeiTi-Bold', 'Microsoft YaHei'/g, "'Microsoft YaHei'");
    
    // Handle cases where 'Microsoft YaHei' might not be next, just in case
    content = content.replace(/'MiSans-Regular'/g, "'Microsoft YaHei'");
    content = content.replace(/'MiSans-Medium'/g, "'Microsoft YaHei'");
    
    // Uncomment commented out font-family lines
    content = content.replace(/\/\* font-family: 'Microsoft YaHei', sans-serif; \*\//g, "font-family: 'Microsoft YaHei', sans-serif;");
    
    fs.writeFileSync(themePath, content, 'utf8');
    console.log('Successfully updated theme.css font definitions.');
    
} catch (err) {
    console.error('Error updating theme.css:', err);
}
