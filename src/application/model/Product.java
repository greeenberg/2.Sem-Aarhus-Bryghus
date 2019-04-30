package application.model;

import java.io.Serializable;

public class Product implements Comparable<Product>, Serializable {

    private final String name;
    private final String type;

    public Product(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Product o) {
        if (this.type.compareTo(o.type) == 0) {
            return this.name.compareTo(o.name);
        } else {
            return this.type.compareTo(o.type);
        }
    }

}
