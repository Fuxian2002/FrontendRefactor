/*
 * Copyright (C) 2018 TypeFox and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
import * as rpc from "vscode-ws-jsonrpc/lib";
import * as server from "vscode-ws-jsonrpc/lib/server";
import * as lsp from "vscode-languageserver/lib/main";
// import newValue = require('./client')
// export let newValue
export function launch(socket: rpc.IWebSocket) {
    const reader = new rpc.WebSocketMessageReader(socket);
    const writer = new rpc.WebSocketMessageWriter(socket);
    // start the language server as an external process
    const socketConnection = server.createConnection(reader, writer, () => socket.dispose());
    // const serverConnection = server.createServerProcess('ts', 'node', [__dirname + '/startserver.js', '--stdio']);
    const serverConnection = server.createServerProcess('pf', 'java', ['-jar','pf-language-server-new.jar', '--stdio']);
    // console.log("socketConnection: socket=",socket)
    // console.log("serverConnection  =",serverConnection)
    server.forward(socketConnection, serverConnection, message => {        
        if (rpc.isRequestMessage(message)) {
            if (message.method === lsp.InitializeRequest.type.method) {
                const initializeParams = message.params as lsp.InitializeParams;
                initializeParams.processId = process.pid;
                console.log("process.pid")
                console.log(process.pid)
            }
        }
        // 检测结果
        let result:any = message
        if(result.method && result.method=="textDocument/publishDiagnostics"){
            console.log(result.params.uri)
            if(result.params.diagnostics.length>=0){
                console.log("==========result.params.diagnostics==============")
                console.log(result.params.diagnostics)
                // console.log(client.editor.getValue())
            }
        }
        //更改
        else if(result.method && result.method=="textDocument/didChange"){
            console.log("==========textDocument/didChange==============")
            if(result.params.textDocument.uri)
                console.log(result.params.textDocument.uri)
            if(result.params.textDocument.version){
                console.log("version: ",result.params.textDocument.version)
            }
        }
        else if(result.method){            
            console.log("=========="+result.method+"==============")
            if(result.params && result.params.textDocument && result.params.textDocument.uri)
                console.log(result.params.textDocument.uri)
            if(result.params && result.params.textDocument && result.params.textDocument.version){
                console.log("version: ",result.params.textDocument.version)
            }
        }
        return message;
    });
}