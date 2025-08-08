package root.supplymanagement;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String dbName = "Kimsa";
        String dbUser = "kimsa";
        String dbPassword = "kimsa@101";
        String dbHost = "localhost";
        String dbPort = "3306";

//        // Online database credentials
//        String dbName = "SupplyManagement";
//        String dbUser = "avnadmin";
//        String dbPassword = "SupplyManagement1";
//        String dbHost = "mysql-39f69fc1-ngangabrianproject1.f.aivencloud.com";
//        String dbPort = "28913";

//        //Azure database credentials
//        String dbName = "supplymanagement";
//        String dbUser = "kimsa";
//        String dbPassword = "SupplyManagement1";
//        String dbHost = "kimsa.mysql.database.azure.com";
//        String dbPort = "3306";

//        String url="jdbc:mysql://kimsa.mysql.database.azure.com:3306/{your_database}?useSSL=true";
//        myDbConn=DriverManager.getConnection(url, "kimsa", "{your_password}");

        // Use SSL if required by Aiven (they often require it)
        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?sslMode=REQUIRED";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return databaseLink;
    }
}
