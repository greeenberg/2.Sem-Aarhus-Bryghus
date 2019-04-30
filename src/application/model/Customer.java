package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
	private final String name;
	private final int phoneNr;
	private final List<Order> orders = new ArrayList<>();

	public Customer(String name, int phoneNr) {
		this.name = name;
		this.phoneNr = phoneNr;
	}

	public String getName() {
		return name;
	}

	public int getPhoneNr() {
		return phoneNr;
	}

	public List<Order> getOrders() {
		return orders;
	}

	@Override
	public String toString() {
		return name + " (" + phoneNr + ")";
	}

	// ---------------------------------------------------------------------------

	public double calcAllMoneySpent() {
		double sum = 0;
		for (Order order : orders) {
			sum += order.calcTotalPrice();
		}
		return sum;
	}

	public List<Reservation> getAllReservations() {
		List<Reservation> reservations = new ArrayList<>();
		for (Order order : orders) {
			for (OrderLine orderline : order.getOrderLines()) {
				if (orderline.getReservation() != null) {
					reservations.add(orderline.getReservation());
				}
			}
		}
		return reservations;
	}

	// ------------------ASSOSATION-----------------------------------------------------
	void addOrder(Order order) {
		orders.add(order);
	}

}
