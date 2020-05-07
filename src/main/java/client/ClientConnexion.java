package client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ClientConnexion implements Runnable {

    private Socket connection;
    private PrintWriter writer;
    private BufferedInputStream reader;
    private static int count = 0;
    private String pseudo;
    private String[] listCommands = {"LOGIN","LOGOUT","NONE"};

    /**
     *
     * @param host
     * @param port
     */
    public ClientConnexion(String host, int port, String pseudo) {
        try {
            this.pseudo = pseudo;
            this.connection = new Socket(host,port);
        } catch(UnknownHostException e) {
            System.out.println("Erreur - Hôte inconnu");
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * Simulation d'envoie de commande de client au serveur.
     */
    public void run(){
        for(int i =0; i < 10; i++){
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                writer = new PrintWriter(this.connection.getOutputStream(), true);
                reader = new BufferedInputStream(this.connection.getInputStream());
                //On envoie la commande au serveur
                String commande = this.getCommand();
                writer.write(commande);
                writer.flush();
                System.out.println("Commande " + commande + " envoyée au serveur");

                //On attend la réponse
                String reponse = read();
                System.out.println("\t * " + this.pseudo + " Réponse reçue " + reponse);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer.write("CLOSE");
        writer.flush();
        writer.close();
    }

    /**
     * Accesseur sur les commandes.
     * @return String
     */
    private String getCommand() {
        Random rand = new Random();
        return listCommands[rand.nextInt(listCommands.length)];
    }

    /**
     * Methode de lecture des réponses du serveur.
     * @return
     * @throws IOException
     */
    private String read() throws IOException{
        String response = "";
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        response = new String(b, 0, stream);
        return response;
    }

    public static void main(String[]args) {
         //ClientConnexion cltConnexion = new ClientConnexion("192.168.1.40",3333);
    }
}
