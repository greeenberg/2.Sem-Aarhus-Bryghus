package gui.panes;

import java.util.ArrayList;
import java.util.List;

import application.model.Customer;
import application.model.Order;
import application.model.Reservation;
import application.service.Service;
import gui.dialog.CreateCustomerDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CustomerIndexPane extends GridPane {
	private final Controller controller = new Controller();
	Service service = new Service();

	public CustomerIndexPane() {
		this.initContent();
	}

	// -------------------------------------------------------------------------

	private final TextField txfSearch = new TextField();
	private final Button btnSearch = new Button("Søg");
	private final Button btnNewCustomer = new Button("Opret kunde");
	private final ListView<Customer> lvwCustomers = new ListView<>();
	private final TextArea txaReservations = new TextArea();
	private final TextArea txaOrders = new TextArea();

	public void initContent() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(10);
		this.setHgap(10);

		this.add(txfSearch, 0, 0);
		txfSearch.setPromptText("Søg");
		txfSearch.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				lvwCustomers.getItems().setAll(service.getAllCustomers(txfSearch.getText().trim()));
			}

		});

		this.add(btnSearch, 1, 0);
		btnSearch.setOnAction(event -> controller.SearchAction());

		Label lblCustomer = new Label("Kunder");
		lblCustomer.setFont(new Font(20));
		this.add(lblCustomer, 0, 1);

		this.add(lvwCustomers, 0, 2, 2, 1);
		ChangeListener<Customer> listenerCustomer = (ov, o, n) -> controller.getInfo();
		lvwCustomers.getSelectionModel().selectedItemProperty().addListener(listenerCustomer);

		Label lblReservation = new Label("Kunde info");
		lblReservation.setFont(new Font(20));
		this.add(lblReservation, 2, 1);

		this.add(txaReservations, 2, 2, 2, 1);
		txaReservations.setMaxWidth(200);
		txaReservations.setWrapText(true);
		txaReservations.setEditable(false);

		this.add(txaOrders, 4, 2, 2, 1);
		txaOrders.setMaxWidth(200);
		txaOrders.setWrapText(true);
		txaOrders.setEditable(false);

		this.add(btnNewCustomer, 1, 3);
		btnNewCustomer.setOnAction(event -> controller.createCustomer());

		controller.fillCustomer();
	}

	// ----------------------------------------------------------------------------

	public void updateControls() {
		controller.updateControls();
	}

	// -----------------------------------------------------------------------------

	private class Controller {
		CreateCustomerDialog createCustomerDialog;

		public void updateControls() {

		}

		public void fillCustomer() {
			lvwCustomers.getItems().setAll(service.getAllCustomers());
			if (lvwCustomers.getItems().size() > 0) {
				lvwCustomers.getSelectionModel().select(0);
			}
		}

		public void createCustomer() {
			if (createCustomerDialog == null) {
				createCustomerDialog = new CreateCustomerDialog("Opret ny kunde");
				Stage stage = (Stage) lvwCustomers.getScene().getWindow();
				createCustomerDialog.initOwner(stage);
			}

			createCustomerDialog.showAndWait();

			boolean isCreated = createCustomerDialog.getResult();
			if (isCreated) {
				lvwCustomers.getItems().setAll(service.getAllCustomers());
				int index = lvwCustomers.getItems().size() - 1;
				lvwCustomers.getSelectionModel().select(index);

			}
		}

		public void SearchAction() {
			String search = txfSearch.getText().trim();

			this.SearchFunction(lvwCustomers.getItems(), search);
		}

		// helper method to searchAction
		private void SearchFunction(List<Customer> list, String target) {
			List<Customer> customers = new ArrayList<>();
			for (Customer customer : list) {
				if (customer.getName().contains(target)) {
					customers.add(customer);

				} else if (target.contains(customer.getPhoneNr() + "")) {
					customers.add(customer);
				}
			}

			lvwCustomers.getItems().setAll(customers);
			txfSearch.clear();
		}

		private void getInfo() {
			txaOrders.setText(this.getOrderInfo());
			txaReservations.setText(this.getReservationInfo());
		}

		private String getReservationInfo() {
			Customer customer = lvwCustomers.getSelectionModel().getSelectedItem();
			if (customer != null) {
				StringBuilder sb = new StringBuilder();
				for (Reservation r : service.getCustomerReservations(customer)) {
					sb.append("Reservation: \n" + r.toString() + "\n");
				}
				return sb.toString();
			} else {
				return "";
			}
		}

		private String getOrderInfo() {
			Customer customer = lvwCustomers.getSelectionModel().getSelectedItem();
			if (customer != null) {
				StringBuilder sb = new StringBuilder();
				for (Order order : service.getCustomerOrders(customer)) {
					sb.append("Orders: \n" + order.toString() + "\n");
				}
				return sb.toString();
			} else {
				return "";
			}
		}
	}
}
