package gui.panes;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import application.model.Customer;
import application.model.Gift;
import application.model.Order;
import application.model.OrderLine;
import application.model.Price;
import application.model.PriceList;
import application.model.Product;
import application.service.Service;
import gui.dialog.CreateCustomerDialog;
import gui.dialog.OrderGiftThenAddBeerDialog;
import gui.dialog.PaymentDialog;
import gui.dialog.SuspendedOrderDialog;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SalePane extends GridPane {
	private final Controller controller = new Controller();

	public SalePane() {
		this.initContent();
	}

	// -------------------------------------------------------

	private final ComboBox<PriceList> cbbPriceLists = new ComboBox<>();
	private final ComboBox<String> cmbType = new ComboBox<>();
	private final ComboBox<Customer> cbbCustomer = new ComboBox<>();

	private final ListView<Product> lvwProducts = new ListView<>();
	private final ListView<OrderLine> lvwItems = new ListView<>();

	private final TextField txfAmount = new TextField();
	private final TextField txfDueAmount = new TextField();
	private final TextField txfDiscount = new TextField();
	private final TextField txfDeposit = new TextField();

	private final VBox buttonPane = new VBox(10);

	private final HBox paymentPane = new HBox(5);
	private final HBox productPane = new HBox(5);
	private final HBox depositPane = new HBox(5);

	private final Button btnAdd = new Button("Tilføj");
	private final Button btnRemove = new Button("Fjern");
	private final Button btnPay = new Button("Betal");
	private final Button btnDiscount = new Button("Rabat");
	private final Button btnPause = new Button("Udskyd ordre");
	private final Button btnsuspends = new Button("Udskudte ordrer");
	private final Button btnCreateCustomer = new Button("Opret kunde");
	// private final Button btnPayDeposit = new Button("Betal Pant");

	private final CheckBox cbxDeposit = new CheckBox("Pant Betalt");

	private final Label lblAmount = new Label("Antal");
	private final Label lblPaid = new Label("Betalt");

	private void initContent() {
		this.setPadding(new Insets(20, 20, 20, 20));
		this.setHgap(10);
		this.setVgap(10);

		this.add(cbbPriceLists, 0, 0);
		cbbPriceLists.setPromptText("Vælg prisliste");
		controller.fillPriceListComboBox();
		ChangeListener<PriceList> listenerPriceList = (ov, o, n) -> controller.fillTypeComboBox();
		cbbPriceLists.getSelectionModel().selectedItemProperty().addListener(listenerPriceList);

		this.add(cmbType, 0, 1);
		cmbType.setPromptText("Vælg type");

		this.add(lvwProducts, 0, 2, 1, 3);

		this.add(buttonPane, 1, 3);
		buttonPane.getChildren().add(lblAmount);
		buttonPane.getChildren().add(txfAmount);
		buttonPane.getChildren().add(btnAdd);
		buttonPane.getChildren().add(btnRemove);
		buttonPane.setAlignment(Pos.CENTER);
		txfAmount.setText("1");

		this.add(cbbCustomer, 1, 5);
		cbbCustomer.setPrefWidth(90);
		controller.fillCostumerComboBox();
		VBox box1 = new VBox(10);
		this.add(box1, 1, 6);
		box1.getChildren().add(btnCreateCustomer);
		box1.getChildren().add(btnPause);
		this.add(btnsuspends, 0, 5);

		txfAmount.setMaxWidth(40);

		lvwProducts.setPrefHeight(300);

		this.add(lvwItems, 2, 2, 1, 3);
		lvwItems.setPrefHeight(300);

		this.add(paymentPane, 2, 6);
		paymentPane.getChildren().add(txfDueAmount);
		txfDueAmount.setMaxWidth(100);
		txfDueAmount.setEditable(false);
		paymentPane.getChildren().add(btnPay);
		btnPay.setPrefWidth(60);
		paymentPane.setAlignment(Pos.CENTER_RIGHT);

		this.add(depositPane, 2, 7);
		depositPane.getChildren().add(txfDeposit);
		txfDeposit.setEditable(false);
		depositPane.getChildren().add(cbxDeposit);
		txfDeposit.setPrefWidth(80);
		cbxDeposit.setPrefWidth(80);
		depositPane.setAlignment(Pos.CENTER_RIGHT);

		this.add(productPane, 2, 5);
		productPane.getChildren().add(txfDiscount);
		productPane.getChildren().add(btnDiscount);
		btnDiscount.setPrefWidth(60);
		txfDiscount.setPrefWidth(100);
		productPane.setAlignment(Pos.CENTER_RIGHT);

		this.add(lblPaid, 2, 7);
		lblPaid.setStyle("-fx-text-fill: green");
		lblPaid.setVisible(false);

		ChangeListener<String> listener1 = (ov, o, n) -> controller.selectedTypeChanged();
		cmbType.getSelectionModel().selectedItemProperty().addListener(listener1);

		btnAdd.setOnAction(event -> controller.addAction());
		btnRemove.setOnAction(event -> controller.removeAction());
		btnPay.setOnAction(event -> controller.payment());
		btnDiscount.setOnAction(event -> controller.discountAction());
		btnPause.setOnAction(event -> controller.pauseAction());
		btnsuspends.setOnAction(event -> controller.suspendedOrders());
		cbxDeposit.setOnAction(event -> controller.depositAction());
		btnCreateCustomer.setOnAction(event -> controller.createCustomer());

	}

	public void updateControls() {
		controller.updateControls();
	}

	// ------------------------------Controller----------------------------------------------

	private class Controller {
		private Service service = new Service();
		private Order order;
		private PaymentDialog paymentDialog;
		private SuspendedOrderDialog suspendedOrderDialog;
		private CreateCustomerDialog createCustomerDialog;
		private OrderGiftThenAddBeerDialog orderGiftThenAddBeerDialog;

		public void updateControls() {
			this.fillCostumerComboBox();
			this.fillPriceListComboBox();
			this.fillTypeComboBox();
		}

		public void updateItems() {
			lvwItems.getItems().setAll(order.getOrderLines());
			txfDueAmount.setText(order.calcPrice() + "");
			txfDeposit.setText(order.calcDeposit() + "");
			if (this.order.isDepositPaid()) {
				cbxDeposit.setSelected(true);
			}
		}

		public void depositAction() {
			if (cbxDeposit.isSelected()) {
				order.setDepositPaid(true);
				this.updateItems();
			}

			else
				order.setDepositPaid(false);
			this.updateItems();
		}

		public void addAction() {

			if (order == null) {
				this.order = service.createOrder(null, LocalDate.now());
			}

			for (OrderLine orderLine : lvwItems.getItems()) {
				if (lvwProducts.getSelectionModel().getSelectedItem().compareTo(orderLine.getProduct()) == 0) {
					int temp = orderLine.getAmount();
					orderLine.setAmount(temp + Integer.parseInt(txfAmount.getText()));
					this.updateItems();
					return;
				}
			}

			service.createOrderLine(order, lvwProducts.getSelectionModel().getSelectedItem(),
					(int) Double.parseDouble(txfAmount.getText()), cbbPriceLists.getSelectionModel().getSelectedItem());
			this.updateItems();
			System.out.println(lvwProducts.getSelectionModel().getSelectedItem().getType());

			if (lvwProducts.getSelectionModel().getSelectedItem() instanceof Gift) {
				if (orderGiftThenAddBeerDialog == null) {
					orderGiftThenAddBeerDialog = new OrderGiftThenAddBeerDialog("Tilføj øl til sampakning", order);
					Stage stage = new Stage();
					orderGiftThenAddBeerDialog.initOwner(stage);
				}

				orderGiftThenAddBeerDialog.showAndWait();

				boolean isCreated = orderGiftThenAddBeerDialog.getResult();
				if (isCreated) {
					this.updateItems();
				}
			}

		}

		public void removeAction() {

			service.removeOrderLine(order, lvwItems.getSelectionModel().getSelectedItem());
			this.updateItems();

		}

		public void discountAction() {
			if (this.order == null)
				return;
			order.discountFullPrice(Double.parseDouble(txfDiscount.getText()));
			txfDueAmount.setText(order.calcTotalPrice() + "");
			lvwItems.getItems().setAll(order.getOrderLines());

		}

		public void pauseAction() {
			if (cbbCustomer.getSelectionModel().getSelectedItem() != null)
				order.setCustomer(cbbCustomer.getSelectionModel().getSelectedItem());
			this.orderFinishedAction();
			System.out.println(service.getAllOrders().get(service.getAllOrders().size() - 1));

		}

		public void selectedTypeChanged() {
			lvwProducts.getItems().clear();
			if (cmbType.getSelectionModel().getSelectedItem() == null) {
				return;
			}

			for (Price price : cbbPriceLists.getSelectionModel().getSelectedItem().getPrices()) {
				if (price.getProduct() != null) {
					if (price.getProduct().getType().toLowerCase()
							.compareTo(cmbType.getSelectionModel().getSelectedItem().toLowerCase()) == 0) {
						lvwProducts.getItems().add(price.getProduct());
					}
				}
			}
		}

		public void fillCostumerComboBox() {
			cbbCustomer.getItems().setAll(service.getAllCustomers());
		}

		public void fillPriceListComboBox() {
			cbbPriceLists.getItems().setAll(service.getAllPricelists());
		}

		public void fillTypeComboBox() {
			Set<String> types = new TreeSet<>();
			if (cbbPriceLists.getSelectionModel().getSelectedItem() != null) {
				for (Price price : cbbPriceLists.getSelectionModel().getSelectedItem().getPrices()) {
					types.add(price.getProduct().getType());
				}
			}
			cmbType.getItems().setAll(types);
		}

		public void orderFinishedAction() {
			lvwItems.getItems().clear();
			txfAmount.setText("1");
			txfDueAmount.clear();
			this.order = null;
			cbxDeposit.setSelected(false);
			txfDeposit.clear();
			cbbCustomer.getSelectionModel().clearSelection();
		}

		public void suspendedOrders() {
			if (suspendedOrderDialog == null) {
				suspendedOrderDialog = new SuspendedOrderDialog("Ubetalte Ordrer");
				Stage stage = new Stage();
				suspendedOrderDialog.initOwner(stage);
			}

			suspendedOrderDialog.updateDialog();
			suspendedOrderDialog.showAndWait();
			if (suspendedOrderDialog.getResult() != null) {
				order = suspendedOrderDialog.getResult();
				this.updateItems();
			}

		}

		// -----------------------------------Payment------------------------------------------------------------
		public void payment() {
			if (paymentDialog == null) {
				paymentDialog = new PaymentDialog("Betal");
				Stage stage = new Stage();
				paymentDialog.initOwner(stage);
			}

			paymentDialog.setOrder(order);

			paymentDialog.showAndWait();
			order.calcPrice();

			this.updateItems();
			if (order.isPaid() == true) {
				order.calcPrice();
				PauseTransition lblvisibility = new PauseTransition(Duration.millis(1500));
				lblvisibility.play();
				lblPaid.setVisible(true);
				this.orderFinishedAction();
				lblvisibility.setOnFinished(event -> lblPaid.setVisible(false));

			}

			boolean isCreated = paymentDialog.getResult();
			if (isCreated) {
				// Do something
			}
		}

		// -----------------------------------Customer-------------------------------------------

		public void createCustomer() {
			if (createCustomerDialog == null) {
				createCustomerDialog = new CreateCustomerDialog("Opret ny kunde");
				Stage stage = (Stage) cbbCustomer.getScene().getWindow();
				createCustomerDialog.initOwner(stage);
			}

			createCustomerDialog.showAndWait();

			boolean isCreated = createCustomerDialog.getResult();
			if (isCreated) {
				cbbCustomer.getItems().setAll(service.getAllCustomers());
				int index = cbbCustomer.getItems().size() - 1;
				cbbCustomer.getSelectionModel().select(index);

			}
		}

	}
}
