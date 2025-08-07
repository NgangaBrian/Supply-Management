package root.supplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ViewOrdersController implements Initializable {

    @FXML
    private ImageView maximizeBtnImage, minimizeBtnImage, closeBtnImage;
    @FXML
    private VBox orderItems;
    @FXML
    private ComboBox<String> filter;

    private final ObservableList<String> filterItems = FXCollections.observableArrayList("All", "Last 7 days", "Last 30 days", "Last 90 days");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();

        filter.setItems(filterItems);
        filter.setValue("All"); // Default to fetching all data
        loadOrderData(null);

        filter.setOnAction(event -> loadOrderData(filter.getValue()));
    }

    private void loadOrderData(String filterOption) {
        orderItems.getChildren().clear(); // Clear existing items

        String query = "SELECT * FROM orders";
        boolean hasFilter = filterOption != null && !"All".equals(filterOption);

        if (hasFilter) {
            query += " WHERE createdAt >= NOW() - INTERVAL ? DAY";
        }

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (hasFilter) {
                int days = switch (filterOption) {
                    case "Last 30 days" -> 30;
                    case "Last 90 days" -> 90;
                    default -> 7;
                };
                preparedStatement.setInt(1, days);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ordersItem.fxml"));
                    Node node = loader.load();
                    OrdersItemController controller = loader.getController();

                    controller.setOrderData(
                            resultSet.getString("orderNo"),
                            resultSet.getString("supplier"),
                            resultSet.getDouble("totalAmount"),
                            resultSet.getDouble("paidAmount"),
                            resultSet.getString("currency"),
                            resultSet.getDouble("balance"),
                            LocalDate.from(resultSet.getTimestamp("createdAt").toLocalDateTime())
                    );

                    orderItems.getChildren().add(node);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Ideally, log this error instead of printing
        }
    }

    @FXML
    private void handleCloseBtnClick() {
        ((Stage) closeBtnImage.getScene().getWindow()).close();
    }

    @FXML
    private void handleMinimizeBtnClick() {
        Stage.getWindows().stream()
                .filter(window -> window instanceof Stage)
                .map(window -> (Stage) window)
                .forEach(stage -> stage.setIconified(true));
    }

    @FXML
    private void handleMaximizeBtnClick() {
        Stage stage = (Stage) maximizeBtnImage.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    private void loadImages() {
        closeBtnImage.setImage(new Image(getClass().getResource("/Images/closeBtn.png").toExternalForm()));
        minimizeBtnImage.setImage(new Image(getClass().getResource("/Images/minimizeBtn.png").toExternalForm()));
        maximizeBtnImage.setImage(new Image(getClass().getResource("/Images/maximizeBtn.png").toExternalForm()));
    }
}
