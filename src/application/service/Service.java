package application.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import application.model.Beer;
import application.model.BeerKeg;
import application.model.BeerTap;
import application.model.BeerTapReservation;
import application.model.CarbonicDioxide;
import application.model.Customer;
import application.model.Gift;
import application.model.GuidedTourReservation;
import application.model.Order;
import application.model.OrderLine;
import application.model.Payment;
import application.model.PaymentMethod;
import application.model.Price;
import application.model.PriceList;
import application.model.Product;
import application.model.Reservation;
import application.model.WareHouse;
import application.model.WareHouseCapacity;
import storage.DiskStorage;
import storage.Storage;

public class Service {

	private static Storage storage;

	public void setStorage(Storage storage) {
		Service.storage = storage;
	}

	// -------------------------------CREATION OF MODEL
	// CLASSES-----------------------------------

	public Customer createCustomer(String name, int phoneNr) {
		Customer customer = new Customer(name, phoneNr);
		storage.storeCustomer(customer);
		return customer;
	}

	public void removeCustomer(Customer customer) {
		storage.removeCustomer(customer);
	}

	public List<Customer> getAllCustomers() {
		return storage.getAllCustomers();
	}

	public List<Order> getCustomerOrders(Customer customer) {
		return new ArrayList<>(customer.getOrders());
	}

	public List<Customer> getAllCustomers(String name) {
		List<Customer> customersWithName = new ArrayList<>();
		for (Customer customer : storage.getAllCustomers()) {
			if (customer.getName().toLowerCase().trim().contains(name.toLowerCase().trim())
					|| (customer.getPhoneNr() + "").toLowerCase().trim().contains(name.toLowerCase().trim())) {
				customersWithName.add(customer);
			}
		}
		return customersWithName;
	}

	// --------------------------------------------------------------------------------------

	public Product createProduct(String name, String type) {
		Product product = new Product(name, type);
		createWareHouseCapacity(product);
		storage.storeProduct(product);
		return product;
	}

	public Beer createBeer(String name, String type) {
		Beer beer = new Beer(name, type);
		createWareHouseCapacity(beer);
		storage.storeProduct(beer);
		return beer;
	}

	public BeerKeg createBeerKeg(String name, double deposit, double litre) {
		String type = "Fustage";
		BeerKeg beerKeg = new BeerKeg(name, type, deposit, litre);
		createWareHouseCapacity(beerKeg);
		storage.storeProduct(beerKeg);
		return beerKeg;
	}

	public Product createCarbonicDioxide(String name, double deposit, double kg) {
		String type = "Kulsyre";
		CarbonicDioxide carbonicDioxide = new CarbonicDioxide(name, type, deposit, kg);
		createWareHouseCapacity(carbonicDioxide);
		storage.storeProduct(carbonicDioxide);
		return carbonicDioxide;
	}

	public Gift createGift(String name, int amountBeer, int amountGlass) {
		String type = "Gaveæske";
		Gift gift = new Gift(name, type, amountBeer, amountGlass);
		createWareHouseCapacity(gift);
		storage.storeProduct(gift);
		return gift;
	}

	public BeerTap createBeerTap(String name, int amountTaps) {
		String type = "Anlæg";
		BeerTap beerTap = new BeerTap(name, type, amountTaps);
		storage.storeProduct(beerTap);
		return beerTap;
	}

	public void removeProduct(Product product) {
		storage.removeProduct(product);
	}

	public List<Product> getAllProducts() {
		return storage.getAllProducts();
	}

	/**
	 * @param name
	 * @return Returns Products whose names contain the string in the parameter
	 */
	public List<Product> getAllProducts(String name) {
		List<Product> productsWithName = new ArrayList<>();
		for (Product product : storage.getAllProducts()) {
			if (product.getName().toLowerCase().trim().contains(name.toLowerCase().trim())) {
				productsWithName.add(product);
			}
		}
		return productsWithName;
	}

	/**
	 * 
	 * @param name
	 * @return Returns a product whose name match exactly to <code>name </code>
	 *         </br>
	 *         Returns <code>null </code> if a product could not be found
	 */
	public Product getProduct(String name) {
		for (Product p : storage.getAllProducts()) {
			if (p.getName().toLowerCase().trim().compareTo(name.toLowerCase().trim()) == 0) {
				return p;
			}
		}
		return null;
	}

	// --------------------------------------------------------------------------------------

	public PriceList createPriceList(String name) {
		PriceList priceList = new PriceList(name);
		storage.storePriceList(priceList);
		return priceList;
	}

	public void removePriceList(PriceList priceList) {
		storage.removePriceList(priceList);
	}

	public List<PriceList> getAllPricelists() {
		return storage.getAllPriceLists();
	}

	public Price createPrice(PriceList priceList, Product product, double priceAmount) {
		Price price = new Price(product, priceAmount);
		priceList.addPrice(price);
		return price;
	}

	public void removePrice(Price price, PriceList priceList) {
		priceList.removePrice(price);
	}

	// --------------------------------------------------------------------------------------

	/**
	 * Customer is nullable
	 * 
	 * @param customer
	 * @param date
	 * @return
	 */
	public Order createOrder(Customer customer, LocalDate date) {
		Order order;
		if (customer != null) {
			order = new Order(customer, date);
		} else {
			order = new Order(null, date);
		}
		order.addWareHouseObserver(getWareHouse());
		storage.storeOrder(order);
		return order;
	}

	public void removeOrder(Order order) {
		storage.removeOrder(order);
	}

	public List<Order> getAllOrders() {
		return storage.getAllOrders();
	}

	/**
	 * Creates and returns an OrderLine if product exists in priceList, else returns
	 * null
	 * 
	 * @param order
	 * @param product
	 * @param amount
	 * @param priceList
	 * @return
	 */
	public OrderLine createOrderLine(Order order, Product product, int amount, PriceList priceList) {
		boolean state = false;
		for (Price p : priceList.getPrices()) {
			if (p.getProduct().compareTo(product) == 0) {
				state = true;
			}
		}
		if (state) {
			OrderLine orderLine = order.createOrderLine(product, amount, priceList);
			return orderLine;
		} else {
			return null;
		}
	}

	public OrderLine createOrderLineReservation(Order order, Reservation reservation) {
		OrderLine orderline = order.createOrderLineReservation(reservation);
		return orderline;
	}

	public void removeOrderLine(Order order, OrderLine orderLine) {
		order.removeOrderLine(orderLine);
	}

	public Payment createPayment(Order order, double amount, PaymentMethod paymentMethod, LocalDate date) {
		Payment payment = order.createPayment(amount, paymentMethod, date);
		return payment;
	}

	public PaymentMethod[] getAllPaymentMethod() {
		return PaymentMethod.values();
	}

	// --------------------------------------------------------------------------------------

	public WareHouse getWareHouse() {
		return storage.getWareHouse();
	}

	public WareHouseCapacity createWareHouseCapacity(Product product) {
		WareHouseCapacity wareHouseCapacity = storage.getWareHouse().createWareHouseCapacity(product);
		return wareHouseCapacity;
	}

	public void removeWareHouseCapacity(WareHouseCapacity wareHouseCapacity) {
		storage.getWareHouse().removeWareHouseCapacity(wareHouseCapacity);
	}

	// --------------------------------------------------------------------------------------

	private Reservation createBeerTapReservation(BeerTap beerTap, LocalDate startDate, LocalDate endDate,
			double dayPrice) {
		Reservation reservation = beerTap.createBeerTapReservation(startDate, endDate, dayPrice);
		storage.storeReservation(reservation);
		return reservation;
	}

	private Reservation createGuidedTourReservation(LocalDateTime reservationTime, int amountPeople,
			double pricePerPerson) {
		Reservation reservation = new GuidedTourReservation(reservationTime, amountPeople, pricePerPerson);
		storage.storeReservation(reservation);
		return reservation;
	}

	public void removeReservation(Reservation reservation) {
		storage.removeReservation(reservation);
	}

	public List<Reservation> getAllReservations() {
		return storage.getAllReservations();
	}

	public List<GuidedTourReservation> getGuidedTourReservations() {
		List<GuidedTourReservation> reservations = new ArrayList<>();
		for (Reservation r : storage.getAllReservations()) {
			if (r instanceof GuidedTourReservation) {
				reservations.add((GuidedTourReservation) r);
			}
		}
		return reservations;
	}

	public List<BeerTapReservation> getBeerTapReservations(BeerTap beertap) {
		return beertap.getReservations();
	}

	/**
	 * Creates a new BeerTapReservation </br>
	 * Throws DateTimeException if endDate is before startDate or the reservation
	 * overlaps an existing reservation
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Reservation reserveBeerTap(BeerTap beerTap, LocalDate startDate, LocalDate endDate, Customer customer,
			double dayPrice) {
		if (endDate.isBefore(startDate)) {
			throw new DateTimeException("Slut dato er før start dato");
		}
		if (!storage.getAllCustomers().contains(customer)) {
			throw new NullPointerException("Customer was not found");
		}

		for (BeerTapReservation r : getBeerTapReservations(beerTap)) {

			if (r.getBeerTap().getName().compareTo(beerTap.getName()) == 0) {
				if (isBetween3(startDate, endDate, r.getStartDate(), r.getEndDate())) {
					throw new DateTimeException("Denne reservation overlapper en eksisterende reservation");
				}
			}
		}
		Reservation reservation = createBeerTapReservation(beerTap, startDate, endDate, dayPrice);
		Order order = createOrder(customer, startDate);
		order.createOrderLineReservation(reservation);
		return reservation;
	}

	/**
	 * Creates a GuidedTourReservation </br>
	 * Throws DateTimeException if <code> reservationTime </code> starts before 12
	 * or after 18 <br>
	 * Throws DateTimeException if <code> reservationTime </code> plus 3 hours,
	 * overlaps another reservation <br>
	 * Also creates an order that has the reservation saved
	 * 
	 * @param reservationTime
	 * @param amountPeople
	 * @return
	 */
	public Reservation reserveGuidedTour(LocalDateTime reservationTime, int amountPeople, double pricePerPerson,
			Customer customer) {

		if (reservationTime.toLocalTime().isBefore(LocalTime.of(12, 00))
				|| reservationTime.toLocalTime().isAfter(LocalTime.of(18, 00))) {
			throw new DateTimeException("Reservation er ikke indenfor det gyldige tidsrum");
		}

		for (GuidedTourReservation r : getGuidedTourReservations()) {
			LocalTime from = r.getReservationTime().toLocalTime();
			LocalTime to = from.plusHours(3);
			if (reservationTime.toLocalTime().compareTo(r.getReservationTime().toLocalTime()) == 0)
				if (isBetween(reservationTime.toLocalTime(), from, to)) {
					throw new DateTimeException("Reservation overlapper en eksisterende reservation");
				}
		}
		Reservation reservation = createGuidedTourReservation(reservationTime, amountPeople, pricePerPerson);

		Order order = createOrder(customer, reservationTime.toLocalDate());
		order.createOrderLineReservation(reservation);
		return reservation;
	}

	/**
	 * Helper method to determine if parameter <code>t </code> is between parameters
	 * <code>from </code> and <code>to </code>
	 */
	private static boolean isBetween(LocalTime t, LocalTime from, LocalTime to) {
		if (from.isBefore(to)) {
			return from.isBefore(t) && t.isBefore(to);
		} else {
			return from.isBefore(t) || t.isBefore(to);
		}
	}

	/**
	 * Helper method that returns true if the 2 first dates overlap the 2 second
	 * dates
	 */
	private static boolean isBetween3(LocalDate firstFrom, LocalDate firstTo, LocalDate secondFrom,
			LocalDate secondTo) {
		long first1 = firstFrom.toEpochDay();
		long first2 = firstTo.toEpochDay();
		long second1 = secondFrom.toEpochDay();
		long second2 = secondTo.toEpochDay();
		return (first1 < second2 && second1 < first2);
	}

	@SuppressWarnings("unused")
	private static boolean isBetween(LocalDate t, LocalDate from, LocalDate to) {
		if ((t.isBefore(to) && t.isAfter(from)) || (t.isAfter(to) && t.isAfter(from))
				|| (t.isBefore(to) && t.isBefore(from))) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unused")
	private static boolean isBetween2(LocalDate t, LocalDate from, LocalDate to) {
		long toTest = t.toEpochDay();
		long getFrom = from.toEpochDay();
		long getTo = to.toEpochDay();

		if ((toTest - getFrom) > 0 && (toTest - getTo) < 0) {
			return true;
		} else
			return false;
	}

	/*
	 * Returns all reservations for the given customer
	 */
	public List<Reservation> getCustomerReservations(Customer customer) {
		return customer.getAllReservations();
	}

	// --------------------------------OTHER
	// FUNCTIONALITY-----------------------------------

	public double calcAllCustomerMoneySpent(Customer customer) {
		return customer.calcAllMoneySpent();
	}

	/**
	 * Goes through orders on the specific <code> LocalDate </code> and checks if
	 * they all match the <code> excpectedValue </code> <br>
	 * Returns <code>true </code>if the balance match the excpected value
	 * 
	 * @param date
	 * @param excpectedValue
	 * @return
	 */
	public boolean calcCashBalance(LocalDate date, double excpectedValue) {
		List<Order> ordersOnThisDate = new ArrayList<>();
		for (Order order : storage.getAllOrders()) {
			if (order.getDate().compareTo(date) == 0) {
				ordersOnThisDate.add(order);
			}
		}
		double datesSum = 0;
		for (Order order : ordersOnThisDate) {
			datesSum += order.calcPrice();
		}

		if (datesSum == excpectedValue) {
			return true;
		} else {
			return false;
		}
	}

	public List<Order> getOrdersInPeriod(LocalDate startDate, LocalDate endDate) {
		List<Order> ordersInPeriod = new ArrayList<>();
		for (Order o : storage.getAllOrders()) {
			if (o != null) {
				if (o.getDate().isAfter(startDate.minusDays(1)) && o.getDate().isBefore(endDate.plusDays(1))) {
					ordersInPeriod.add(o);
				}
			}
		}

		return ordersInPeriod;
	}

	public int getClipsBoughtInOrderList(List<Order> orders) {
		int clipBoughtAmount = 0;
		for (Order order : orders) {
			if (order != null) {
				for (OrderLine orderLine : order.getOrderLines()) {
					if (orderLine != null && orderLine.getReservation() == null) {
						if (orderLine.getProduct().getType().compareTo("Klippekort") == 0) {
							clipBoughtAmount += orderLine.getAmount();
						}
					}
				}
			}
		}

		return clipBoughtAmount * 4;
	}

	/**
	 * Returns the amount of products sold within the specified period
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getAmountSoldByTypeInPeriod(String type, LocalDate startDate, LocalDate endDate) {
		List<Order> ordersInPeriod = getOrdersInPeriod(startDate, endDate);
		int amount = 0;
		for (Order order : ordersInPeriod) {
			if (order != null)
				for (OrderLine o : order.getOrderLines()) {
					if (o != null && o.getReservation() == null) {
						if (o.getProduct().getType().toLowerCase().trim().compareTo(type.toLowerCase().trim()) == 0) {
							amount += o.getAmount();
						}
					}
				}
		}
		return amount;
	}

	public int getAmountSoldByProductsInPeriod(Product product, LocalDate startDate, LocalDate endDate) {
		List<Order> ordersInPeriod = getOrdersInPeriod(startDate, endDate);
		int amount = 0;

		for (Order order : ordersInPeriod) {
			if (order != null) {
				for (OrderLine orderline : order.getOrderLines()) {
					if (orderline != null && orderline.getReservation() == null) {
						if (orderline.getProduct().compareTo(product) == 0) {
							amount += orderline.getAmount();
						}
					}
				}
			}
		}
		return amount;
	}

	public int getClipsUsedInOrderList(List<Order> orders) {
		int clipUsedAmount = 0;
		for (Order order : orders) {
			if (order != null) {
				for (Payment p : order.getPayments()) {
					if (p != null) {
						if (p.getPaymentMethod() == PaymentMethod.KLIP) {
							clipUsedAmount += p.getClip();
						}
					}
				}
			}
		}
		return clipUsedAmount;
	}

	/**
	 * Pre: <code>startDate </code> is before <code> endDate </code>
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int remainingClipsInPeriod(LocalDate startDate, LocalDate endDate) {
		List<Order> ordersInPeriod = getOrdersInPeriod(startDate, endDate);

		int clipBoughtAmount = getClipsBoughtInOrderList(ordersInPeriod);
		int clipUsedAmount = getClipsUsedInOrderList(ordersInPeriod);

		return clipBoughtAmount - clipUsedAmount;
	}

	public List<LocalDate> calcDays(LocalDate start, LocalDate end) {
		List<LocalDate> localDates = new ArrayList<>();
		LocalDate dateNow = start;

		while (start.isBefore(end)) {
			localDates.add(dateNow);
			dateNow.plusDays(1);
		}

		return localDates;
	}

	// -------------------------------STORAGE LOADING AND
	// SAVING----------------------------------

	/** Loads the storage (including all objects in storage). */
	public static void loadStorage() {
		try (FileInputStream fileIn = new FileInputStream("storage.ser")) {
			try (ObjectInputStream in = new ObjectInputStream(fileIn);) {
				storage = (DiskStorage) in.readObject();
				System.out.println("Storage loaded from file storage.ser.");
			} catch (ClassNotFoundException ex) {
				System.out.println("Error loading storage object.");
				throw new RuntimeException(ex);
			}
		} catch (IOException ex) {
			System.out.println("Error loading storage object.");
			throw new RuntimeException(ex);
		}
	}

	/** Saves the storage (including all objects in storage). */
	public static void saveStorage() {
		try (FileOutputStream fileOut = new FileOutputStream("storage.ser")) {
			try (ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
				out.writeObject(storage);
				System.out.println("Storage saved in file storage.ser.");
			}
		} catch (IOException ex) {
			System.out.println("Error saving storage object.");
			throw new RuntimeException(ex);
		}
	}

}
