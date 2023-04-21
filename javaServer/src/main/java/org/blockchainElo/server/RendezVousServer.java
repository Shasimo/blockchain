package org.blockchainElo.server;

import org.blockchainElo.data.Group;
import org.blockchainElo.server.node_handler.RendezVousNodeHandler;
import org.blockchainElo.util.Const;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RendezVousServer extends Server{
    public RendezVousServer(ServerSocket serverSocket) {
        super(serverSocket);
    }

    @Override
    protected void handleNode(Socket socket) {
        RendezVousNodeHandler rendezVousNodeHandler = new RendezVousNodeHandler(socket);
        rendezVousNodeHandler.setGroup(Group.SUPER_NODE);
        Thread thread = new Thread(rendezVousNodeHandler);
        thread.start();
    }


    public static void main(String[] args) throws IOException {
//        ServerSocket serverSocket = new ServerSocket(8080);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(Const.RENDEZVOUS_SERVER_IP,Const.RENDEZVOUS_SERVER_PORT));
        Server server = new RendezVousServer(serverSocket);

        RendezVousNodeHandler phantomRendezVousNodeHandler = new RendezVousNodeHandler();
        // used for starting the timer without binding a real user to it thus making him unable to receive ad
        server.runServer();
    }
}
