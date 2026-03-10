const fs = require('fs');
const path = require('path');

const themePath = path.join(__dirname, 'src/assets/theme.css');

try {
    let content = fs.readFileSync(themePath, 'utf8');
    console.log('Read theme.css, length:', content.length);
    
    const newFont = "font-family: PingFangSC-Regular, 'Microsoft YaHei', sans-serif;";
    
    // Define patterns for problematic fonts
    // We use a broader regex to capture the whole line value
    const mappings = [
        { regex: /font-family:\s*'FRIZON'[^;]+;/g, name: 'FRIZON' },
        { regex: /font-family:\s*'AlimamaShuHeiTi-Bold'[^;]+;/g, name: 'Alimama' },
        { regex: /font-family:\s*'MiSans-Regular'[^;]+;/g, name: 'MiSans-Regular' },
        { regex: /font-family:\s*'MiSans-Medium'[^;]+;/g, name: 'MiSans-Medium' }
    ];
    
    let totalReplacements = 0;
    
    mappings.forEach(m => {
        const matches = content.match(m.regex);
        if (matches) {
            console.log(`Found ${matches.length} matches for ${m.name}`);
            content = content.replace(m.regex, newFont);
            totalReplacements += matches.length;
        } else {
            console.log(`No matches for ${m.name}`);
        }
    });
    
    if (totalReplacements > 0) {
        fs.writeFileSync(themePath, content, 'utf8');
        console.log(`Successfully updated ${totalReplacements} font definitions in theme.css`);
    } else {
        console.log('No changes needed.');
    }
    
} catch (err) {
    console.error('Error updating theme.css:', err);
}
