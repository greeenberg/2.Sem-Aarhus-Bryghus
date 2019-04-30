package application.model;

import java.io.Serializable;

public class WareHouseCapacity implements Serializable {

    private int stock;
    private Product product;

    WareHouseCapacity(Product product) {
        this.product = product;
        stock = 0;
    }

    public int getStock() {
        return stock;
    }

    public void addToStock(int amount) {
        this.stock += amount;
    }

    public void decrementToStock(int amount) {
        this.stock -= amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return product.getType() + " " + product.getName() + " (" + stock + ")";
    }

    public void update(Order order) {
        for (OrderLine ol : order.getOrderLines()) {
            if (ol.getProduct().compareTo(product) == 0) {
                stock -= ol.getAmount();
            }
        }
    }

}
