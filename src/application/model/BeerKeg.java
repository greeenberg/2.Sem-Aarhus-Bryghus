package application.model;

public class BeerKeg extends Product {
    private final double deposit;
    private final double litre;

    public BeerKeg(String name, String type, double deposit, double litre) {
        super(name, type);
        this.deposit = deposit;
        this.litre = litre;
    }

    public double getDeposit() {
        return deposit;
    }

    public double getLitre() {
        return litre;
    }

    @Override
    public String toString() {
        return getName() + " (" + litre + " L)";
    }

}
