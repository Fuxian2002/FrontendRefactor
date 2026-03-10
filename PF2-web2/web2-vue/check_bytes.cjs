const fs = require('fs');
const buf = fs.readFileSync('src/components/TopBar.vue');
console.log('Hex Dump of first 500 bytes:');
console.log(buf.slice(0, 500).toString('hex'));
console.log('String representation (utf8):');
console.log(buf.slice(0, 500).toString('utf8'));
