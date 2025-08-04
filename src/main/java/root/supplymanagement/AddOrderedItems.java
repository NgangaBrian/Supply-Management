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

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddOrderedItems {

    @FXML private ImageView closeBtnImage, minimizeBtnImage, maximizeBtnImage;
    @FXML private TextField itemNameId, unitPrice;
    @FXML private VBox vboxContainer;

    private ObservableList<String> itemNames = FXCollections.observableArrayList();
    private String orderNo;

    public void initialize() {
        unitPrice.setOnAction(event -> {
            String itemName = itemNameId.getText().trim();
            String priceText = unitPrice.getText().trim();

            if (itemName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a product name.");
                return;
            }

            try {
                Double amount = Double.valueOf(priceText);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be greater than 0.");
                    return;
                }
                addProductToList(itemName, amount);
                itemNameId.clear();
                unitPrice.clear();
                itemNameId.requestFocus();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid number for unit price.");
            }
        });
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void saveOrderedProducts() {
        if (orderNo == null || orderNo.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Order Missing", "Order number is missing. Please set it before saving.");
            return;
        }

        List<ProductOrderModel> productOrders = new ArrayList<>();

        for (Node node : vboxContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                Object data = hbox.getUserData();
                if (!(data instanceof OrderedProductItemController controller)) {
                    System.out.println("Invalid or missing controller on node.");
                    continue;
                }

                String productName = controller.getProductName();
                int quantity;
                double unitPrice;

                try {
                    quantity = controller.getQuantity();
                    unitPrice = controller.getUnitPrice();
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please enter valid numeric values.");
                    return;
                }

                if (productName.isEmpty() || quantity <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Each product must have a name and quantity > 0.");
                    return;
                }

                productOrders.add(new ProductOrderModel(productName, quantity, unitPrice));
            }
        }

        if (productOrders.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Products", "Please add at least one valid product to save.");
            return;
        }

        insertProductsIntoDatabase(productOrders);
    }

    private void insertProductsIntoDatabase(List<ProductOrderModel> productOrders) {
        String query = "INSERT INTO ordered_products (orderNo, productName, quantity, unit_price, amount) VALUES (?, ?, ?, ?, ?)";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (ProductOrderModel product : productOrders) {
                preparedStatement.setString(1, orderNo);
                preparedStatement.setString(2, product.getProductName());
                preparedStatement.setInt(3, product.getQuantity());
                preparedStatement.setDouble(4, product.getUnitPrice());
                preparedStatement.setDouble(5, product.getAmount());
                preparedStatement.addBatch();
            }

            int[] results = preparedStatement.executeBatch();
            int successful = 0;
            for (int result : results) {
                if (result >= 0 || result == Statement.SUCCESS_NO_INFO) {
                    successful++;
                }
            }

            if (successful > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ordered products saved successfully.");
                Stage stage = (Stage) vboxContainer.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "No products were inserted. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving products.\n" + e.getMessage());
        }
    }

    private void addProductToList(String productName, Double amount) {
        for (Node node : vboxContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                Label nameLabel = (Label) hbox.lookup("#productName");
                if (nameLabel != null && nameLabel.getText().equalsIgnoreCase(productName)) {
                    showAlert(Alert.AlertType.WARNING, "Duplicate", "Product already added: " + productName);
                    return;
                }
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("orderedProductItem.fxml"));
            Parent hboxNode = loader.load();

            OrderedProductItemController controller = loader.getController();
            controller.setProductName(productName, amount);
            hboxNode.setUserData(controller);

            controller.setOnRemove(() -> vboxContainer.getChildren().remove(hboxNode));
            vboxContainer.getChildren().add(hboxNode);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "UI Error", "Failed to load product item component.");
        }
    }

    // Utility method for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Optional: remove unused method if not needed
    private void removeProduct(HBox productHBox) {
        vboxContainer.getChildren().remove(productHBox);
    }

    public void handleCloseBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) closeBtnImage.getScene().getWindow();
        stage.close();
    }

    public void handleMinimizeBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage stage) {
                stage.setIconified(true);
            }
        }
    }

    public void handleMaximizeBtnClick(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = (Stage) maximizeBtnImage.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
}