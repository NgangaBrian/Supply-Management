package root.supplymanagement;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.Connection;

public class HelloApplication extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    private static void waitForMySQLConnection() {
        boolean dbReady = false;
        int retries = 0;

        while (!dbReady && retries < 10) {
            try {
                // Reuse your existing DBConnection class
                DBConnection dbConnection = new DBConnection();
                Connection conn = dbConnection.getConnection();

                if (conn != null && !conn.isClosed()) {
                    dbReady = true;
                    System.out.println("Connected to MySQL successfully.");
                    conn.close();
                }
            } catch (Exception e) {
                retries++;
                System.out.println("Waiting for MySQL to be ready... attempt " + retries);
                try {
                    Thread.sleep(1000); // wait 1 second before retrying
                } catch (InterruptedException ignored) {}
            }
        }

        if (!dbReady) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to connect to MySQL after multiple attempts.");
            alert.showAndWait();
            System.exit(1); // Optionally stop the app
        }
    }


    private static void startMySQLServer() {
        try {
            String installDire = new File(HelloApplication.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent();
//
//            // String mysqlPath = installDir + "\\mysql\\bin\\mysqld.exe";
//
//            //String installDir = System.getProperty("user.dir");
//
//            //String mysqlPath = installDir + "\\mysql\\bin\\mysqld.exe";
//
//            String mysqlPath = ".\\mysql\\bin\\mysqld.exe";

            // Get the working directory (project root when running from IntelliJ)
            String workingDir = System.getProperty("user.dir");

            // Construct full path to mysqld.exe
            String mysqlPath = workingDir + File.separator + "mysql" + File.separator + "bin" + File.separator + "mysqld.exe";

            String path = "C:\\Program Files\\SupplyManagement\\app\\mysql\\bin\\mysqld.exe";

            ProcessBuilder processBuilder = new ProcessBuilder(path, "--console");
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(new File(installDire + "\\mysql_startup_log.txt")); // Log will be saved here
            processBuilder.start();
            System.out.println("MySQL Server started successfully.");
        } catch (IOException | URISyntaxException e) {
            logErrorToFile(e); // Log error to a file
            e.printStackTrace();
            System.err.println("Failed to start MySQL Server.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to start MySQL Server.");
            alert.showAndWait();
        }
    }

    public static void logErrorToFile(Exception e) {
        try (FileWriter fw = new FileWriter("error_log.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println("========== ERROR ==========");
            out.println("Time: " + java.time.LocalDateTime.now());
            out.println("Message: " + e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) {
                out.println("\tat " + ste);
            }
            out.println(); // blank line between logs
        } catch (IOException ioException) {
            ioException.printStackTrace(); // If logging fails
        }
    }

    // Firebase initialization method
//    public static void initializeFirebase() {
//        try {
//            // Path to your service account JSON file
//            FileInputStream serviceAccount = new FileInputStream("src/serviceAccountKey.json");
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setStorageBucket("theshop-29b33.appspot.com")
//                    .build();
//
//            // Initialize Firebase if it is not already initialized
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        //startMySQLServer();
        //waitForMySQLConnection();   // Wait until it is ready
        launch();
    }
}