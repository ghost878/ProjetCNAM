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
    public static String idUser;
    @FXML
    private ImageView background;
    @FXML
    private ImageView icon;
    // formulaire d'inscription
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField email;
    @FXML
    private CheckBox checkBox;
    // formulaire de connexion
    @FXML
    private TextField login;
    @FXML
    private PasswordField pass;
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

            ArrayList<Object> connects = new ArrayList<>();
            connects.add("CONN");
            connects.add(pseudo);
            connects.add(password);
            ClientConnexion client = new ClientConnexion("79.94.239.225",3333,connects);
            ArrayList<Object> responses = client.run();
            if(responses.get(0).equals("OK")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("accueil.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 700, 400);
                scene.getStylesheets().add(getClass().getResource("style.css").toString());
                stageAccueil.setTitle("Accueil");
                stageAccueil.setScene(scene);
                stageAccueil.show();
                primaryStage.hide();


                String nom = (String) responses.get(1);
                String prenom = (String) responses.get(2);
                String email = (String) responses.get(3);
                idUser = (String) responses.get(4);

               //}
              // rs.next();
               //String name = rs.getString(1);
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
        ArrayList<Object> datas = new ArrayList<>();
        String nom = this.nom.getText();
        String prenom = this.prenom.getText();
        String username = this.username.getText();
        String password = this.password.getText();
        String email = this.email.getText();
        datas.add("INSC");
        datas.add(nom);
        datas.add(prenom);
        datas.add(username);
        datas.add(password);
        datas.add(email);
        ClientConnexion client = new ClientConnexion("192.168.1.77",3333,datas);
        ArrayList<Object> responses = client.run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connexion.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toString());
        primaryStage.setTitle("Connexion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}