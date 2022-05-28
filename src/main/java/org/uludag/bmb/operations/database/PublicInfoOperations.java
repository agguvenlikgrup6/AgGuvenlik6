package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.operations.dropbox.Client;

public class PublicInfoOperations {
    private DatabaseController databaseController;

    public PublicInfoOperations() {
        this.databaseController = new DatabaseController();
    }

    public void insertShareKeys(String publicKey, String privateKey) {
        String privateKeyQuery = "INSERT INTO " + this.databaseController.TABLES.privateKey + " (privateKey) values(?)";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(privateKeyQuery);
            statement.setString(1, privateKey);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String publicKeyQuery = "BEGIN IF NOT EXISTS (SELECT * FROM " + this.databaseController.TABLES.publicInfo
                + " WHERE email=?)"
                + "BEGIN INSERT INTO " + this.databaseController.TABLES.publicInfo
                + "(email, publicKey) VALUES(?,?) END "
                + "END";
        try {
            PreparedStatement statement = this.databaseController.getAzureCon().prepareStatement(publicKeyQuery);
            statement.setString(1, Client.client.users().getCurrentAccount().getEmail());
            statement.setString(2, Client.client.users().getCurrentAccount().getEmail());
            statement.setString(3, publicKey);

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getUsersList() {
        try {
            Statement statement = this.databaseController.getAzureCon().createStatement();
            String query = "SELECT email FROM " + this.databaseController.TABLES.publicInfo;
            ResultSet rst = statement.executeQuery(query);
            List<String> emailList = new ArrayList<String>();
            while (rst.next()) {
                emailList.add(rst.getString("email"));
            }
            return emailList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
