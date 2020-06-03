package sample;

import client.ClientConnexion;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccueilController {
    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane anchorUser;
    @FXML
    private AnchorPane anchorPlay;
    @FXML
    private Label labelBonjour;
    @FXML
    private VBox boxInfo;
    @FXML
    private Button playButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button disconnectButton;
    public static String numJoueur;

    @FXML
    public void disconnect() {
        ConnexionController.stageAccueil.hide();
        ConnexionController.primaryStage.show();
    }

    @FXML
    public void play() throws IOException, InterruptedException {


        ArrayList<Object> connects = new ArrayList<>();
        ArrayList<Object> waits = new ArrayList<>();
        connects.add("PLAYLOBBY");
        connects.add(ConnexionController.idUser);
        System.out.println("Méthode plays " + connects.get(1));
        ClientConnexion client = new ClientConnexion("192.168.1.77",3333,connects);
        ArrayList<Object> datas = client.run();
        System.out.println("Reponse :" + datas.get(0));
        int nbJoueur = -1;
        while(nbJoueur != 0) {
            waits.add("WAIT");
            ClientConnexion client1 = new ClientConnexion("192.168.1.77",3333,waits);
            ArrayList<Object> datas1 = client1.run();
            nbJoueur = (int)datas1.get(0)%5;
            if(nbJoueur == 0) {
                //JOptionPane.showMessageDialog(null,"Pret à jouer !");
            } else {
                JOptionPane.showMessageDialog(null,"En attente de " + (5 - ((int)datas1.get(0)%5)) + " joueurs");
            }
        }

        ArrayList<Object> plays = new ArrayList<>();
        plays.add("PLAY");
        plays.add(ConnexionController.idUser);
        plays.add(datas.get(1));
        ClientConnexion client2 = new ClientConnexion("192.168.1.77",3333,plays);
        ArrayList<Object> datas2 = client2.run();
        String lienCartes[] = datas2.get(0).toString().split(";");
        String idCartes[] = datas2.get(1).toString().split(";");
        numJoueur = (String) datas2.get(2);


        ConnexionController.stageAccueil.hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("partie.fxml"));
        Parent root = loader.load();
        Stage gameStage = new Stage();
        Scene scene = new Scene(root, 700, 400);
        scene.getStylesheets().add(getClass().getResource("style.css").toString());
        gameStage.setTitle("Tarot en ligne");
        gameStage.setScene(scene);
        gameStage.show();


        HBox main = (HBox) root.lookup("#main");
        main.setSpacing(10);
        for(int i = 0; i < lienCartes.length ; i++) {
            File f = new File("L:/CNAM/PROJET/ProjetCNAM/src/main/resources/sample/img/" + lienCartes[i]);
            if(f.exists()) {
                main.getChildren().add(new ImageView(new Image("/sample/img/" + lienCartes[i])));
            }
        }
        //Thread.currentThread().sleep(1000);
        //int input = JOptionPane.showConfirmDialog(null, "Do you like bacon?");
        // 0=yes, 1=no, 2=cancel
        //System.out.println(input);

    }
}
