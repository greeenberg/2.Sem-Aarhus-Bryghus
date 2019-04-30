package gui.panes;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import application.model.Order;
import application.model.OrderLine;
import application.model.Price;
import application.model.Product;
import application.service.Service;
import gui.dialog.PaymentDialog;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FridayPane extends GridPane {

	private final Controller controller = new Controller();

	public FridayPane() {
		this.initContent();
	}

	// -------------------------------------------------------------------------------------------

	private final ComboBox<String> cbbType = new ComboBox<>();
	private final VBox btnPane = new VBox(10);
	private final ListView<OrderLine> lvwProduct = new ListView<>();
	private final TextField txfFullPrice = new TextField();
	private final TextField txfProductPrice = new TextField();
	private final TextField txfReamingPrice = new TextField();
	private final Button btnProductDiscount = new Button("Produkt rabat");
	private final Button btnRemoveProduct = new Button("Fjern produkt");
	private final Button btnPayment = new Button("Betaling");
	private final Label lblPaid = new Label("Ordren er nu betalt");
	private final HBox box2 = new HBox();

	private void initContent() {
		this.setPadding(new Insets(20, 20, 20, 20));
		this.setHgap(10);
		this.setVgap(10);
		// this.setGridLinesVisible(true);

		Label lblTyper = new Label("Typer");
		this.add(lblTyper, 0, 0);
		this.add(cbbType, 0, 1);

		this.add(btnPane, 0, 3, 1, 6);
		ChangeListener<String> listener1 = (ov, o, n) -> controller.selectedTypeChanged();
		cbbType.getSelectionModel().selectedItemProperty().addListener(listener1);

		Label lblProduct = new Label("Kvittering");
		this.add(lblProduct, 6, 0);
		this.add(lvwProduct, 6, 1, 2, 5);
		ChangeListener<OrderLine> listener2 = (ov, o, n) -> controller.selectedProduct();
		lvwProduct.getSelectionModel().selectedItemProperty().addListener(listener2);

		Label lblFullPrice = new Label("Samlet Pris");
		this.add(lblFullPrice, 6, 7);
		this.add(txfFullPrice, 7, 7);
		txfFullPrice.setEditable(false);
		txfFullPrice.prefWidth(60);

		Label lblProductPrice = new Label("Produkt Pris");
		this.add(lblProductPrice, 6, 6);
		this.add(txfProductPrice, 7, 6);
		txfProductPrice.setEditable(true);
		txfProductPrice.prefWidth(60);

		this.add(btnProductDiscount, 8, 6);
		btnProductDiscount.setOnAction(event -> controller.productDiscount());

		this.add(btnRemoveProduct, 8, 0);
		btnRemoveProduct.setOnAction(event -> controller.removeProduct());

		HBox box1 = new HBox();
		this.add(box1, 7, 8);
		box1.getChildren().add(txfReamingPrice);
		box1.getChildren().add(btnPayment);
		box1.setAlignment(Pos.CENTER);
		btnPayment.setOnAction(event -> controller.payment());

		Label lblReamingPrice = new Label("Resterende beløb");
		this.add(lblReamingPrice, 6, 8);

		this.add(box2, 7, 9);
		box2.getChildren().add(lblPaid);
		lblPaid.setVisible(false);
		lblPaid.setStyle("-fx-text-fill: green");

		controller.fillComboBox();
	}

	public void updateControls() {
		controller.updateControls();
	}

	// -------------------------------------------------------------------------------------------------------

	private class Controller {
		private Service service = new Service();
		private Order order;
		private PaymentDialog paymentDialog;

		public void updateControls() {
			this.fillComboBox();
		}

		// TODO tjek null point
		public void productDiscount() {
			double newPrice = Double.parseDouble(txfProductPrice.getText().trim());
			lvwProduct.getSelectionModel().getSelectedItem().giveDiscountWholeAmount(newPrice);
			this.updateFullPrice();
			lvwProduct.getSelectionModel().select(0);
			this.selectedProduct();
			this.updateLvwProduct();
		}

		// this method remove a product from the product list
		public void removeProduct() {
			OrderLine orderline = lvwProduct.getSelectionModel().getSelectedItem();
			if (orderline != null) {
				lvwProduct.getItems().remove(orderline);
				service.removeOrderLine(order, orderline);
			}
		}

		public void selectedProduct() {
			System.out.println(lvwProduct.getSelectionModel().getSelectedItem());
			if (lvwProduct.getSelectionModel().getSelectedItem() != null) {
				txfProductPrice.setText(lvwProduct.getSelectionModel().getSelectedItem().getPrice() + "");
			}
		}

		// Fill the Vbox with buttons with product names
		public void selectedTypeChanged() {
			if (cbbType.getSelectionModel().getSelectedItem() == null) {
				return;
			}

			btnPane.getChildren().clear();
			for (Product product : service.getAllProducts()) {
				if (product != null) {
					if (product.getType().toLowerCase()
							.compareTo(cbbType.getSelectionModel().getSelectedItem().toLowerCase()) == 0) {
						Button btn = new Button(product.getName());
						btnPane.getChildren().add(btn);
						// btnPane.setMargin(btn, new Insets(5, 5, 5, 5));
						if (btn.isPressed()) {
							// this.product = btn.getText();
							System.out.println(btn.getText());
						}
						btn.setOnAction(event -> this.selectItem(product));
					}
				}
			}
		}

		// this create a order if the order is null, and a orderline, if the orderline
		// is made, it just chance the amount in orderline
		public void selectItem(Product product) {
			if (order == null) {
				this.order = service.createOrder(null, LocalDate.now());
			}
			for (OrderLine orderLine : lvwProduct.getItems()) {
				if (product.compareTo(orderLine.getProduct()) == 0) {
					int temp = orderLine.getAmount();
					orderLine.setAmount(temp + 1);
					this.updateLvwProduct();
					this.updateFullPrice();
					return;
				}
			}
			OrderLine ol = service.createOrderLine(order, product, 1, service.getAllPricelists().get(0));
			lvwProduct.getItems().add(ol);
			this.updateFullPrice();
		}

		// ----------------------PAYMENT-------------------------------------------------------------------------------------
		public void payment() {
			if (paymentDialog == null) {
				paymentDialog = new PaymentDialog("Betal");
				Stage stage = new Stage();
				paymentDialog.initOwner(stage);
			}

			paymentDialog.setOrder(order);

			paymentDialog.showAndWait();

			boolean isCreated = paymentDialog.getResult();
			if (isCreated) {
				controller.updateReamingPrice();
			}
		}

		// -------------------------------------------------------------------------------------------------

		// Update the textfield with full price for the whole order
		public void updateFullPrice() {
			txfFullPrice.setText(order.calcTotalPrice() + "");
		}

		public void orderFinished() {
			lvwProduct.getItems().clear();
			txfFullPrice.clear();
			txfProductPrice.clear();
			txfReamingPrice.clear();
			order = null;
		}

		public void updateReamingPrice() {
			txfReamingPrice.setText(order.calcPrice() + "");
			if (order.calcPrice() == 0) {
				PauseTransition lblvisibility = new PauseTransition(Duration.millis(1500));
				lblvisibility.play();
				lblPaid.setVisible(true);
				this.orderFinished();
				lblvisibility.setOnFinished(event -> lblPaid.setVisible(false));

			}
		}

		public void updateLvwProduct() {
			lvwProduct.getItems().setAll(order.getOrderLines());
		}

		public void fillComboBox() {
			Set<String> types = new TreeSet<>();
			for (Price price : service.getAllPricelists().get(0).getPrices()) {
				types.add(price.getProduct().getType());
			}

			cbbType.getItems().setAll(types);
		}
	}

}
