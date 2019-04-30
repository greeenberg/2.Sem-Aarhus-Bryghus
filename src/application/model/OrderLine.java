package application.model;

import java.io.Serializable;

public class OrderLine implements Serializable {
    private int amount;
    private PriceList pricelist;
    private Product product;
    private double price;
    private Reservation reservation;

    public OrderLine(Product product, int amount, PriceList pricelist) {
        this.amount = amount;
        this.pricelist = pricelist;
        this.product = product;
        this.price = this.getProductPrice();
    }

    public OrderLine(Reservation reservation) {
        this.reservation = reservation;
        this.amount = 1;
        this.price = reservation.getPrice();
    }

    public double getPrice() {
        return price;
    }

    public Product getProduct() {
        return product;
    }

    public PriceList getPricelist() {
        return pricelist;
    }

    public int getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        reservation.setOrderLine(this);
    }

    /**
     * Find the price of a product, if it returns -1 the product does not exist
     **/
    public double getProductPrice() {
        for (Price prices : pricelist.getPrices()) {
            if (prices.getProduct().compareTo(product) == 0) {
                return prices.getPrice();
            }
        }
        return -1;
    }

    /**
     * Pre: 0 < percentAmount <= 100
     * 
     * @param percentAmount
     */
    public void giveDiscountPercentage(double percentAmount) {
        assert percentAmount > 0;
        assert percentAmount <= 100;

        this.price = price - ((percentAmount / 100) * price);

    }

    /**
     * If the discount amount is greater than the price, the price will be set to 0
     * </br>
     * Pre: wholeAmount > 0
     * 
     * @param wholeAmount
     */
    public void giveDiscountWholeAmount(double wholeAmount) {
        assert wholeAmount > 0;
        if (price - wholeAmount < 0) {
            this.price = 0;
        } else {
            this.price -= wholeAmount;
        }

    }

    @Override
    public String toString() {
        if (this.reservation == null) {
            return amount + "x " + product + " " + price + ",-";
        } else {
            return reservation.toString();
        }
    }

}
