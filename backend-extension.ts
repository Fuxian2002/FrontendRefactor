/*
 * Copyright (C) 2017 TypeFox and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
import * as stream from 'stream'
import * as cp from 'child_process'
import { injectable, ContainerModule } from "inversify";
import { BaseLanguageServerContribution, LanguageServerContribution, IConnection } from "@theia/languages/lib/node";
// import { createSocketConnection } from 'vscode-ws-jsonrpc/lib/server'
import { StreamMessageReader, StreamMessageWriter, SocketMessageReader, SocketMessageWriter } from "vscode-jsonrpc";

import * as path from 'path'
import * as net from 'net'
import { MessageReader, MessageWriter, Disposable, Message } from 'vscode-jsonrpc';
import { DisposableCollection } from "vscode-ws-jsonrpc/lib/disposable";


export default new ContainerModule(bind => {
    bind<LanguageServerContribution>(LanguageServerContribution).to(pfContribution);
});

function getPort(): number | undefined {
    let arg = process.argv.filter(arg => arg.startsWith('--LSP_PORT='))[0]
    if (!arg) {
        return undefined
    } else {
        return Number.parseInt(arg.substring('--LSP_PORT='.length))
    }
}

@injectable()
class pfContribution extends BaseLanguageServerContribution {

    readonly id = "pf";
    readonly name = "PF";

    start(clientConnection: IConnection): void {
        openWebSocket()
        register(title,version)
        let socketPort = getPort();
        if (socketPort) {
            const socket = new net.Socket()
            const serverConnection = createSocketConnection(socket, socket, () => {
                socket.destroy()
            });
            this.forward(clientConnection, serverConnection)
            socket.connect(socketPort)
        } else {
            const jar = path.resolve(__dirname, '../../build/pf-language-server.jar');
    
            const command = 'java';
            const args: string[] = [
                '-jar',
                jar
            ];
            const serverConnection = this.createProcessStreamConnection(command, args);
            this.forward(clientConnection, serverConnection);
        }
    }
    //============@theia\languages\src\node\language-server-contribution========
    protected forward(clientConnection: IConnection, serverConnection: IConnection): void {
        forward(clientConnection, serverConnection, this.map.bind(this));

    }

    protected onDidFailSpawnProcess(error: Error): void {
        super.onDidFailSpawnProcess(error);
        console.error("Error starting pf language server.", error)
    }


}

//================vscode-ws-jsonrpc/lib/server/===================
export function createSocketConnection(outSocket: net.Socket, inSocket: net.Socket, onDispose: () => void): IConnection {
    const reader = new SocketMessageReader(outSocket);
    const writer = new SocketMessageWriter(inSocket);
    return createConnection(reader, writer, onDispose);
}

export function createProcessStreamConnection(process: cp.ChildProcess): IConnection {
    return createStreamConnection(process.stdout, process.stdin, () => process.kill());
}

export function createStreamConnection(outStream: stream.Readable, inStream: stream.Writable, onDispose: () => void): IConnection {
    const reader = new StreamMessageReader(outStream);
    const writer = new StreamMessageWriter(inStream);
    return createConnection(reader, writer, onDispose);
}
//===============vscode-ws-jsonrpc/lib/server/connection==============
export function forward(clientConnection: IConnection, serverConnection: IConnection, map?: (message: Message) => Message): void {
    clientConnection.forward(serverConnection, map);
    serverConnection.forward(clientConnection, map);
    clientConnection.onClose(() => serverConnection.dispose());
    serverConnection.onClose(() => clientConnection.dispose());
}
export interface IConnection extends Disposable {
    readonly reader: MessageReader;
    readonly writer: MessageWriter;
    forward(to: IConnection, map?: (message: Message) => Message): void;
    onClose(callback: () => void): Disposable;
}

export function createConnection(reader: MessageReader, writer: MessageWriter, onDispose: () => void): IConnection {
    const disposeOnClose = new DisposableCollection();
    reader.onClose(() => disposeOnClose.dispose());
    writer.onClose(() => disposeOnClose.dispose());
    return {
        reader, writer,
        forward(to: IConnection, map: (message: Message) => Message = (message) => message): void {
            reader.listen(input => {
                const output = map(input);
                to.writer.write(output)
                console.log(output)
                change("pf","pf",output,null)
            });
        },
        onClose(callback: () => void): Disposable {
            return disposeOnClose.push(Disposable.create(callback));
        },
        dispose: () => onDispose()
    }
}