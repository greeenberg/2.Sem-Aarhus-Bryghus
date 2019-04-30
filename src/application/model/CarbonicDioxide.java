package application.model;

public class CarbonicDioxide extends Product {
    private final double deposit;
    private final double kg;

    public CarbonicDioxide(String name, String type, double deposit, double kg) {
        super(name, type);
        this.deposit = deposit;
        this.kg = kg;
    }

    public double getDeposit() {
        return deposit;
    }

    public double getkg() {
        return kg;
    }

    @Override
    public String toString() {
        return getName() + " " + kg;
    }
}
