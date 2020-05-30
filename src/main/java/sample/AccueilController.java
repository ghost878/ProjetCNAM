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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
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

    @FXML
    public void disconnect() {
        ConnexionController.stageAccueil.hide();
        ConnexionController.primaryStage.show();
    }

    @FXML
    public void play() throws IOException {


        ArrayList<String> connects = new ArrayList<>();
        connects.add("PLAYLOBBY");
        connects.add(ConnexionController.idUser);
        System.out.println("Méthode plays " + connects.get(1));
        ClientConnexion client = new ClientConnexion("192.168.1.77",3333,connects);
        ArrayList<String> datas = client.run();
        System.out.println("Reponse :" + datas.get(0));

        while(!datas.get(1).equals("5")) {
            System.out.println(datas.get(1));
            JOptionPane.showMessageDialog(null,"En attente de " + (5 - (Integer.parseInt(datas.get(1))%5)) + " joueurs");
        }


        ConnexionController.stageAccueil.hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("partie.fxml"));
        Parent root = loader.load();
        Stage gameStage = new Stage();
        Scene scene = new Scene(root, 700, 400);
        scene.getStylesheets().add(getClass().getResource("style.css").toString());
        gameStage.setTitle("Tarot en ligne");
        gameStage.setScene(scene);
        gameStage.show();
    }



}
