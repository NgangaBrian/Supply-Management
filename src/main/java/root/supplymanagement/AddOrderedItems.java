package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddOrderedItems {

    @FXML
    private ImageView closeBtnImage, minimizeBtnImage, maximizeBtnImage;
    @FXML
    private ComboBox<String> productsComboBox;
    @FXML
    private VBox vboxContainer;

    private ObservableList<String> itemNames = FXCollections.observableArrayList();


    public void initialize() {
        loadProductsComboBox();

        productsComboBox.setOnAction(this::handleComboBoxSelection);
    }

    private void handleComboBoxSelection(javafx.event.ActionEvent actionEvent) {

        String selectedProduct = productsComboBox.getValue();
        if (selectedProduct != null) {
            addProductToList(selectedProduct);
        }
    }

    private void addProductToList(String productName) {
        // Check if the product is already in the VBox
        for (Node node : vboxContainer.getChildren()) {
            if (node instanceof HBox) {
                Label label = (Label) node.lookup("#productName");
                if (label != null && label.getText().equals(productName)) {
                    System.out.println("Product already added: " + productName);
                    return; // Exit method without adding a duplicate
                }
            }
        }

        try {
            // Load FXML and its controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("orderedProductItem.fxml"));
            Parent hboxNode = loader.load();

            OrderedProductItemController controller = loader.getController();
            controller.setProductName(productName);

            // Set up the remove action
            controller.setOnRemove(() -> vboxContainer.getChildren().remove(hboxNode));

            // Add to VBox
            vboxContainer.getChildren().add(hboxNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeProduct(HBox productHBox) {
        vboxContainer.getChildren().remove(productHBox);
    }

    private void loadProductsComboBox() {
        String querry = "select name from products";

        DBConnection connect = new DBConnection();
        try(Connection connection = connect.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querry);){
            while (resultSet.next()) {
                itemNames.add(resultSet.getString("name"));
            }
            productsComboBox.setItems(itemNames);

        } catch (SQLException e){
            e.printStackTrace();
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
}
