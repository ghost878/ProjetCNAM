package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class Server
 */
public class Server {
    /**
     * Socket en attente de demande de connexion entrante
     */
    private ServerSocket serverSocket;

    /**
     * Constructeur du serveur
     */
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * Boucle d'écoute du serveur.
     */
    public void listen() throws IOException {
        // pool de threads à utiliser pour gérer les connexions
        ExecutorService pool = Executors.newCachedThreadPool();

        //Création de la liste des connexions
        ActiveConnections activeConnections = new ActiveConnections();
        System.out.println("Serveur démarré port 3333");
        while(true) {
            Socket clientSocket = serverSocket.accept();
            pool.submit(new ServerConnection(clientSocket,activeConnections));
        }
    }

    /**
     * Programme principal
     */
    public static void main(String[]args) {
        try {
            Server server = new Server(3333);
            server.listen();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
