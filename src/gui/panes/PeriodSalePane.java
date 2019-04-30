package gui.panes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.model.Order;
import application.model.OrderLine;
import application.model.Product;
import application.model.Reservation;
import application.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PeriodSalePane extends GridPane {
	private final Controller controller = new Controller();

	public PeriodSalePane() {
		this.initContent();
	}

	// -------------------------------------------------------------------------------------------

	private final DatePicker startDate = new DatePicker();
	private final DatePicker endDate = new DatePicker();
	private final Button btnSearch = new Button("Søg");
	private final HBox dateBox = new HBox();

	private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	private final PieChart chart = new PieChart(pieChartData);
	private final Label lblCaption = new Label();
	private final StackPane pane1 = new StackPane();

	private final ObservableList<PieChart.Data> pieChartDataAllProducts = FXCollections.observableArrayList();
	private final PieChart chartAllProduct = new PieChart(pieChartDataAllProducts);
	private final Label lblCaptionAllProduct = new Label();
	private final StackPane paneProducts = new StackPane();

	public void initContent() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(10);
		this.setHgap(10);

		Label lblHeadLine = new Label("Salg i periode");
		lblHeadLine.setFont(new Font(35));
		this.add(lblHeadLine, 0, 0);
		lblHeadLine.setAlignment(Pos.CENTER);

		this.add(dateBox, 0, 1, 2, 1);
		dateBox.setSpacing(10);
		dateBox.getChildren().addAll(startDate, endDate, btnSearch);

		startDate.setPromptText("Vælg start dato");
		endDate.setPromptText("Vælg slut dato");

		btnSearch.setOnAction(event -> controller.fillCharts());

		// ----------------------------------------------------

		this.add(pane1, 0, 2);
		pane1.getChildren().add(chart);
		pane1.getChildren().add(lblCaption);
		lblCaption.setTextFill(Color.BLACK);
		lblCaption.setStyle("-fx-font: 24 arial;");

		// ------------------------------------------------------

		this.add(paneProducts, 1, 2);
		paneProducts.getChildren().addAll(chartAllProduct, lblCaptionAllProduct);
		lblCaptionAllProduct.setTextFill(Color.BLACK);
		lblCaptionAllProduct.setStyle("-fx-font: 24 arial;");
	}

	// -------------------------------------------------------------------------------------------

	public void updateControls() {
		controller.updateControls();
	}

	// -------------------------------------------------------------------------------------------

	private class Controller {
		private Service service = new Service();

		public void updateControls() {

		}

		public void fillCharts() {
			this.fillChartType();
			this.fillChartAllProducts();
		}

		public void fillChartType() {
			if (pieChartData != null) {
				pieChartData.clear();
			}
			Set<String> types = new HashSet<>();
			List<Reservation> reservations = new ArrayList<>();

			chart.setTitle("Typer");
			LocalDate start = startDate.getValue();
			LocalDate end = endDate.getValue();

			for (Order order : service.getOrdersInPeriod(start, end)) {
				for (OrderLine orderline : order.getOrderLines()) {
					if (orderline.getReservation() == null) {
						types.add(orderline.getProduct().getType().trim());
					} else {
						reservations.add(orderline.getReservation());
					}
				}
			}

			for (String type : types) {
				int amount = service.getAmountSoldByTypeInPeriod(type, start, end);
				pieChartData.add(new PieChart.Data(type, amount));
			}
			if (reservations.size() > 0) {
				pieChartData.add(new PieChart.Data("Reservationer", reservations.size()));
			}
			this.clickTypes();
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

		// ----------------------------------------------------------------------------------------------------------

		public void fillChartAllProducts() {
			Set<Product> products = new HashSet<>();
			List<Reservation> guidedReservations = new ArrayList<>();
			List<Reservation> beerTapReservations = new ArrayList<>();

			chartAllProduct.setTitle("Produkter");

			for (Order order : service.getOrdersInPeriod(LocalDate.now(), LocalDate.now())) {
				for (OrderLine orderline : order.getOrderLines()) {
					if (orderline.getReservation() == null) {
						products.add(orderline.getProduct());
					} else if (orderline.getReservation().toString().contains("Rundvisning")) {
						guidedReservations.add(orderline.getReservation());
					} else {
						beerTapReservations.add(orderline.getReservation());
					}
				}
			}

			for (Product product : products) {
				int amount = service.getAmountSoldByProductsInPeriod(product, LocalDate.now(), LocalDate.now());
				pieChartDataAllProducts.add(new PieChart.Data(product.getName(), amount));
			}

			if (guidedReservations.size() > 0) {
				pieChartDataAllProducts.add(new PieChart.Data("Rundvisning", guidedReservations.size()));
			}

			if (beerTapReservations.size() > 0) {
				pieChartDataAllProducts.add(new PieChart.Data("Anlæg", beerTapReservations.size()));
			}
			this.clickAllProducts();
		}

		public void clickAllProducts() {
			for (final PieChart.Data data : chartAllProduct.getData()) {
				data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						lblCaptionAllProduct.setTranslateX(e.getX());
						lblCaptionAllProduct.setTranslateY(e.getY());
						lblCaptionAllProduct.setText(String.valueOf(data.getPieValue()) + " stk");
					}
				});
			}
		}
	}

}
