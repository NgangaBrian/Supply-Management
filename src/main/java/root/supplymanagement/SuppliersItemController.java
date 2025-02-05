package root.supplymanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class SuppliersItemController {

    @FXML
    private Label supplierName, supplierPhone, supplierAddress, supplierEmail;
    @FXML
    private ImageView editSupplier, deleteSupplier;

    public void setSupplierData(String name, String phone, String address, String email) {
        supplierName.setText(name);
        supplierPhone.setText(phone);
        supplierAddress.setText(address);
        supplierEmail.setText(email);

        File editFile = new File("Images/editBtn.png");
        Image editImage = new Image(editFile.toURI().toString());
        editSupplier.setImage(editImage);

        File deleteFile = new File("Images/deleteBtn.png");
        Image deleteImage = new Image(deleteFile.toURI().toString());
        deleteSupplier.setImage(deleteImage);
    }
}
