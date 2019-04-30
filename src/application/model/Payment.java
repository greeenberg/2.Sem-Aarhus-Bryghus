package application.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Payment implements Serializable {
    private double amount;
    private PaymentMethod paymentMethod;
    private LocalDate date;
    private Order order;
    private int clip; //used when paymentMethod is Klip to save the amount of clips used

    // Spørg Micheal om package visibility
    Payment(Order order, double amount, PaymentMethod paymentMethod, LocalDate date) { // Package visibility
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.order = order;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Order getOrder() {
        return order;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public int getClip() {
        return clip;
    }

    public void setClip(int clip) {
        this.clip = clip;
    }

}
