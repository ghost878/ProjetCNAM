package server;

import java.util.ArrayList;

/**
 * Liste des connexions actives
 */
public class ActiveConnections {
    /**
     * Liste des connexions actives
     */
    private ArrayList<ServerConnection> connectionsList = new ArrayList<>();

    /**
     * Ajout d'une nouvelle connexion
     */
    synchronized void add(ServerConnection connection) {
        connectionsList.add(connection);
    }

    /**
     * Suppression d'une connexion
     */
    synchronized void remove(ServerConnection connection) {
        connectionsList.remove(connection);
    }
}
