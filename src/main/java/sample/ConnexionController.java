package sample;

import client.ClientConnexion;
import javafx.scene.layout.VBox;
import server.MySQLConnection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class ConnexionController extends Application {

    public static MySQLConnection db;
    public static String pseudo;

    static {
        try {
            db = new MySQLConnection("jdbc:mysql://localhost:3306/tarot_project","root","");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection connection;
    //public Stage accueilStage;
    public static Stage primaryStage = new Stage();
    public static Stage stageAccueil = new Stage();
    @FXML
    private ImageView background;
    @FXML
    private ImageView icon;
    @FXML
    private Label username;
    @FXML
    private Label password;
    @FXML
    private TextField login;
    @FXML
    private TextField user;
    @FXML
    private PasswordField pass;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Button loginButton;
    @FXML
    private StackPane stack;
    @FXML
    private Pane pane;
    @FXML
    private AnchorPane anchorPane;

    public static void main(String[] args) {
        launch(args);
    }


    public void onLogin(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
       pseudo = this.login.getText();
       String password = this.pass.getText();
        MySQLConnection db = new MySQLConnection("jdbc:mysql://localhost:3306/tarot_project","root","");
        this.connection = db.getConnection();
/*
       if (login != null && password != null) {

           String query = "SELECT pseudo, motdepasse FROM utilisateur WHERE pseudo=? AND motdepasse=?";
           PreparedStatement ps = this.connection.prepareStatement(query);
           ps.setString(1,pseudo);
           ps.setString(2,password);
           ResultSet results = ps.executeQuery();

           if (results.next()) {
               System.out.println("Utilisateur et mot de passe correct");
               //JOptionPane.showMessageDialog(null,"L'utilisateur et le mot de passe sont correct");
               FXMLLoader loader = new FXMLLoader(getClass().getResource("accueil.fxml"));
               Parent root = loader.load();
               Scene scene = new Scene(root, 700, 400);
               scene.getStylesheets().add(getClass().getResource("style.css").toString());
               stageAccueil.setTitle("Accueil");
               stageAccueil.setScene(scene);
               stageAccueil.show();
               primaryStage.hide();

               Statement stmt = this.connection.createStatement();
               */
/**
                * Exemple de requête avec BD
                *//*

               ResultSet rs = stmt.executeQuery("SELECT nom, prenom, pseudo, email  FROM utilisateur WHERE pseudo LIKE \""+ pseudo +"\"");
               //ResultSet rs = stmt.executeQuery("SELECT *  FROM utilisateur");
               //while(rs.next()) {
               rs.next();
                   String prenom = rs.getString("prenom");
                   String nom = rs.getString("nom");
                String email = rs.getString("email");

               //}
              // rs.next();
               //String name = rs.getString(1);
               System.out.println(nom);
               VBox infoBox = (VBox) root.lookup("#boxInfo");
               infoBox.getChildren().add(new Label("Prenom : " + prenom));
               infoBox.getChildren().add(new Label("Nom : " + nom));
               infoBox.getChildren().add(new Label("Email : " + email));
               Label lblData = (Label) root.lookup("#labelBonjour");
               lblData.setText("Bonjour " + pseudo);

               AccueilController controller = loader.getController();
               //controller.init();


*/


               //lancement de la session - connexion au serveur
               //System.out.println(pseudo);
            ArrayList<String> connects = new ArrayList<>();
            connects.add("CONN");
            connects.add(pseudo);
            connects.add(password);
            ClientConnexion client = new ClientConnexion("192.168.1.77",3333,connects);
            ArrayList<String> responses = client.run();
            if(responses.get(0).equals("OK")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("accueil.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 700, 400);
                scene.getStylesheets().add(getClass().getResource("style.css").toString());
                stageAccueil.setTitle("Accueil");
                stageAccueil.setScene(scene);
                stageAccueil.show();
                primaryStage.hide();


                String nom = responses.get(1);
                String prenom = responses.get(2);
                String email = responses.get(3);

               //}
              // rs.next();
               //String name = rs.getString(1);
               System.out.println(nom);
               VBox infoBox = (VBox) root.lookup("#boxInfo");
               infoBox.getChildren().add(new Label("Prenom : " + prenom));
               infoBox.getChildren().add(new Label("Nom : " + nom));
               infoBox.getChildren().add(new Label("Email : " + email));
               Label lblData = (Label) root.lookup("#labelBonjour");
               lblData.setText("Bonjour " + pseudo);

           } else {
               System.out.println("Utilisateur ou mot de passe incorrect");
               JOptionPane.showMessageDialog(null,"Utilisateur ou mot de passe incorrects");
           }
       }


    public void onSignIn(ActionEvent event) {
        System.out.println("Inscription function");
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connexion.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 400);
        scene.getStylesheets().add(getClass().getResource("style.css").toString());
        primaryStage.setTitle("Connexion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}