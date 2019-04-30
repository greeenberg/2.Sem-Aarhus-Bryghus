package gui.dialog;

import java.util.HashSet;
import java.util.Set;

import application.model.BeerKeg;
import application.model.PriceList;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddKegDialog extends Stage {
	private final Controller controller = new Controller();

	public AddKegDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		this.getIcons().add(new Image("Beer.png"));
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	// ------------------------------------------------------------------------------

	private final TextField txfName = new TextField();
	private final TextField txfDeposit = new TextField();
	private final TextField txfLitre = new TextField();
	private final ComboBox<PriceList> cbbPricelists = new ComboBox<>();
	private final ListView<PriceList> lvwSelectedPriceList = new ListView<>();
	private final Button btnPlus = new Button("Tiløj");
	private final Button btnMinus = new Button("Fjern");
	private final Button btnOk = new Button("Accepter");
	private final Button btnCancel = new Button("Annuller");
	private final VBox boxTxfPrice = new VBox(10);
	private final Label lblError = new Label();

	public void initContent(GridPane pane) {
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());

		pane.add(txfName, 0, 0);
		txfName.setPromptText("Navn");

		pane.add(txfLitre, 1, 0);
		txfLitre.setPromptText("Antal liter");

		pane.add(txfDeposit, 0, 1);
		txfDeposit.setPromptText("Pant");

		Label lblPriceLists = new Label("Vælg prisliste(r)");
		pane.add(lblPriceLists, 0, 2, 2, 1);
		pane.add(cbbPricelists, 0, 3, 2, 1);
		cbbPricelists.setPrefWidth(150);
		cbbPricelists.setPromptText("Vælg prisliste");

		pane.add(lvwSelectedPriceList, 1, 3, 2, 2);
		lvwSelectedPriceList.setPrefSize(100, 100);

		HBox boxPriceControl = new HBox(10);
		pane.add(boxPriceControl, 0, 4);
		boxPriceControl.getChildren().addAll(btnPlus, btnMinus);
		btnPlus.setOnAction(event -> controller.addPriceList());
		btnPlus.setPrefWidth(60);

		btnMinus.setOnAction(event -> controller.removePriceList());
		btnMinus.setPrefWidth(60);

		pane.add(boxTxfPrice, 0, 5);

		// ----------------------------------------------------------------------------------------

		HBox hBox = new HBox();
		pane.add(hBox, 0, 7, 2, 1);
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(10);
		hBox.getChildren().add(btnOk);
		hBox.getChildren().add(btnCancel);

		btnOk.setOnAction(event -> controller.okAction());
		btnCancel.setOnAction(event -> controller.cancelAction());

		HBox boxError = new HBox();
		pane.add(boxError, 0, 6);
		boxError.getChildren().add(lblError);
		boxError.setAlignment(Pos.CENTER);
		lblError.setStyle("-fx-text-fill: red");

		controller.fillComboBox();
	}

	// -------------------------------------------------------------------------------

	public boolean getResult() {
		return controller.result;
	}

	// -------------------------------------------------------------------------------

	private class Controller {
		private Service service = new Service();
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

		public void fillComboBox() {
			cbbPricelists.getItems().setAll(service.getAllPricelists());
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

		public void okAction() {
			String kegName = txfName.getText().trim();
			if (kegName.length() == 0) {
				lblError.setText("Udfyld navn");
				return;
			}

			double litre = -1;
			try {
				litre = Double.parseDouble(txfLitre.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (litre < 0) {
				lblError.setText("Angiv antal liter");
				return;
			}

			double deposit = -1;
			try {
				deposit = Double.parseDouble(txfLitre.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (deposit < 0) {
				lblError.setText("Angiv pant");
				return;
			}

			BeerKeg keg = service.createBeerKeg(kegName, deposit, litre);

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
					service.createPrice(pricelist, keg, price);
				}
			} else {
				lblError.setText("Vælg en prisliste");
				return;
			}

			this.resetAction();
			AddKegDialog.this.close();
		}

		public void cancelAction() {
			this.resetAction();
			AddKegDialog.this.close();
		}

		public void resetAction() {
			txfName.clear();
			txfDeposit.clear();
			txfLitre.clear();
			lblError.setText(null);
			cbbPricelists.getItems().clear();
			lvwSelectedPriceList.getItems().clear();
			boxTxfPrice.getChildren().clear();
			this.fillComboBox();
		}
	}
}
