package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Classe fournissant les fonctionnalités de communication avec un client. Une instance de cette classe est créée chaque fois
 * qu'un client se connecte.
 */
public class ServerConnection implements Runnable {

    // socket de communication avec le client qui a ouvert cette connexion
    private Socket socket;

    // flot de sortie associé au socket
    private PrintStream output;

    // flot d'entrée associé au socket
    private BufferedReader input;

    // état de la connexion
    private boolean active = false;

    // liste des connexions actives
    private ActiveConnections activeConnections;

    // copie locale du pseudonyme du client
    private String pseudo = null;

    /**
     * Constructeur
     *
     * @param socket: socket de communication avec le client
     * @param activeConnections: instance du serveur sur lequel cette connexion a été ouverte
     * @throws IOException: en cas d'erreur lors de la création des flots d'entrée/sortie
     */
    public ServerConnection(Socket socket, ActiveConnections activeConnections) throws IOException {
        this.socket = socket;
        this.activeConnections = activeConnections;
        this.output = new PrintStream(this.socket.getOutputStream());
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    /**
     * Met à jour le pseudonyme du client. Si la mise à jour est effective, une
     * commande #alias est envoyée au client. Sinon, une commande #error est
     * émise.
     *
     * @param newPseudo
     *            nouveau pseudonyme
     * @return true si la mise à jour a pu être effectuée, false sinon (newAlias
     *         vaut null ou est déjà utilisé ou contient un caractère invalide)
     */
    public boolean updatePseudo(String newPseudo) {
        this.pseudo = newPseudo;
        sendToClient("#alias " + newPseudo);

        return true;
    }

    /**
     * Envoi d'un message au client qui a ouvert cette connexion
     *
     * @param message
     *            message à envoyer
     */
    public void sendToClient(String message) {
        output.println(message);
    }

    /**
     * Echange initial et boucle de communication avec le client
     */
    @Override
    public void run() {
        try {
            this.active = this.updatePseudo(this.input.readLine());
            if (this.active) {
                this.activeConnections.add(this);
                this.sendToClient(this.activeConnections.toString());
            }
            while(this.active) {
                String message = this.input.readLine();
            }
        } catch(Exception e) {
        }
        finally {
            try {
                this.activeConnections.remove(this);
                this.socket.close();
            } catch(Exception e) {
            }
        }
    }
}
