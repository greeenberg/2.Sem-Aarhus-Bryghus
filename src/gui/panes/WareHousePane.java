package gui.panes;

import application.model.WareHouseCapacity;
import application.service.Service;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class WareHousePane extends GridPane {

	private final Service service = new Service();
	private final Controller controller = new Controller();

	public WareHousePane() {
		this.initContent();
	}

	public void updateControls() {
		controller.fillListView();

	}

	private final TextField txfAmount = new TextField();
	private final ListView<WareHouseCapacity> lvwProducts = new ListView<>();

	private void initContent() {
		this.setPadding(new Insets(20, 20, 20, 20));
		this.setHgap(10);
		this.setVgap(10);
		// this.setGridLinesVisible(true);

		Label lblHeadLine = new Label("Lager");
		lblHeadLine.setFont(new Font(30));
		this.add(lblHeadLine, 0, 0);
		this.add(lvwProducts, 0, 1);
		this.add(txfAmount, 0, 2);
		txfAmount.setPromptText("Antal");
		txfAmount.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					txfAmount.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}

		});
		HBox boxButtons = new HBox(10);
		this.add(boxButtons, 0, 3);

		Button btnAdd = new Button("Tilføj");
		Button btnRemove = new Button("Fjern");
		boxButtons.getChildren().addAll(btnAdd, btnRemove);
		btnAdd.setOnAction(Event -> controller.addAction());
		btnRemove.setOnAction(Event -> controller.removeAction());
	}

	private class Controller {

		private void addAction() {
			WareHouseCapacity prod = lvwProducts.getSelectionModel().getSelectedItem();
			if (prod != null) {
				WareHouseCapacity whc = null;
				for (WareHouseCapacity newwhc : service.getWareHouse().getWareHouseCapacity()) {
					if (newwhc.getProduct().compareTo(prod.getProduct()) == 0) {
						whc = newwhc;
						break;
					}
				}
				whc.addToStock(Integer.parseInt(txfAmount.getText()));
				updateControls();
			}
		}

		private void removeAction() {
			WareHouseCapacity prod = lvwProducts.getSelectionModel().getSelectedItem();
			if (prod != null) {
				WareHouseCapacity whc = null;
				for (WareHouseCapacity newwhc : service.getWareHouse().getWareHouseCapacity()) {
					if (newwhc.getProduct().compareTo(prod.getProduct()) == 0) {
						whc = newwhc;
						break;
					}
				}
				whc.decrementToStock(Integer.parseInt(txfAmount.getText()));
				updateControls();
			}
		}

		private void fillListView() {
			lvwProducts.getItems().setAll(service.getWareHouse().getWareHouseCapacity());
		}
	}
}
