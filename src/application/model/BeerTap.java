package application.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BeerTap extends Product {

	private final int amountTaps;
	private final List<BeerTapReservation> reservations = new ArrayList<>();

	public BeerTap(String name, String type, int amountTaps) {
		super(name, type);
		this.amountTaps = amountTaps;
	}

	public List<BeerTapReservation> getReservations() {
		return new ArrayList<>(reservations);
	}

	public Reservation createBeerTapReservation(LocalDate startDate, LocalDate endDate, double dayPrice) {
		BeerTapReservation reservation = new BeerTapReservation(startDate, endDate, dayPrice);
		reservation.setBeerTap(this);
		reservations.add(reservation);
		return reservation;
	}

	public int getAmountTaps() {
		return amountTaps;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
