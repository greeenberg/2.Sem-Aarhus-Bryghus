package gui.dialog;

import application.model.Order;
import application.model.OrderLine;
import application.service.Service;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SuspendedOrderDialog extends Stage {
	private final Controller controller = new Controller();

	public SuspendedOrderDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	private final ListView<Order> lvwsuspendedOrders = new ListView<>();
	private final ListView<OrderLine> lvworderitems = new ListView<>();
	private final Button btnSelect = new Button("Vælg Ordre");

	public void initContent(GridPane pane) {
		Platform.runLater(() -> pane.requestFocus());

		pane.setPadding(new Insets(20));

		pane.add(lvwsuspendedOrders, 0, 1);
		pane.add(new Label("Ubetalte Ordre"), 0, 0);
		pane.add(lvworderitems, 2, 1);
		pane.add(new Label("Ordrens indhold"), 2, 0);

		pane.add(btnSelect, 0, 2);

		controller.findSuspended();

		btnSelect.setOnAction(event -> controller.selectThisOrder());

		ChangeListener<Order> listener1 = (ov, o, n) -> controller.selectedOrder();
		lvwsuspendedOrders.getSelectionModel().selectedItemProperty().addListener(listener1);

	}

	// -------------------------------------------------------------------------

	public Order getResult() {
		return controller.result;
	}

	public void updateDialog() {
		lvwsuspendedOrders.getItems().clear();
		controller.findSuspended();
	}

	private class Controller {
		Service service = new Service();
		private Order result;

		public void selectThisOrder() {
			result = lvwsuspendedOrders.getSelectionModel().getSelectedItem();
			lvworderitems.getItems().clear();
			SuspendedOrderDialog.this.close();
		}

		public void selectedOrder() {
			if (lvwsuspendedOrders.getSelectionModel().getSelectedItem() != null) {
				lvworderitems.getItems().clear();
				lvworderitems.getItems()
						.addAll(lvwsuspendedOrders.getSelectionModel().getSelectedItem().getOrderLines());
			} else
				return;

		}

		public void findSuspended() {
			for (Order order : service.getAllOrders()) {
				if (!order.isPaid()) {
					lvwsuspendedOrders.getItems().add(order);
				}
			}
		}

	}
}
