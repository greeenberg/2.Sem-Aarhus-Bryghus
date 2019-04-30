package gui.dialog;

import java.util.HashSet;
import java.util.Set;

import application.model.Beer;
import application.model.PriceList;
import application.service.Service;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

public class TilføjØlDialog extends Stage {
	private final Controller controller = new Controller();

	public TilføjØlDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		this.getIcons().add(new Image("Beer.png"));
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	private final TextField txfName = new TextField();
	private final CheckBox chbBottle = new CheckBox("Flaske");
	private final CheckBox chbTapBeer = new CheckBox("Fadøl");
	private final ComboBox<PriceList> cbbPricelists = new ComboBox<>();
	private final ListView<PriceList> lvwSelectedPriceList = new ListView<>();
	private final Button btnPlus = new Button("Tilføj");
	private final Button btnMinus = new Button("Fjern");
	private final Button btnOk = new Button("Tilføj");
	private final Button btnCancel = new Button("Annuller");

	private final VBox boxTxfPrice = new VBox();

	private final Label lblError = new Label();

	public void initContent(GridPane pane) {
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());

		pane.add(txfName, 0, 0, 2, 1);
		txfName.setPromptText("Navn");

		VBox boxChb = new VBox(10);
		pane.add(boxChb, 2, 0);
		boxChb.getChildren().addAll(chbBottle, chbTapBeer);
		// ---------------------------------------------------------------------------------------

		Label lblPriceLists = new Label("Vælg prisliste(r)");
		pane.add(lblPriceLists, 0, 2, 2, 1);
		pane.add(cbbPricelists, 0, 3, 2, 1);
		cbbPricelists.setPrefWidth(150);
		cbbPricelists.setPromptText("Vælg prisliste");

		pane.add(lvwSelectedPriceList, 2, 3, 2, 2);
		lvwSelectedPriceList.setPrefSize(100, 100);

		pane.add(btnPlus, 0, 4);
		btnPlus.setOnAction(event -> controller.addPriceList());
		btnPlus.setPrefWidth(60);
		pane.add(btnMinus, 1, 4);
		btnMinus.setOnAction(event -> controller.removePriceList());
		btnMinus.setPrefWidth(60);

		pane.add(boxTxfPrice, 0, 5, 2, 1);

		// ----------------------------------------------------------------------------------------

		HBox hBox = new HBox();
		pane.add(hBox, 1, 7);
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

	// ---------------------------------------------------------------------------

	public boolean getResult() {
		return controller.result;
	}
	// ---------------------------------------------------------------------------

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
			String beerName = txfName.getText().trim();
			if (beerName.length() == 0) {
				lblError.setText("Udfyld navn");
				return;
			}

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

					if (chbTapBeer.isSelected() == true && chbBottle.isSelected() == true) {
						Beer product = service.createBeer(beerName, "Fadøl");
						Beer product2 = service.createBeer(beerName, "Flaske");
						service.createPrice(pricelist, product, price);
						service.createPrice(pricelist, product2, price);

					} else if (chbBottle.isSelected() == true) {
						Beer product2 = service.createBeer(beerName, "Flaske");
						service.createPrice(pricelist, product2, price);
					} else if (chbTapBeer.isSelected() == true) {
						Beer product = service.createBeer(beerName, "Fadøl");
						service.createPrice(pricelist, product, price);
					} else {
						lblError.setText("Vælg en øl type");
						return;
					}
				}
			} else {
				lblError.setText("Vælg en prisliste");
				return;
			}

			this.resetAction();
			TilføjØlDialog.this.close();
		}

		public void cancelAction() {
			this.resetAction();
			TilføjØlDialog.this.close();
		}

		public void resetAction() {
			txfName.clear();
			chbBottle.setSelected(false);
			chbTapBeer.setSelected(false);
			lblError.setText(null);
			cbbPricelists.getItems().clear();
			lvwSelectedPriceList.getItems().clear();
			boxTxfPrice.getChildren().clear();
			this.fillComboBox();
		}

	}

}