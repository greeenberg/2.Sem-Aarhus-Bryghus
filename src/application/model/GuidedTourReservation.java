package application.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GuidedTourReservation implements Reservation, Serializable {

	private final LocalDateTime reservationTime;
	private final int amountPeople;
	private final double pricePerPerson;
	private OrderLine orderline;

	public GuidedTourReservation(LocalDateTime reservationTime, int amountPeople, double pricePerPerson) {
		this.reservationTime = reservationTime;
		this.amountPeople = amountPeople;
		this.pricePerPerson = pricePerPerson;
	}

	public LocalDateTime getReservationTime() {
		return reservationTime;
	}

	public int getAmountPeople() {
		return amountPeople;
	}

	public OrderLine getOrder() {
		return orderline;
	}

	@Override
	public void setOrderLine(OrderLine orderline) {
		this.orderline = orderline;
	}

	/** @return The price for the entire GuidedTour */
	@Override
	public double getPrice() {
		return amountPeople * pricePerPerson;
	}

	/** @return the price for a single person */
	public double getPricePerPerson() {
		return pricePerPerson;
	}

	@Override
	public LocalDate getDate() {
		return reservationTime.toLocalDate();
	}

	@Override
	public String toString() {
		return "Rundvisning: ( d. " + reservationTime.toLocalDate().toString() + " kl. "
				+ reservationTime.toLocalTime().toString() + ", " + amountPeople + " pers.  pris pr pers. "
				+ pricePerPerson + " )";
	}

}
