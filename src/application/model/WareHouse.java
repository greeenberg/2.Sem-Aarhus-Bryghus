package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WareHouse implements Serializable, WareHouseObserver {

    private final List<WareHouseCapacity> wareHouseCapacity = new ArrayList<>();

    public WareHouseCapacity createWareHouseCapacity(Product product) {
        WareHouseCapacity house = new WareHouseCapacity(product);
        wareHouseCapacity.add(house);
        return house;
    }

    public void removeWareHouseCapacity(WareHouseCapacity house) {
        wareHouseCapacity.remove(house);
    }

    public List<WareHouseCapacity> getWareHouseCapacity() {
        return wareHouseCapacity;
    }

    @Override
    public void update(Order order) {
        for (WareHouseCapacity wareHouseCapacity2 : wareHouseCapacity) {
            wareHouseCapacity2.update(order);
        }
    }

}
