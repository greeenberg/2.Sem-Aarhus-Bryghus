package storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import application.model.Customer;
import application.model.Order;
import application.model.PriceList;
import application.model.Product;
import application.model.Reservation;
import application.model.WareHouse;

public class DiskStorage implements Storage, Serializable {

    private static DiskStorage storage;

    private DiskStorage() {
        //Do nothing
    }

    public static Storage getInstance() {
        if (storage == null) {
            storage = new DiskStorage();
        }
        return storage;
    }

    public static void clearStorage() {
        DiskStorage.storage = null;
        DiskStorage.storage = new DiskStorage();
    }

    //-----------------------------------------------------------------------------
    private final List<Customer> customers = new ArrayList<>();
    private final List<PriceList> priceLists = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private WareHouse wareHouse = new WareHouse();

    //-----------------------------------------------------------------------------

    @Override
    public void storeCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public void removeCustomer(Customer customer) {
        customers.remove(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    //-----------------------------------------------------------------------------

    @Override
    public void storePriceList(PriceList priceList) {
        priceLists.add(priceList);
    }

    @Override
    public void removePriceList(PriceList priceList) {
        priceLists.remove(priceList);
    }

    @Override
    public List<PriceList> getAllPriceLists() {
        return new ArrayList<>(priceLists);
    }

    //-----------------------------------------------------------------------------

    @Override
    public void storeProduct(Product product) {
        products.add(product);
    }

    @Override
    public void removeProduct(Product product) {
        products.remove(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    //-----------------------------------------------------------------------------

    @Override
    public void storeOrder(Order order) {
        orders.add(order);
    }

    @Override
    public void removeOrder(Order order) {
        orders.remove(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    //-----------------------------------------------------------------------------

    @Override
    public void storeReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }
    // ----------------------------------------------------------------------------

    @Override
    public WareHouse getWareHouse() {
        return wareHouse;
    }

}
