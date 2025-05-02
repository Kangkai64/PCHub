package pchub.model;

public abstract class LineItem {
    private Product product;
    private int quantity;
    private double unitPrice;

    public LineItem(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public LineItem(){

    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}