package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PriceList implements Serializable {
	private String name;
	private List<Price> prices = new ArrayList<>();

	public PriceList(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addPrice(Price p) {
		prices.add(p);
	}

	public void removePrice(Price p) {
		prices.remove(p);
	}

	public List<Price> getPrices() {
		return new ArrayList<>(prices);
	}

	public String toString() {
		return getName();
	}

}
