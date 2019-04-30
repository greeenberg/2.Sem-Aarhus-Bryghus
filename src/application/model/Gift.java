package application.model;

public class Gift extends Product {

    private final int amountBeer;
    private final int amountGlass;

    public Gift(String name, String type, int amountBeer, int amountGlass) {
        super(name, type);
        this.amountBeer = amountBeer;
        this.amountGlass = amountGlass;
    }

    public int getAmountBeer() {
        return amountBeer;
    }

    public int getAmountGlass() {
        return amountGlass;
    }

    @Override
    public String toString() {
        if (amountGlass == 0)
            return super.toString() + " Antal øl: " + amountBeer;
        else {
            return super.toString() + " (Antal øl: " + amountBeer + " Antal Glas: " + amountGlass
                    + ")";
        }
    }

    @Override
    public int compareTo(Product o) {
        if (o instanceof Gift) {
            if (this.amountBeer - ((Gift) o).amountBeer == 0) {
                return this.amountGlass - ((Gift) o).amountGlass;
            } else {
                return this.amountBeer - ((Gift) o).amountBeer;
            }
        } else {
            return super.compareTo(o);
        }
    }

}
