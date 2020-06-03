package sample;

import client.ClientConnexion;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import static java.awt.event.MouseEvent.*;

public class PartieController {

    @FXML
    private HBox main;
    @FXML
    private Button play;
    @FXML
    private Button take;
    @FXML
    private Button refuse;
    @FXML
    private HBox boxRois;
    @FXML
    private ImageView carteCenter;
    @FXML
    private ImageView dog;

    public void play() throws InterruptedException {
        ArrayList<Object> waitsAnswer = new ArrayList<>();
        int currentNumJoueur = -1;
        String hasTake = "";
        int numPlayerTake = -1;
        while(currentNumJoueur != Integer.parseInt(AccueilController.numJoueur) && !hasTake.equals("TAKE")) {
            waitsAnswer.add("WAITANSWER");
            ClientConnexion client3 = new ClientConnexion("192.168.1.77",3333,waitsAnswer);
            ArrayList<Object> datas3 = client3.run();
            currentNumJoueur = (int) datas3.get(0);
            hasTake = (String) datas3.get(1);
            numPlayerTake = (int) datas3.get(2);
            Thread.currentThread().sleep(1000);
            System.out.println(currentNumJoueur);
            System.out.println(AccueilController.numJoueur);

            if(currentNumJoueur == Integer.parseInt(AccueilController.numJoueur) && !hasTake.equals("TAKE")) {
                JOptionPane.showMessageDialog(null,"A votre tour !");
                take.setVisible(true);
                refuse.setVisible(true);
            }
            else if(hasTake.equals("TAKE")) {
                JOptionPane.showMessageDialog(null,"Le chien a été pris");
                take(numPlayerTake);
            } else {
                JOptionPane.showMessageDialog(null,"En attente");
            }


        }

            play.setVisible(false);

    }

    public void playWithDog() throws InterruptedException {
        System.out.println("PRIS !");
        ArrayList<Object> chiens = new ArrayList<>();
        chiens.add("CHIEN");
        chiens.add(ConnexionController.idUser);

        ClientConnexion client2 = new ClientConnexion("192.168.1.77",3333,chiens);
        ArrayList<Object> datas2 = client2.run();
        refuse.setVisible(false);
        take.setVisible(false);
        play();
    }

    public void playWithoutDog() throws InterruptedException {
        ArrayList<Object> refuses = new ArrayList<>();
        refuses.add("REFUSE");
        refuses.add(ConnexionController.idUser);

        ClientConnexion client2 = new ClientConnexion("192.168.1.77",3333,refuses);
        ArrayList<Object> datas2 = client2.run();
        refuse.setVisible(false);
        take.setVisible(false);
        play();
    }

    public void take(int numPlayerTake) {
        if(numPlayerTake == Integer.parseInt(AccueilController.numJoueur)){
            carteCenter.setVisible(false);
            ArrayList<Object> rois = new ArrayList<>();
            rois.add("ROIS");
            rois.add(ConnexionController.idUser);
            ClientConnexion client2 = new ClientConnexion("192.168.1.77",3333,rois);
            ArrayList<Object> datas = client2.run();
            String idCartes[] = datas.get(0).toString().split(";");
            String lienCartes[] = datas.get(1).toString().split(";");
            for(int i = 0; i < idCartes.length ; i++) {
                File f = new File("L:/CNAM/PROJET/ProjetCNAM/src/main/resources/sample/img/" + lienCartes[i]);
                if(f.exists()) {
                    ImageView imageRoi = new ImageView(new Image("/sample/img/" + lienCartes[i]));
                    int finalI = i;
                    imageRoi. addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                        boxRois.getChildren().clear();
                            ArrayList<Object> calls = new ArrayList<>();
                            calls.add("CALL");
                            calls.add(Integer.parseInt(idCartes[finalI]));
                            calls.add(ConnexionController.idUser);
                            ClientConnexion client3 = new ClientConnexion("192.168.1.77",3333,calls);
                        ArrayList<Object> datas2 = client3.run();
                        ArrayList<Integer> idCartesChien = (ArrayList) datas2.get(0);
                        ArrayList<String> lienCartesChien = (ArrayList) datas2.get(1);
                        for(int j = 0; j < idCartesChien.size() ; j++) {
                            ImageView imageChien = new ImageView(new Image("/sample/img/" + lienCartesChien.get(j)));
                            boxRois.getChildren().add(imageChien);
                            main.getChildren().add(imageChien);
                        }
                    });
                    boxRois.getChildren().add(imageRoi);
                }
            }
        } else {
            ArrayList<Object> waitcalls = new ArrayList<>();
            waitcalls.add("WAITCALL");
            waitcalls.add(ConnexionController.idUser);
            boolean callsDone = false;
            while(callsDone == false) {
                ClientConnexion client2 = new ClientConnexion("192.168.1.77",3333,waitcalls);
                ArrayList<Object> datas2 = client2.run();
                callsDone = (boolean) datas2.get(0);
                if(callsDone == true) {
                    //boxRois.getChildren().clear();
                    ArrayList<Integer> idCartes = (ArrayList) datas2.get(1);
                    ArrayList<String> lienCartes = (ArrayList) datas2.get(2);
                    for(int i = 0; i < idCartes.size() ; i++) {
                        ImageView imageChien = new ImageView(new Image("/sample/img/" + lienCartes.get(i)));
                        boxRois.getChildren().add(imageChien);
                    }
                    carteCenter.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null,"Appel du Roi en cours !");
                }

            }
        }
    }

}
