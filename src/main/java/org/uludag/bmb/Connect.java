package org.uludag.bmb;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class Connect {
    public static void main(String[] args) {
        // connectToDatabase("DBOX");
        // createNewDatabase("gggg");
        // createNewTable("DBOX", "records");

        insert("Aryan", 30000);
        insert("Robert", 20000);
        insert("Jerry", 15000);

    }

    private static String getConnectionUrl(String a){
        return "";
    }

    public static Connection connectToDatabase(String dbName) {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(getConnectionUrl(dbName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("başarılı");
        return conn;
    }


    public static void createNewTable(String dbName, String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(\n";
        sql += "id integer PRIMARY KEY,\n";
        sql += "name text NOT NULL,\n";
        sql += "capacity real\n";
        sql += ");";

        try {
            Connection conn = DriverManager.getConnection(getConnectionUrl(dbName));
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("başarılı tablo");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(String name, double capacity) {
        String sql = "INSERT INTO records(name, capacity) VALUES (?,?)";

        try {
            Connection conn = connectToDatabase("DBOX");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
            System.out.println("insert success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
