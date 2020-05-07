package sample;

import client.ClientConnexion;
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
import java.sql.*;

public class ConnexionController extends Application {

    private Connection connection;

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

    public void onLogin(ActionEvent event) throws SQLException, ClassNotFoundException {
       String login = this.login.getText();
       String password = this.pass.getText();
       if (login != null && password != null) {
           MySQLConnection db = new MySQLConnection("jdbc:mysql://localhost:3306/tarot_project","root","");
           this.connection = db.getConnection();
           String query = "SELECT pseudo, motdepasse FROM utilisateur WHERE pseudo=? AND motdepasse=?";
           PreparedStatement ps = this.connection.prepareStatement(query);
           ps.setString(1,login);
           ps.setString(2,password);
           ResultSet results = ps.executeQuery();

           if (results.next()) {
               System.out.println("Utilisateur et mot de passe correct");
               JOptionPane.showMessageDialog(null,"L'utilisateur et le mot de passe sont correct");
               //lancement de la session - connexion au serveur
               ClientConnexion client = new ClientConnexion("192.168.1.40",3333,"pseudo");
           } else {
               System.out.println("Utilisateur ou mot de passe incorrect");
               JOptionPane.showMessageDialog(null,"Utilisateur ou mot de passe incorrects");
           }
       }
    }

    public void onSignIn(ActionEvent event) {
        System.out.println("Inscription function");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connexion.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("style.css").toString());
        primaryStage.setTitle("Connexion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}