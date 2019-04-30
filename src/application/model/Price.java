package application.model;

import java.io.Serializable;

public class Price implements Serializable {
    private Product product;
    private double price;

    public Price(Product product, double price) {
        this.product = product;
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

}
