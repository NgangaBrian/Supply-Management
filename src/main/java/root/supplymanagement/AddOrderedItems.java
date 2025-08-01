package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddOrderedItems {

    @FXML
    private ImageView closeBtnImage, minimizeBtnImage, maximizeBtnImage;
    @FXML
    private TextField itemNameId, unitPrice;
    @FXML
    private VBox vboxContainer;

    private ObservableList<String> itemNames = FXCollections.observableArrayList();
    private String orderNo;


    public void initialize() {
        unitPrice.setOnAction(event -> {
            String itemName = itemNameId.getText();
            Double amount = Double.valueOf(unitPrice.getText());
            if (!itemName.trim().isEmpty()) {
                addProductToList(itemName, amount);
                itemNameId.clear();
                unitPrice.clear();
            }
        });
    }

    public void saveOrderedProducts() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (orderNo == null || orderNo.isEmpty()) {
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Order No is missing!");
            alert.showAndWait();
            return;
        }

        List<ProductOrderModel> productOrders = new ArrayList<>();

        // Loop through VBox children and collect product names and quantities
        for (Node node : vboxContainer.getChildren()) {
            if (node instanceof HBox) {
                OrderedProductItemController controller = (OrderedProductItemController) node.getUserData();
                if (controller == null) {
                    System.out.println("Controller not found for node!");
                    continue;
                }

                String productName = controller.getProductName();
                int quantity;

                try {
                    quantity = controller.getQuantity();

                } catch (NumberFormatException e) {
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid quantity format for: " + productName);
                    return;
                }

                if (productName.isEmpty() || quantity <= 0) {
                    System.out.println("Skipping invalid entry: Empty name or invalid quantity.");
                    continue;
                }
                double unitPrices = controller.getUnitPrice();
                productOrders.add(new ProductOrderModel(productName, quantity, unitPrices));
            }
        }

        if (productOrders.isEmpty()) {
            System.out.println("No valid products to save!");
            return;
        }

        insertProductsIntoDatabase(productOrders);
    }

    public void insertProductsIntoDatabase(List<ProductOrderModel> productOrders) {
        String query = "INSERT INTO ordered_products (orderNo, productName, quantity, unit_price, amount) VALUES (?, ?, ?, ?, ?)";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        DBConnection connect = new DBConnection();
        try (Connection connection = connect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (ProductOrderModel product : productOrders) {
                preparedStatement.setString(1, orderNo);
                preparedStatement.setString(2, product.getProductName());
                preparedStatement.setInt(3, product.getQuantity());
                preparedStatement.setDouble(4, product.getUnitPrice());
                preparedStatement.setDouble(5, product.getAmount());
                preparedStatement.addBatch();
            }

            int[] rowsAffected = preparedStatement.executeBatch();
            if (rowsAffected.length > 0) {

                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Ordered products saved successfully!");
                alert.showAndWait();

                // ✅ Close the window
                Stage stage = (Stage) vboxContainer.getScene().getWindow();
                stage.close();
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Ordered products could not be saved!");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save products. Please try again.");
            alert.showAndWait();
        }
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    private void addProductToList(String productName, Double amount) {
        // Check if the product is already in the VBox
        for (Node node : vboxContainer.getChildren()) {
            if (node instanceof HBox) {
                Label label = (Label) node.lookup("#productName");
                Label label2 = (Label) node.lookup("#itemAmount");
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
            controller.setProductName(productName, amount);

            // ✅ Store the controller in the HBox (so we can retrieve it later)
            hboxNode.setUserData(controller);

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

//    private void loadProductsComboBox() {
//        String querry = "select name from products";
//
//        DBConnection connect = new DBConnection();
//        try(Connection connection = connect.getConnection();
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(querry);){
//            while (resultSet.next()) {
//                itemNames.add(resultSet.getString("name"));
//            }
//            productsComboBox.setItems(itemNames);
//
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//    }

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
