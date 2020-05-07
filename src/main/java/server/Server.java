package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class Server
 */
public class Server {
    /**
     * Socket en attente de demande de connexion entrante
     */
    private ServerSocket serverSocket;
    private int port = 3333;
    private boolean isRunning = true;
    private String host = "192.168.1.40";

    /**
     * Constructeur du serveur
     */
    public Server() throws IOException {
        try {
            this.serverSocket = new ServerSocket(this.port,100, InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            System.out.println("Erreur lors de la construction du serveur (Hôte inconnu) - Server Constructor Error");
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Boucle d'écoute du serveur.
     */
    public void listen() throws IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning) {
                    try {
                        Socket client = serverSocket.accept();
                        System.out.println("Connexion cliente reçue");
                        Thread t = new Thread(new ClientProcessor(client));
                        t.start();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    serverSocket.close();
                } catch(IOException e) {
                    e.printStackTrace();
                    serverSocket = null;
                }
            }
        });
        t.start();
    }

    public void close() {
        this.isRunning = false;
    }

    /**
     * Programme principal
     */
    public static void main(String[]args) {
        try {
            Server server = new Server();
            server.listen();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
