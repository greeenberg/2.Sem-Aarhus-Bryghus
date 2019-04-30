package gui.dialog;

import java.util.HashSet;
import java.util.Set;

import application.model.PriceList;
import application.model.Product;
import application.service.Service;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddCarbonDioxideDialog extends Stage {
	private final Controller controller = new Controller();

	public AddCarbonDioxideDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		this.getIcons().add(new Image("Kulsyre.png"));
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	private final TextField txfName = new TextField();
	private final TextField txfDeposit = new TextField();
	private final TextField txfWeight = new TextField();
	private final ComboBox<PriceList> cbbPricelists = new ComboBox<>();
	private final ListView<PriceList> lvwSelectedPriceList = new ListView<>();
	private final Button btnAdd = new Button("Tilføj");
	private final Button btnRemove = new Button("Fjern");
	private final Button btnOk = new Button("Accepter");
	private final Button btnCancel = new Button("Annuler");
	private final Label lblError = new Label();
	private final VBox boxTxfPrice = new VBox();

	public void initContent(GridPane pane) {
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());
		pane.setGridLinesVisible(false);

		HBox box1 = new HBox();
		pane.add(box1, 0, 0, 3, 1);
		box1.setSpacing(10);
		box1.getChildren().add(txfName);
		txfName.setPromptText("Navn");

		HBox box2 = new HBox();
		pane.add(box2, 0, 1, 3, 1);
		box2.setSpacing(10);
		box2.getChildren().add(txfWeight);
		txfWeight.setPromptText("Vægt");

		box2.getChildren().add(txfDeposit);
		txfDeposit.setPromptText("Pant");

		Label lblPriceList = new Label("Vælg prisliste(r)");
		pane.add(lblPriceList, 0, 2, 2, 1);
		lblPriceList.setFont(new Font(18));
		pane.add(cbbPricelists, 0, 3, 2, 1);
		cbbPricelists.setPromptText("Vælg Prisliste");
		cbbPricelists.setPrefWidth(150);

		pane.add(btnAdd, 0, 4);
		btnAdd.setOnAction(event -> controller.addPriceList());

		pane.add(btnRemove, 1, 4);
		btnRemove.setOnAction(event -> controller.removePriceList());

		pane.add(boxTxfPrice, 0, 5, 2, 1);

		pane.add(lvwSelectedPriceList, 2, 3, 1, 2);
		lvwSelectedPriceList.setPrefHeight(100);

		pane.add(lblError, 0, 6, 3, 1);
		lblError.setStyle("-fx-text-fill: red");

		HBox box3 = new HBox();
		pane.add(box3, 0, 7, 3, 1);
		box3.setAlignment(Pos.CENTER);
		box3.setSpacing(10);
		box3.getChildren().add(btnOk);
		box3.getChildren().add(btnCancel);
		btnOk.setOnAction(event -> controller.okAction());
		btnCancel.setOnAction(event -> controller.cancelAction());

		controller.fillComboBox();
	}

	// --------------------------------------------------------------
	public boolean getResult() {
		return controller.result;
	}

	// --------------------------------------------------------------

	private class Controller {
		Service service = new Service();
		private boolean result = false;
		private Set<TextField> txfFields = new HashSet<>();

		public void updateTextfields() {
			if (lvwSelectedPriceList.getItems() == null) {
				return;
			}

			txfFields.clear();
			boxTxfPrice.getChildren().clear();
			for (PriceList pricelist : lvwSelectedPriceList.getItems()) {
				if (pricelist != null) {
					TextField txf = new TextField();
					txf.setPromptText(pricelist.getName());
					boxTxfPrice.getChildren().add(txf);
					txfFields.add(txf);
					System.out.println(txfFields);
				}
			}
		}

		public void addPriceList() {
			PriceList priceList = cbbPricelists.getSelectionModel().getSelectedItem();
			if (priceList != null) {
				lvwSelectedPriceList.getItems().add(priceList);
				cbbPricelists.getItems().remove(priceList);
			}
			this.updateTextfields();
		}

		public void removePriceList() {
			PriceList priceList = lvwSelectedPriceList.getSelectionModel().getSelectedItem();
			if (priceList != null) {
				cbbPricelists.getItems().add(priceList);
				lvwSelectedPriceList.getItems().remove(priceList);
			}
			for (TextField txf : txfFields) {
				if (txf.getPromptText().compareTo(priceList.getName()) == 0) {
					txfFields.remove(txf);
					boxTxfPrice.getChildren().remove(txf);
				}
			}
		}

		public void fillComboBox() {
			cbbPricelists.getItems().setAll(service.getAllPricelists());
		}

		public void okAction() {
			String name = txfName.getText().trim();
			if (name.length() == 0) {
				lblError.setText("Udfyld navn");
				txfName.requestFocus();
				return;
			}

			double weight = -1;
			try {
				weight = Double.parseDouble(txfWeight.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (weight < 0) {
				lblError.setText("Kan ikke være bogstaver eller negative tal");
				txfWeight.requestFocus();
				return;
			}

			double deposit = -1;
			try {
				deposit = Double.parseDouble(txfDeposit.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (deposit < 0) {
				lblError.setText("Kan ikke være bogstaver eller negative tal");
				txfDeposit.requestFocus();
				return;
			}

			Product product = service.createCarbonicDioxide(name, deposit, weight);

			if (lvwSelectedPriceList.getItems().isEmpty() == false) {
				for (PriceList pricelist : lvwSelectedPriceList.getItems()) {

					double price = -1;
					for (TextField txf : txfFields) {
						if (txf.getPromptText().compareTo(pricelist.getName()) == 0) {
							try {
								price = Double.parseDouble(txf.getText().trim());
							} catch (NumberFormatException ex) {
								// do nothing
							}
						}
					}

					if (price < 0) {
						lblError.setText("Udfyld pris");
						return;
					}

					service.createPrice(pricelist, product, price);
				}

			} else {
				lblError.setText("Vælg en prisliste");
				return;
			}

			resetAction();
			AddCarbonDioxideDialog.this.close();
		}

		public void cancelAction() {
			resetAction();
			AddCarbonDioxideDialog.this.close();
		}

		private void resetAction() {
			txfName.clear();
			txfDeposit.clear();
			txfWeight.clear();
			cbbPricelists.getItems().clear();
			lvwSelectedPriceList.getItems().clear();
			boxTxfPrice.getChildren().clear();
			txfFields.clear();
			this.fillComboBox();
		}
	}
}
