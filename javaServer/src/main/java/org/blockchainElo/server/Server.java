package org.blockchainElo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server implements Runnable {
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


    protected void runServer() throws IOException { // in the future the method can be made private
        while (!this.serverSocket.isClosed()) {
            Socket socket = this.serverSocket.accept();
            System.out.println("new user connected!");
            handleNode(socket);

            // TODO : logging the connections
        }
        terminate();
    }

    public void terminate() throws IOException {
        this.serverSocket.close();
    }

    @Override
    public void run() {
        try {
            runServer();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected abstract void handleNode(Socket socket);
}
