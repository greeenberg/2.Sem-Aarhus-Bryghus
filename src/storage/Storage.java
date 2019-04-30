package storage;

import java.util.List;

import application.model.Customer;
import application.model.Order;
import application.model.PriceList;
import application.model.Product;
import application.model.Reservation;
import application.model.WareHouse;

public interface Storage {

    public void storeCustomer(Customer customer);

    public void removeCustomer(Customer customer);

    public List<Customer> getAllCustomers();

    //-----------------------------------------------------------------------------

    public void storePriceList(PriceList priceList);

    public void removePriceList(PriceList priceList);

    public List<PriceList> getAllPriceLists();

    //-----------------------------------------------------------------------------

    public void storeProduct(Product product);

    public void removeProduct(Product product);

    public List<Product> getAllProducts();

    //-----------------------------------------------------------------------------

    public void storeOrder(Order order);

    public void removeOrder(Order order);

    public List<Order> getAllOrders();

    //-----------------------------------------------------------------------------

    public void storeReservation(Reservation reservation);

    public void removeReservation(Reservation reservation);

    public List<Reservation> getAllReservations();

    // ----------------------------------------------------------------------------

    public WareHouse getWareHouse();

    // ----------------------------------------------------------------------------

}
