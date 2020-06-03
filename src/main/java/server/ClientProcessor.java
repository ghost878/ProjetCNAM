package server;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientProcessor implements Runnable {

    private Socket sock;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;
    private Connection connection;
    public static int currentnumJoueur = 0;
    public static int currentEquipe =1;
    public static int currentPartie =1;
    public static boolean callDone = false;


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
            try {
                //writer = new PrintWriter(sock.getOutputStream());
                //reader = new BufferedInputStream(sock.getInputStream());
                //On attend la demande du client
                InputStream inputStream = sock.getInputStream();

                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                List<Object> responses = (List<Object>) objectInputStream.readObject();
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
                List<Object> toSend = new ArrayList<>();
                if(responses.get(0).toString().toUpperCase().equals("CONN")) {
                    //case "CONN":
                    String pseudo = (String) responses.get(1);
                    String password = (String) responses.get(2);
                    String query = "SELECT id, nom, prenom, email,pseudo, motdepasse FROM utilisateur WHERE pseudo=? AND motdepasse=?";
                    PreparedStatement ps = this.connection.prepareStatement(query);
                    ps.setString(1, pseudo);
                    ps.setString(2, password);
                    ResultSet results = ps.executeQuery();

                    if (results.next()) {
                        System.out.println("Utilisateur et mot de passe correct");
                        toSend.add("OK");
                        toSend.add(results.getString("nom"));
                        toSend.add(results.getString("prenom"));
                        toSend.add(results.getString("email"));
                        toSend.add(results.getString("id"));

                    } else {
                        System.out.println("Utilisateur et mot de passe incorrects");
                    }
                    //break;
                  /*  case "LIST":
                        //toSend.add(genericList(responses.get(1)));
                        break;*/
                } else if(responses.get(0).toString().toUpperCase().equals("INSC")) {
                    System.out.println("INSC");
                    String nom = (String) responses.get(1);
                    String prenom = (String) responses.get(2);
                    String pseudo = (String) responses.get(3);
                    String password = (String) responses.get(4);
                    String email = (String) responses.get(5);
                    String insertUser = "INSERT INTO utilisateur (nom,prenom,pseudo,email,motdepasse) VALUES (?,?,?,?,?)";
                    PreparedStatement ps = this.connection.prepareStatement(insertUser);
                    ps.setString(1, nom);
                    ps.setString(2, prenom);
                    ps.setString(3, pseudo);
                    ps.setString(4,email);
                    ps.setString(5,password);
                    ps.executeUpdate();
                }else if(responses.get(0).toString().toUpperCase().equals("PLAYLOBBY")) {
                    String idUser = (String) responses.get(1);
                    System.out.println("PLAY LOBBY");

                    if(currentnumJoueur%5 ==0) {
                        String query = "SELECT COUNT(id) AS\"nbPartie\"  FROM partie ";
                        PreparedStatement ps = this.connection.prepareStatement(query);
                        ResultSet results = ps.executeQuery();
                        if(results.next()) {
                            currentPartie = results.getInt("nbPartie") +1;
                        }

                        Statement stmt2 = this.connection.createStatement();
                        stmt2.executeUpdate("INSERT INTO partie(id) VALUES ("+ currentPartie  +")") ;
                    }

                    Statement stmt = this.connection.createStatement();
                    stmt.executeUpdate("INSERT INTO joueur(utilisateur,num,partie) VALUES (" + idUser + ","+ ((currentnumJoueur%5)+1) + ","+ currentPartie +")");
                    System.out.println("INSERT INTO joueur(utilisateur,num,partie) VALUES (" + idUser + ","+ ((currentnumJoueur%5)+1) + ","+ currentPartie +")");

                    toSend.add("JOINLOBBY");
                    toSend.add(currentnumJoueur+1);
                    currentnumJoueur++;



                }
                else if(responses.get(0).toString().toUpperCase().equals("WAIT")) {

                    String query = "SELECT COUNT(id) AS\"nbJoueur\"  FROM joueur";
                    PreparedStatement ps = this.connection.prepareStatement(query);
                    ResultSet results = ps.executeQuery();
                    results.next();
                    int nbJoueur = results.getInt("nbJoueur");
                    toSend.add(nbJoueur);
                } else if(responses.get(0).toString().toUpperCase().equals("PLAY")) {
                    String idUser = (String) responses.get(1);
                    System.out.println(responses.get(2));
                    String query = "SELECT *  FROM main WHERE id = " + responses.get(2);
                    PreparedStatement ps = this.connection.prepareStatement(query);
                    ResultSet results = ps.executeQuery();
                    if(results.next()) {
                        for(int i =1; i<=15;i++) {
                            //nomCartes.add(results.getString("carte" + i));
                            Statement stmt2 = this.connection.createStatement();
                            stmt2.executeUpdate("UPDATE joueur SET carte" + i + " = " + results.getString("carte" + i) + " WHERE utilisateur = " + idUser + " AND partie = "+ currentPartie) ;
                        }
                    }

                    String query2 = "SELECT *  FROM joueur WHERE utilisateur = " + idUser + " AND partie = "+ currentPartie;
                    PreparedStatement ps2 = this.connection.prepareStatement(query2);
                    ResultSet results2 = ps2.executeQuery();
                    String numJoueur = "";
                    String idCartes = "";
                    String lienCartes = "";
                    if(results2.next()) {
                        numJoueur = results2.getString("num");
                        for(int i =1; i<=15;i++) {
                            System.out.println("");
                            String query3 = "SELECT *  FROM carte WHERE id = " + results2.getString("carte" + i);
                            PreparedStatement ps3 = this.connection.prepareStatement(query3);

                            ResultSet results3 = ps3.executeQuery();
                            if (results3.next()) {
                                lienCartes += results3.getString("lien") + ";";
                                idCartes += results3.getString("id") + ";";
                            }
                        }
                    }



                    System.out.println("---" + lienCartes + "   ////  " + idCartes);
                    toSend.add(lienCartes);
                    toSend.add(idCartes);
                    toSend.add(numJoueur);

                    // rs.next();

                } else if(responses.get(0).toString().toUpperCase().equals("WAITANSWER")) {
                    String query = "SELECT COUNT(id) AS\"nbJoueur\"  FROM joueur WHERE reponse != 'WAIT' AND partie = "+ currentPartie;
                    PreparedStatement ps = this.connection.prepareStatement(query);
                    ResultSet results = ps.executeQuery();
                    results.next();
                    int nbJoueur = results.getInt("nbJoueur")+1;

                    String query2 = "SELECT num, COUNT(id) AS\"nbJoueur\"  FROM joueur WHERE reponse = 'TAKE' AND partie = "+ currentPartie;
                    PreparedStatement ps2 = this.connection.prepareStatement(query2);
                    ResultSet results2 = ps2.executeQuery();
                    results2.next();
                    int hasTake = results2.getInt("nbJoueur");
                    int numPlayer = results2.getInt("num");

                    toSend.add(nbJoueur);
                    if(hasTake >0) {
                        toSend.add("TAKE");
                        toSend.add(numPlayer);
                    } else {
                        toSend.add("NOTAKE");
                        toSend.add(-1);
                    }



                }
                else if(responses.get(0).toString().toUpperCase().equals("CHIEN")) {
                    String idUser = (String) responses.get(1);

                    Statement stmt2 = this.connection.createStatement();
                    stmt2.executeUpdate("UPDATE joueur SET reponse = 'TAKE' , equipe = 1 WHERE utilisateur = " + idUser + " AND partie = " + currentPartie) ;



                } else if(responses.get(0).toString().toUpperCase().equals("REFUSE")) {
                    String idUser = (String) responses.get(1);

                    Statement stmt = this.connection.createStatement();
                    stmt.executeUpdate("UPDATE joueur SET reponse = 'REFUSE' , equipe = 2 WHERE utilisateur = " + idUser + " AND partie = " + currentPartie);
                }  else if(responses.get(0).toString().toUpperCase().equals("ROIS")) {
                    String query = "SELECT *  FROM carte WHERE valeur = 'R'";
                    PreparedStatement ps = this.connection.prepareStatement(query);
                    ResultSet results = ps.executeQuery();
                    String idCartes = "";
                    String lienCartes = "";
                    while(results.next()) {
                        idCartes += results.getString("id") + ";";
                        lienCartes += results.getString("lien") + ";";
                    }
                    toSend.add(idCartes);
                    toSend.add(lienCartes);
                } else if(responses.get(0).toString().toUpperCase().equals("CALL")) {
                    System.out.println("Rep");
                    int idCarte = (int) responses.get(1);
                    System.out.println(idCarte);
                    String query2 = "SELECT * FROM joueur WHERE partie = " + currentPartie;
                    for(int i =1; i< 15; i++) {
                        if(i==1) {
                            query2 += " AND carte" + i + " = " + idCarte;
                        }  else {
                            query2+= " OR carte" + i + " = " + idCarte + " ";
                        }
                    }
                    int idUserAllied = -1;
                    PreparedStatement ps2 = this.connection.prepareStatement(query2);
                    ResultSet results2 = ps2.executeQuery();
                    if(results2.next()) {
                        idUserAllied = results2.getInt("utilisateur");
                    }

                    Statement stmt1 = this.connection.createStatement();
                    stmt1.executeUpdate("UPDATE joueur SET equipe = 1 WHERE utilisateur = " + idUserAllied + " AND partie = " + currentPartie) ;

                    Statement stmt2 = this.connection.createStatement();
                    stmt2.executeUpdate("UPDATE joueur SET equipe = 2 WHERE equipe != 1 AND partie = " + currentPartie) ;

                    String query3 = "SELECT * FROM chien";
                    PreparedStatement ps3 = this.connection.prepareStatement(query3);
                    ResultSet results3 = ps3.executeQuery();
                    ArrayList<Integer> idCartes = new ArrayList<>();
                    ArrayList<String> lienCartes = new ArrayList<>();
                    if(results3.next()) {
                        for(int i = 1; i <= 3; i++) {
                            String query4 = "SELECT * FROM carte WHERE id = " + results3.getInt("carte" + i) ;
                            PreparedStatement ps4 = this.connection.prepareStatement(query4);
                            ResultSet results4 = ps4.executeQuery();
                            if(results4.next()) {
                                idCartes.add(results4.getInt("id"));
                                lienCartes.add(results4.getString("lien"));
                            }
                        }
                    }
                    toSend.add(idCartes);
                    toSend.add(lienCartes);
                    callDone = true;


                } else if(responses.get(0).toString().toUpperCase().equals("WAITCALL")) {

                    String query = "SELECT * FROM chien";
                    PreparedStatement ps = this.connection.prepareStatement(query);
                    ResultSet results = ps.executeQuery();
                    ArrayList<Integer> idCartes = new ArrayList<>();
                    ArrayList<String> lienCartes = new ArrayList<>();
                    if(results.next()) {
                        for(int i = 1; i <= 3; i++) {
                            String query1 = "SELECT * FROM carte WHERE id = " + results.getInt("carte" + i) ;
                            PreparedStatement ps1 = this.connection.prepareStatement(query1);
                            ResultSet results1 = ps1.executeQuery();
                            if(results1.next()) {
                                idCartes.add(results1.getInt("id"));
                                lienCartes.add(results1.getString("lien"));
                            }
                        }
                    }
                        toSend.add(callDone);
                        toSend.add(idCartes);
                        toSend.add(lienCartes);


                }else {
                        toSend.add("UNKNOWN_COMMAND");
                        break;
                }

                objectOutputStream.writeObject(toSend);
                objectOutputStream.flush();
                objectOutputStream.close();

/*                if (closeConnexion) {
                    System.err.println("Fermeture de la connexion");
                    reader = null;
                    sock.close();
                    break;
                }*/

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
