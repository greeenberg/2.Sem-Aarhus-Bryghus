package application.model;

import java.time.LocalDate;

public interface Reservation {

	public LocalDate getDate();

	public double getPrice();

	public void setOrderLine(OrderLine orderline);

}
