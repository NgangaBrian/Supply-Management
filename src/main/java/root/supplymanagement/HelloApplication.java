package root.supplymanagement;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.IOException;

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

    private static void startMySQLServer() {
        try {
            String mysqlPath = ".\\mysql\\bin\\mysqld";  // Change this to your MySQL path
            ProcessBuilder processBuilder = new ProcessBuilder(mysqlPath, "--console");
            processBuilder.start();
            System.out.println("MySQL Server started successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to start MySQL Server.");
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
        startMySQLServer();
        launch();
    }
}