package root.supplymanagement;


import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.mysql.cj.conf.PropertyKey.logger;

public class HomeController implements Initializable {

    @FXML
    private Pane overdueInvoicesPane, recordOrderPane, recordPaymentPane, addSupplierPane,
            viewOrderPane, viewPaymentsPane, viewSuppliersPane, logOutPane, settingsContainer;
    @FXML
    private ImageView logoutImage, viewSupplierImage, viewPaymentsImage, viewOrdersImage, overdueInvoiceImage,
            addSupplierImage, recordPaymentImage, recordOrderImage, settingsImage, closeBtnImage, maximizeBtnImage,
            minimizeBtnImage, logoImageView;
    @FXML
    private Label paymentsMadeLB, suppliersLB, overdueInvoicesLB, outstandingBalanceLB, nameOfUser;

    public User loggedInUser;

    @FXML
    private StackPane rootPane;

    @FXML
    private Group scalingGroup;

    private final double BASE_WIDTH = 780;
    private final double BASE_HEIGHT = 550;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
        getNoOfSuppliers();
        getNoOfOverdueInvoices();
        getTotalAmountPaid();
        getTotalBalance();

        // Apply fade-in and slide-in animations
        applyFadeInAnimation(overdueInvoicesPane);
        applyFadeInAnimation(recordOrderPane);
        applyFadeInAnimation(recordPaymentPane);
        applyFadeInAnimation(addSupplierPane);
        applyFadeInAnimation(viewOrderPane);
        applyFadeInAnimation(viewPaymentsPane);
        applyFadeInAnimation(viewSuppliersPane);
        applyFadeInAnimation(logOutPane);

        // Bind width and height to rootPane
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> updateScale());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> updateScale());
    }

    @FXML
    private void handleRecordOrderClick(MouseEvent event) {
        // Add the CSS class
        recordOrderImage.getStyleClass().add("blink");

        // Remove the class after animation (0.5s delay)
        new Thread(() -> {
            try {
                Thread.sleep(500);
                recordOrderImage.getStyleClass().remove("blink");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateScale() {
        double scaleX = rootPane.getWidth() / BASE_WIDTH;
        double scaleY = rootPane.getHeight() / BASE_HEIGHT;
        double scale = Math.min(scaleX, scaleY); // uniform scaling to avoid distortion
        scalingGroup.setScaleX(scale);
        scalingGroup.setScaleY(scale);
    }

    public void setUserData(User user) {
        this.loggedInUser = user;
        nameOfUser.setText(user.getFirstname() + " " + user.getLastname());
    }

    public void getNoOfSuppliers(){
        String querry = "select count(*) from suppliers";

        try(Connection conn = new DBConnection().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(querry);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            if(resultSet.next()){
                int noOfSuppliers = resultSet.getInt(1);
                suppliersLB.setText(String.valueOf(noOfSuppliers));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void getTotalAmountPaid() {
        String query = "SELECT SUM(paidAmount) FROM payments";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double paidAmount = rs.getDouble(1);
                paymentsMadeLB.setText(String.valueOf(paidAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTotalBalance() {
        String query = "SELECT SUM(balance) FROM orders WHERE balance > 0";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double totalBalance = rs.getDouble(1);
                outstandingBalanceLB.setText(String.valueOf(totalBalance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getNoOfOverdueInvoices(){
        String query = "select count(*) from orders where dueDate < CURDATE() AND balance > 0";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int noOfOverdueInvoices = rs.getInt(1);
                overdueInvoicesLB.setText(String.valueOf(noOfOverdueInvoices));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void openSettingsPane(javafx.scene.input.MouseEvent mouseEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
            Parent root = fxmlLoader.load();

            SettingsController settingsController = fxmlLoader.getController();
            settingsController.setUserData(loggedInUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 654, 400));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openAddSuppliersPane(javafx.scene.input.MouseEvent mouseEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-suppliers.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 450));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openViewOrdersPane(javafx.scene.input.MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view-orders.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 567));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openInvoicePane(javafx.scene.input.MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("invoices.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 870, 607));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openRecordOrdersPane(javafx.scene.input.MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("record-order.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 816, 628));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openViewSuppliersPane(javafx.scene.input.MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view-suppliers.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 567));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openRecordPaymentsPane(javafx.scene.input.MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("record-payment.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 816, 619));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openViewPaymentsPane(javafx.scene.input.MouseEvent mouseEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view-payments.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1000, 607));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLogoutClick(javafx.scene.input.MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log out?");
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            Stage stage = (Stage) logOutPane.getScene().getWindow();
            stage.close();
        }
    }

    public void handleCloseBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) closeBtnImage.getScene().getWindow();
        stage.close();
    }

    public void handleMinimizeBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                ((Stage) window).setIconified(true); // Minimize each stage
            }
        }
    }

    public void handleMaximizeBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) maximizeBtnImage.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    private void applyFadeInAnimation(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), node);
        fadeTransition.setFromValue(0);  // Start invisible
        fadeTransition.setToValue(1);    // End visible
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);  // Apply easing
        fadeTransition.play();
    }

    private void applyTranslateInAnimation(Node node) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(3), node);
        translateTransition.setFromX(-500);  // Start off-screen to the left
        translateTransition.setToX(0);      // Move to original position
        translateTransition.setInterpolator(Interpolator.EASE_BOTH);  // Apply easing
        translateTransition.play();
    }

    public void loadImages(){
        Image brandingImage = new Image(getClass().getResource("/Images/kimsalogo.png").toExternalForm());
        logoImageView.setImage(brandingImage);

        Image orderImage = new Image(getClass().getResource("/Images/recordOrder.png").toExternalForm());
        recordOrderImage.setImage(orderImage);

        Image paymentImage = new Image(getClass().getResource("/Images/recordPayment.png").toExternalForm());
        recordPaymentImage.setImage(paymentImage);

        Image supplierImage = new Image(getClass().getResource("/Images/addSupplier.png").toExternalForm());
        addSupplierImage.setImage(supplierImage);

        Image invoiceImage = new Image(getClass().getResource("/Images/overdueInvoices.png").toExternalForm());
        overdueInvoiceImage.setImage(invoiceImage);

        Image ordersImage = new Image(getClass().getResource("/Images/viewOrders.png").toExternalForm());
        viewOrdersImage.setImage(ordersImage);

        Image viewPaymentImage = new Image(getClass().getResource("/Images/viewPayments.png").toExternalForm());
        viewPaymentsImage.setImage(viewPaymentImage);

        Image viewSuppliersImage = new Image(getClass().getResource("/Images/viewSuppliers.png").toExternalForm());
        viewSupplierImage.setImage(viewSuppliersImage);

        Image logOutImage = new Image(getClass().getResource("/Images/logout.png").toExternalForm());
        logoutImage.setImage(logOutImage);

        Image settingImage = new Image(getClass().getResource("/Images/settings.png").toExternalForm());
        settingsImage.setImage(settingImage);

        Image exitImage = new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm());
        closeBtnImage.setImage(exitImage);

        Image minimizeImage = new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm());
        minimizeBtnImage.setImage(minimizeImage);

        Image maximizeImage = new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm());
        maximizeBtnImage.setImage(maximizeImage);
    }
}
