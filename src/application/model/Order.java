package application.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
	private LocalDate date;
	private boolean isPaid;
	private double deposit;
	private boolean depositPaid;
	private boolean isUsed = false;

	// Observer pattern
	private final List<WareHouseObserver> wareHouseObservers = new ArrayList<>();

	// Assosations
	private Customer customer;
	private List<OrderLine> orderLines = new ArrayList<>();
	private List<Payment> payments = new ArrayList<>();

	public Order(Customer customer, LocalDate date) {
		this.customer = customer;
		this.date = date;
		isPaid = false;

		if (this.customer != null) {
			customer.addOrder(this);
		}
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}

	public void addWareHouseObserver(WareHouseObserver observer) {
		wareHouseObservers.add(observer);
	}

	public void removeWareHouseObserver(WareHouseObserver observer) {
		wareHouseObservers.remove(observer);
	}

	private void updateObservers() {
		for (WareHouseObserver who : wareHouseObservers) {
			who.update(this);
		}
	}

	public double calcDeposit() {
		double sum = 0;
		for (OrderLine ol : this.getOrderLines()) {
			if (ol.getReservation() == null) {
				if (ol.getProduct().getType().toLowerCase().toLowerCase().compareTo("Fustage".toLowerCase()) == 0)
					sum += 200 * ol.getAmount();
				if (ol.getProduct().getType().compareTo("Kulsyre") == 0)
					sum += 1000 * ol.getAmount();
			}
		}
		deposit = sum;
		return deposit;

	}

	public void setDepositPaid(boolean depositPaid) {
		this.depositPaid = depositPaid;
	}

	public boolean isDepositPaid() {
		return depositPaid;
	}

	public OrderLine createOrderLine(Product product, int amount, PriceList priceList) {
		OrderLine orderLine = new OrderLine(product, amount, priceList);
		orderLines.add(orderLine);
		return orderLine;
	}

	public OrderLine createOrderLineReservation(Reservation reservation) {
		OrderLine orderline = new OrderLine(reservation);
		orderLines.add(orderline);
		return orderline;
	}

	@Override
	public String toString() {
		if (isPaid) {
			return "(Betalt) " + this.date.toString() + " " + this.calcTotalPrice();
		} else {
			return this.date.toString() + " " + this.calcPrice();
		}
	}

	public void removeOrderLine(OrderLine orderLine) {
		orderLines.remove(orderLine);
	}

	public Payment createPayment(double amount, PaymentMethod paymentMethod, LocalDate date) {
		Payment payment = new Payment(this, amount, paymentMethod, date);
		payments.add(payment);

		if (payment.getPaymentMethod() == PaymentMethod.KLIP) {
			payment.setClip((int) amount);
			if (this.getHighestOrderLineOfType("Fadøl") != null)
				payment.setAmount(this.getHighestOrderLineOfType("Fadøl").getPrice() * amount);
			else
				payment.setAmount(0);
		}
		return payment;
	}

	private OrderLine getHighestOrderLineOfType(String type) {
		List<OrderLine> newProducts = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getProduct().getType().compareTo(type) == 0) {
				newProducts.add(orderLine);
			}
		}

		OrderLine product = null;

		for (OrderLine orderLine : newProducts) {
			if (product != null) {

				if (orderLine.getPrice() > product.getPrice()) {
					product = orderLine;
				}
			} else {
				product = orderLine;
			}
		}

		return product;
	}

	/**
	 * You can't get a discount on reservations
	 * 
	 * @param amount
	 */
	public void discountFullPrice(double amount) {
		for (OrderLine order : orderLines) {
			if (order.getPrice() * order.getAmount() >= amount) {
				order.giveDiscountWholeAmount(amount / order.getAmount());
				amount = 0;
				break;
			}
		}

	}

	/**
	 * Returns the value that has to be paid minus the deposit if there is one and
	 * if it's paid
	 * 
	 * @return
	 */
	public double calcTotalPrice() {
		double sum = 0.0;
		for (OrderLine orderLine : orderLines) {
			sum += orderLine.getPrice() * orderLine.getAmount();
		}

		if (depositPaid) {
			return sum - this.calcDeposit();
		}

		return sum;
	}

	/**
	 * Returns the value that has been paid
	 * 
	 * @return double
	 */
	public double calcPrice() {
		double sum = this.calcTotalPrice();

		for (Payment pay : payments) {
			sum -= pay.getAmount();
		}

		if (sum <= 0 && payments.size() > 0) {
			isPaid = true;
			updateWarehouse();
			return sum;
		}

		return sum;
	}

	private void updateWarehouse() {
		if (isPaid && !isUsed) {
			isUsed = true;
			updateObservers();
		}
	}

	public LocalDate getDate() {
		return date;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public Customer getCustomer() {
		return customer;
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public double calcRemainingPayment() {
		return this.calcPrice();
	}

}
