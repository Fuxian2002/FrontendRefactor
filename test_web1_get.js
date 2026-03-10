const http = require('http');

function getProject(port, username, project) {
  const options = {
    hostname: 'localhost',
    port: port,
    path: `/file/getProject/${project}/undefined?username=${username}`,
    method: 'GET'
  };

  const req = http.request(options, (res) => {
    let data = '';
    res.on('data', (chunk) => {
      data += chunk;
    });
    res.on('end', () => {
      console.log(`Port ${port} User '${username}' Project '${project}':`);
      try {
        const json = JSON.parse(data);
        console.log("Success! Project Title:", json.title);
        console.log("IntentDiagram:", json.intentDiagram ? "Exists" : "NULL");
        console.log("ProblemDiagram:", json.problemDiagram ? "Exists" : "NULL");
        console.log("ContextDiagram:", json.contextDiagram ? "Exists" : "NULL");
      } catch (e) {
        console.log("Failed to parse JSON:", data.substring(0, 200));
      }
    });
  });

  req.on('error', (e) => {
    console.error(`Port ${port} error: ${e.message}`);
  });

  req.end();
}

getProject(7084, 'test', 'TestPowerOn');
