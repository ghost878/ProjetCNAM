package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class ClientProcessor implements Runnable {

    private Socket sock;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;

    public ClientProcessor(Socket pSock) {
        sock = pSock;
    }

    //Le traitement lancé dans un thread séparé
    public void run() {
        System.err.println("Lancement du traitement de la connexion cliente");

        boolean closeConnexion = false;
        //tant que la connexion est active, on traite les demandes
        while (!sock.isClosed()) {

            try {
                writer = new PrintWriter(sock.getOutputStream());
                reader = new BufferedInputStream(sock.getInputStream());

                String response = read();
                InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

                String debug = "";
                debug = "Thread : " + Thread.currentThread().getName() + ". ";
                debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() + ".";
                debug += " Sur le port : " + remote.getPort() + ".\n";
                debug += "\t -> Commande reçue : " + response + "\n";
                System.err.println("\n" + debug);

                String toSend = "";

                switch (response.toUpperCase()) {
                    case "LOGIN":
                        toSend = "Client connecté";
                        break;
                    case "LOGOUT":
                        toSend = "Client deconnecté";
                        break;
                    case "CLOSE":
                        toSend = "Communication terminée";
                    case "PLAY":
                        toSend = "En attente d'une partie";
                        break;
                    default:
                        toSend = "Commande inconnu !";
                        break;
                }

                //On envoie la réponse au client
                writer.write(toSend);
                writer.flush();

                if (closeConnexion) {
                    System.err.println("Commande close détectée");
                    writer = null;
                    reader = null;
                    sock.close();
                    break;
                }
            } catch (SocketException e) {
                System.err.println("Connexion interrompue");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //La méthode que nous utilisons pour lire les réponses
    private String read() throws IOException {
        String response = "";
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        response = new String(b, 0, stream);
        return response;
    }
}
