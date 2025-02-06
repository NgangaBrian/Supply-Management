package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RecordPaymentController implements Initializable {

    @FXML
    private ImageView closeBtnImage, maximizeBtnImage, minimizeBtnImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImages();
    }

    private void loadImages() {
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
