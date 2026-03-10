/*
 * Copyright (C) 2018 TypeFox and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
require('monaco-editor-core');
import { listen, MessageConnection } from 'vscode-ws-jsonrpc';
import {
    MonacoLanguageClient, CloseAction, ErrorAction,
    MonacoServices, createConnection, TextDocumentPositionParams,
    ProtocolToMonacoConverter
} from 'monaco-languageclient';
// import { TypeScriptRenameRequest } from 'problemframe-language-server/lib/ts-protocol';
import normalizeUrl = require('normalize-url');
const ReconnectingWebSocket = require('reconnecting-websocket');
export function abc(){
    (self as any).MonacoEnvironment = {
        getWorkerUrl: () => './editor.worker.bundle.js'
    }
    // register Monaco languages
    monaco.languages.register({
        id: 'problemframe',
        aliases: [
            'ProblemFrame',
            'problemframe',
            'pf'
        ],
        extensions: [
            '.pf'
        ],
        mimetypes: [
            'text/problemframe'
        ]
    });
    // create Monaco editor
const value = `problem: #Flexible Modeling#
// Your problem frame descriptions goes here
Requirement R "Make the modeling flexible"
World P "Models"
Machine M "Modeling wiki"
Machine -- World {
  event SharedPhenomenon
} "a"
World ~~ Requirement {
  state ReferredPhenomenon
} "b"
Machine <~ Requirement {
  state ConstrainedPhenomenon
} "c"
`;
// const rootUri = (window as any).rootUri;
const rootUri = "file://root/pf-language-server/test-data"
// const rootUri = "file://C:/PF/LSP_Server/pf-language-server/test-data"
const editor = monaco.editor.create(document.getElementById("container")!, {
    model: monaco.editor.createModel(value, "problemframe", monaco.Uri.parse(rootUri + '/bar.pf')),
    glyphMargin: true,
    lightbulb: {
        enabled: true
    }
});
 let newValue=value
editor.onDidChangeModelContent((event) => {
    newValue=editor.getValue();
    console.log(newValue)
   })
// install Monaco language client services
MonacoServices.install(editor, { rootUri });
const p2m = new ProtocolToMonacoConverter();
// editor.setValue(newValue);
// create the web socket
const url = createUrl('/sampleServer')
const webSocket = createWebSocket(url);
// listen when the web socket is opened
listen({
    webSocket,
    onConnection: connection => {
        // create and start the language client
        const languageClient = createLanguageClient(connection);
        languageClient.onReady().then(() => {
            // languageClient.onRequest(TypeScriptRenameRequest.type, params => {
            //     editor.setPosition(p2m.asPosition(params.position));
            //     editor.trigger('', 'editor.action.rename', {});
            // });
        });
        const disposable = languageClient.start();
        connection.onClose(() => disposable.dispose());
    }
});
}
abc()

function createLanguageClient(connection: MessageConnection): MonacoLanguageClient {
    return new MonacoLanguageClient({
        name: "ProblemFrame Language Client",
        clientOptions: {
            // use a language id as a document selector
            documentSelector: ['problemframe'],
            // disable the default error handler
            errorHandler: {
                error: () => ErrorAction.Continue,
                closed: () => CloseAction.DoNotRestart
            }
        },
        // create a language client connection from the JSON RPC connection on demand
        connectionProvider: {
            get: (errorHandler, closeHandler) => {
                return Promise.resolve(createConnection(connection, errorHandler, closeHandler))
            }
        }
    });
}

function createUrl(path: string): string {
    const protocol = location.protocol === 'https:' ? 'wss' : 'ws';
    return normalizeUrl(`${protocol}://${location.host}${location.pathname}${path}`);
}

function createWebSocket(url: string): WebSocket {
    const socketOptions = {
        maxReconnectionDelay: 10000,
        minReconnectionDelay: 1000,
        reconnectionDelayGrowFactor: 1.3,
        connectionTimeout: 10000,
        maxRetries: Infinity,
        debug: false
    };
    return new ReconnectingWebSocket(url, undefined, socketOptions);
}