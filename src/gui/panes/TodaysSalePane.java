package gui.panes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.model.Order;
import application.model.OrderLine;
import application.model.PriceList;
import application.model.Product;
import application.model.Reservation;
import application.service.Service;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class TodaysSalePane extends GridPane {
	private final Controller controller = new Controller();

	public TodaysSalePane() {
		this.initContent();
	}

	// ----------------------------------------------------------------------------------------------
	private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	private final PieChart chart = new PieChart(pieChartData);
	private final Label lblCaption = new Label();
	private final StackPane paneType = new StackPane();

	private final ObservableList<PieChart.Data> pieChartDataAllProducts = FXCollections.observableArrayList();
	private final PieChart chartAllProducts = new PieChart(pieChartDataAllProducts);
	private final Label lblCaptionAllProducts = new Label();
	private final StackPane paneAllProducts = new StackPane();

	private final ComboBox<String> cbbPricelist = new ComboBox<>();

	public void initContent() {
		this.add(cbbPricelist, 0, 0);
		ChangeListener<String> listenerPriceList = (ov, o, n) -> controller.fillAllCharts();
		cbbPricelist.getSelectionModel().selectedItemProperty().addListener(listenerPriceList);

		// ---------------------------------------------------------------------------------------
		controller.fillChartTypes();
		chart.setTitle("Dagens salg af typer");
		this.add(paneType, 0, 1);
		paneType.getChildren().add(chart);
		paneType.getChildren().add(lblCaption);
		lblCaption.setTextFill(Color.BLACK);
		lblCaption.setStyle("-fx-font: 24 arial;");

		// -------------------------------------------------------------------------------------

		chartAllProducts.setTitle("Dagens salg af produkter");
		controller.fillChartAllProducts();
		this.add(paneAllProducts, 1, 1);
		paneAllProducts.getChildren().add(chartAllProducts);
		paneAllProducts.getChildren().add(lblCaptionAllProducts);
		lblCaptionAllProducts.setTextFill(Color.BLACK);
		lblCaptionAllProducts.setStyle("-fx-font: 15 arial");

		// -------------------------------------------------------------------------------------------

		controller.updateControls();
	}

	// -----------------------------------------------------------------------------------------------

	public void updateControls() {
		controller.updateControls();
	}

	// ------------------------------------------------------------------------------------------------

	private class Controller {
		private final Service service = new Service();

		public void updateControls() {
			cbbPricelist.getItems().setAll(this.fillComboBox());
			cbbPricelist.getItems().addAll("All", "Reservation");
		}

		private List<String> fillComboBox() {
			List<String> list = new ArrayList<>();
			for (PriceList pricelist : service.getAllPricelists()) {
				list.add(pricelist.getName());
			}
			return list;
		}

		public void clickTypes() {
			for (final PieChart.Data data : chart.getData()) {
				data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						lblCaption.setTranslateX(e.getX());
						lblCaption.setTranslateY(e.getY());
						lblCaption.setText(String.valueOf(data.getPieValue()) + " stk");
					}
				});
			}

		}

		private void fillAllCharts() {
			this.fillChartTypes();
			this.fillChartAllProducts();
		}

		private void allSalesTypes() {
			Set<String> types = new HashSet<>();
			List<String> reservations = new ArrayList<>();

			for (Order order : service.getOrdersInPeriod(LocalDate.now(), LocalDate.now())) {
				for (OrderLine orderline : order.getOrderLines()) {
					if (orderline.getReservation() == null) {
						types.add(orderline.getProduct().getType().trim());
					} else {
						reservations.add("Reservation");
					}
				}
			}

			for (String type : types) {
				int amount = service.getAmountSoldByTypeInPeriod(type, LocalDate.now(), LocalDate.now());
				pieChartData.add(new PieChart.Data(type, amount));
			}
			if (reservations.size() > 0) {
				pieChartData.add(new PieChart.Data("Reservation", reservations.size()));
			}
		}

		public void fillChartTypes() {
			pieChartData.clear();
			String pricelist = cbbPricelist.getSelectionModel().getSelectedItem();
			Set<String> types = new HashSet<>();
			List<String> reservations = new ArrayList<>();

			if (pricelist != null && pricelist.compareTo("All") != 0) {
				for (Order order : service.getOrdersInPeriod(LocalDate.now(), LocalDate.now())) {
					for (OrderLine orderline : order.getOrderLines()) {
						if (orderline.getReservation() == null
								&& orderline.getPricelist().getName().compareTo(pricelist) == 0) {
							types.add(orderline.getProduct().getType().trim());
						} else if (pricelist.compareTo("Reservation") == 0) {
							reservations.add("Reservation");
						}
					}
				}

				for (String type : types) {
					int amount = service.getAmountSoldByTypeInPeriod(type, LocalDate.now(), LocalDate.now());
					pieChartData.add(new PieChart.Data(type, amount));
				}
				if (reservations.size() > 0) {
					pieChartData.add(new PieChart.Data("Reservation", reservations.size()));
				}
			} else if (pricelist != null && pricelist.compareTo("All") == 0) {
				this.allSalesTypes();
			}
			this.clickTypes();
		}

		public void allSalesProducts() {
			Set<Product> products = new HashSet<>();
			List<Reservation> guidedReservations = new ArrayList<>();
			List<Reservation> beerTapReservations = new ArrayList<>();

			for (Order order : service.getOrdersInPeriod(LocalDate.now(), LocalDate.now())) {
				for (OrderLine orderline : order.getOrderLines()) {
					if (orderline.getReservation() == null) {
						products.add(orderline.getProduct());
					} else if (orderline.getReservation() != null
							&& orderline.getReservation().toString().contains("Rundvisning")) {
						guidedReservations.add(orderline.getReservation());
					} else {
						beerTapReservations.add(orderline.getReservation());
					}
				}
			}

			for (Product product : products) {
				int amount = service.getAmountSoldByProductsInPeriod(product, LocalDate.now(), LocalDate.now());
				pieChartDataAllProducts.add(new PieChart.Data(product + "", amount));
			}

			if (guidedReservations.size() > 0) {
				pieChartDataAllProducts.add(new PieChart.Data("Rundvisning", guidedReservations.size()));
			}

			if (beerTapReservations.size() > 0) {
				pieChartDataAllProducts.add(new PieChart.Data("Anlæg", beerTapReservations.size()));
			}
		}

		public void fillChartAllProducts() {
			pieChartDataAllProducts.clear();
			String pricelist = cbbPricelist.getSelectionModel().getSelectedItem();
			Set<Product> products = new HashSet<>();
			List<Reservation> guidedReservations = new ArrayList<>();
			List<Reservation> beerTapReservations = new ArrayList<>();

			if (pricelist != null && pricelist.compareTo("All") != 0) {
				for (Order order : service.getOrdersInPeriod(LocalDate.now(), LocalDate.now())) {
					for (OrderLine orderline : order.getOrderLines()) {
						if (orderline.getReservation() == null
								&& orderline.getPricelist().getName().compareTo(pricelist) == 0) {
							products.add(orderline.getProduct());
						} else if (orderline.getReservation() != null
								&& orderline.getReservation().toString().contains("Rundvisning")
								&& pricelist.compareTo("Reservation") == 0) {
							guidedReservations.add(orderline.getReservation());
						} else if (pricelist.compareTo("Reservation") == 0) {
							beerTapReservations.add(orderline.getReservation());
						}
					}
				}

				for (Product product : products) {
					int amount = service.getAmountSoldByProductsInPeriod(product, LocalDate.now(), LocalDate.now());
					pieChartDataAllProducts.add(new PieChart.Data(product + "", amount));
				}

				if (guidedReservations.size() > 0) {
					pieChartDataAllProducts.add(new PieChart.Data("Rundvisning", guidedReservations.size()));
				}

				if (beerTapReservations.size() > 0) {
					pieChartDataAllProducts.add(new PieChart.Data("Anlæg", beerTapReservations.size()));
				}
			} else if (pricelist != null && pricelist.compareTo("All") == 0) {
				this.allSalesProducts();
			}
			clickAllProducts();
		}

		public void clickAllProducts() {
			for (final PieChart.Data data : chartAllProducts.getData()) {
				data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						lblCaptionAllProducts.setTranslateX(e.getX());
						lblCaptionAllProducts.setTranslateY(e.getY());
						lblCaptionAllProducts.setText(String.valueOf(data.getPieValue()) + " stk");
					}
				});
			}
		}
	}

}
