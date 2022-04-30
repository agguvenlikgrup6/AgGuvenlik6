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

    public static String getConnectionUrl(String dbName) {
        String url = "jdbc:sqlite:" + Paths.get("").toAbsolutePath().toString();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.indexOf("mac") >= 0) {
            url += "/Data/";
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            url += "/Data/";
        } else {
            url += "\\Data\\";
        }

        url += dbName + ".db";

        return url;
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

    public static void createNewDatabase(String dbName) {
        try {
            Connection conn = DriverManager.getConnection(getConnectionUrl(dbName));
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
