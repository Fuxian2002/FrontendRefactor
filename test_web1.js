const http = require('http');

function searchProject(port, username) {
  const options = {
    hostname: 'localhost',
    port: port,
    path: `/file/searchProject?username=${username}`,
    method: 'GET'
  };

  const req = http.request(options, (res) => {
    let data = '';
    res.on('data', (chunk) => {
      data += chunk;
    });
    res.on('end', () => {
      console.log(`Port ${port} User '${username}': ${data}`);
    });
  });

  req.on('error', (e) => {
    console.error(`Port ${port} error: ${e.message}`);
  });

  req.end();
}

searchProject(7084, 'test');
searchProject(7084, '');
