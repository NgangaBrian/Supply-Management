package root.supplymanagement;


import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Pane overdueInvoicesPane, recordOrderPane, recordPaymentPane, addSupplierPane,
            viewOrderPane, viewPaymentsPane, viewSuppliersPane, logOutPane;
    @FXML
    private ImageView logoutImage, viewSupplierImage, viewPaymentsImage, viewOrdersImage, overdueInvoiceImage,
            addSupplierImage, recordPaymentImage, recordOrderImage, settingsImage, closeBtnImage, maximizeBtnImage,
            minimizeBtnImage, logoImageView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();

        // Apply fade-in and slide-in animations
        applyFadeInAnimation(overdueInvoicesPane);
        applyFadeInAnimation(recordOrderPane);
        applyFadeInAnimation(recordPaymentPane);
        applyFadeInAnimation(addSupplierPane);
        applyFadeInAnimation(viewOrderPane);
        applyFadeInAnimation(viewPaymentsPane);
        applyFadeInAnimation(viewSuppliersPane);
        applyFadeInAnimation(logOutPane);
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

    public void openAddSuppliersPane(javafx.scene.input.MouseEvent mouseEvent){
        try {
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
        File logoFile = new File("Images/kimsalogo.png");
        Image logoImage = new Image(logoFile.toURI().toString());
        logoImageView.setImage(logoImage);

        File orderFile = new File("Images/recordOrder.png");
        Image orderImage = new Image(orderFile.toURI().toString());
        recordOrderImage.setImage(orderImage);

        File paymentFile = new File("Images/recordPayment.png");
        Image paymentImage = new Image(paymentFile.toURI().toString());
        recordPaymentImage.setImage(paymentImage);

        File supplierFile = new File("Images/addSupplier.png");
        Image supplierImage = new Image(supplierFile.toURI().toString());
        addSupplierImage.setImage(supplierImage);

        File invoiceFile = new File("Images/overdueInvoices.png");
        Image invoiceImage = new Image(invoiceFile.toURI().toString());
        overdueInvoiceImage.setImage(invoiceImage);

        File ordersFile = new File("Images/viewOrders.png");
        Image ordersImage = new Image(ordersFile.toURI().toString());
        viewOrdersImage.setImage(ordersImage);

        File viewPaymentsFile = new File("Images/viewPayments.png");
        Image viewPaymentImage = new Image(viewPaymentsFile.toURI().toString());
        viewPaymentsImage.setImage(viewPaymentImage);

        File viewSupplierFile = new File("Images/viewSuppliers.png");
        Image viewSuppliersImage = new Image(viewSupplierFile.toURI().toString());
        viewSupplierImage.setImage(viewSuppliersImage);

        File logoutFile = new File("Images/logout.png");
        Image logOutImage = new Image(logoutFile.toURI().toString());
        logoutImage.setImage(logOutImage);

        File settingsFile = new File("Images/settings.png");
        Image settingImage = new Image(settingsFile.toURI().toString());
        settingsImage.setImage(settingImage);

        File exitFile = new File("Images/closeBtn.png");
        Image exitImage = new Image(exitFile.toURI().toString());
        closeBtnImage.setImage(exitImage);

        File minimizeFile = new File("Images/minimizeBtn.png");
        Image minimizeImage = new Image(minimizeFile.toURI().toString());
        minimizeBtnImage.setImage(minimizeImage);

        File maximizeFile = new File("Images/maximizeBtn.png");
        Image maximizeImage = new Image(maximizeFile.toURI().toString());
        maximizeBtnImage.setImage(maximizeImage);
    }
}
