package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientProcessor implements Runnable {

    private Socket sock;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;
    private Connection connection;

    public ClientProcessor(Socket pSock) throws SQLException, ClassNotFoundException {
        MySQLConnection db = new MySQLConnection("jdbc:mysql://localhost:3306/tarot_project","root","");
        this.connection = db.getConnection();
        sock = pSock;
    }

    //Le traitement lancé dans un thread séparé

    public void run() {
        System.err.println("Lancement du traitement de la connexion cliente");

        boolean closeConnexion = false;
        //tant que la connexion est active, on traite les demandes
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("coucou");
            System.out.println(sock);
            try {
                System.out.println("In try");
                //writer = new PrintWriter(sock.getOutputStream());
                //reader = new BufferedInputStream(sock.getInputStream());
                //On attend la demande du client
                InputStream inputStream = sock.getInputStream();

                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                List<String> responses = (List<String>) objectInputStream.readObject();
                System.out.println("Im after inputstream");
                System.out.println("REPONSE : "+responses);
                //String response = read();
                InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

                //On affiche quelques infos, pour le débuggage
                String debug = "";
                debug = "Thread : " + Thread.currentThread().getName() + ". ";
                debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() + ".";
                debug += " Sur le port : " + remote.getPort() + ".\n";
                debug += "Received [" + responses.size() + "] messages from: " + sock;
                System.err.println("\n" + debug);

                //On traite la demande du client en fonction de la commande envoyée
                System.out.println(sock);
                OutputStream outputStream = sock.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                List<String> toSend = new ArrayList<>();

                switch (responses.get(0).toUpperCase()) {
                    case "CONN":
                        String pseudo = responses.get(1);
                        String password = responses.get(2);
                        String query = "SELECT nom, prenom, email,pseudo, motdepasse FROM utilisateur WHERE pseudo=? AND motdepasse=?";
                        PreparedStatement ps = this.connection.prepareStatement(query);
                        ps.setString(1,pseudo);
                        ps.setString(2,password);
                        ResultSet results = ps.executeQuery();
                     System.out.println(responses.get(1));
                     System.out.println(responses.get(2));

                        if (results.next()) {
                            System.out.println("Utilisateur et mot de passe correct");
                            toSend.add("OK");
                            toSend.add(results.getString("nom"));
                            toSend.add(results.getString("prenom"));
                            toSend.add(results.getString("email"));

                        }
                        else {
                            System.out.println("Utilisateur et mot de passe incorrects");
                        }
                  /*  case "LIST":
                        //toSend.add(genericList(responses.get(1)));
                        break;*/
                    default:
                        toSend.add("UNKNOWN_COMMAND");
                        break;
                }

                objectOutputStream.writeObject(toSend);
                objectOutputStream.flush();
                objectOutputStream.close();

                if (closeConnexion) {
                    System.err.println("Fermeture de la connexion");
                    reader = null;
                    sock.close();
                    break;
                }
            } catch (SocketException e) {
                System.err.println("Interruption de la connexion");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
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
