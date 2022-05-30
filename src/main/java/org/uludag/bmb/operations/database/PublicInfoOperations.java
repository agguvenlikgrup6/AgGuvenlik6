package org.uludag.bmb.operations.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.uludag.bmb.beans.database.SharedFile;
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

    public SharedFile getSharedFileByEncryptedName(String encryptedName) {
        try {
            String query = "SELECT * FROM " + this.databaseController.TABLES.sharedFilesKeyTable
                    + " WHERE encryptedName=?";
            PreparedStatement statement = this.databaseController.getAzureCon().prepareStatement(query);
            statement.setString(1, encryptedName);
            ResultSet rst = statement.executeQuery();
            List<SharedFile> sharedFiles = new ArrayList<SharedFile>();
            while (rst.next()) {
                sharedFiles.add(new SharedFile(rst.getString("email"), rst.getString("encryptedName"),
                        rst.getString("fileKeyPart1"), rst.getString("fileKeyPart2")));
            }
            return sharedFiles.get(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
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

    public String getUserPublicKey(String email) {
        try {
            String query = "SELECT publicKey FROM " + this.databaseController.TABLES.publicInfo + " WHERE email=?";
            PreparedStatement statement = this.databaseController.getAzureCon().prepareStatement(query);
            statement.setString(1, email);
            String publicKey = "";
            ResultSet rst = statement.executeQuery();
            while (rst.next()) {
                publicKey = rst.getString("publicKey");
            }
            return publicKey;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getPrivateKey() {
        String privateKeyQuery = "SELECT privateKey FROM " + this.databaseController.TABLES.privateKey;
        String privateKey = "";
        try {
            PreparedStatement statement = this.databaseController.getConn().prepareStatement(privateKeyQuery);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                privateKey = rs.getString("privateKey");
            }
            return privateKey;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertSharedFileKey(String recieverEmail, String fileKeyPart1, String fileKeyPart2,
            String encryptedFileName) {
        String query = "INSERT INTO " + this.databaseController.TABLES.sharedFilesKeyTable
                + "(email, fileKeyPart1, fileKeyPart2, encryptedName) VALUES(?,?,?,?)";
        try {
            PreparedStatement statement = this.databaseController.getAzureCon().prepareStatement(query);
            statement.setString(1, recieverEmail);
            statement.setString(2, fileKeyPart1);
            statement.setString(3, fileKeyPart2);
            statement.setString(4, encryptedFileName);

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
