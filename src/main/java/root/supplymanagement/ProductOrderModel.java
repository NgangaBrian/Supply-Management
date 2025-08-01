package root.supplymanagement;

public class ProductOrderModel {
    String productName;
    int quantity;
    double unitPrice;
    double amount;

    public ProductOrderModel(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = unitPrice * quantity;
    }

    public double getAmount() {return amount;}

    public void setAmount(double amount) {this.amount = amount;}

    public double getUnitPrice() {return unitPrice;}

    public void setUnitPrice(double unitPrice) {this.unitPrice = unitPrice;}

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
