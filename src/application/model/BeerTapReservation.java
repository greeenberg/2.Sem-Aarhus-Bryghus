package application.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BeerTapReservation implements Reservation, Serializable {

	private LocalDate startDate;
	private LocalDate endDate;
	private double dayPrice;
	private BeerTap beerTap;
	private OrderLine orderline;

	BeerTapReservation(LocalDate startDate, LocalDate endDate, double dayPrice) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.dayPrice = dayPrice;
	}

	void setBeerTap(BeerTap beerTap) { // Package visibility
		this.beerTap = beerTap;
	}

	public OrderLine getOrder() {
		return orderline;
	}

	@Override
	public void setOrderLine(OrderLine orderline) {
		this.orderline = orderline;

	}

	public BeerTap getBeerTap() {
		return beerTap;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public int getAmountDays() {
		return (int) ChronoUnit.DAYS.between(startDate, endDate);
	}

	@Override
	public LocalDate getDate() {
		return startDate;
	}

	@Override
	public double getPrice() {
		return dayPrice * getAmountDays();
	}

	@Override
	public String toString() {
		return "Anlæg: " + beerTap.getName() + " (" + startDate.toString() + " - " + endDate.toString() + ")";
	}

}
