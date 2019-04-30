package gui.dialog;

import application.service.Service;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddBeerTapDialog extends Stage {
	private final Controller controller = new Controller();

	public AddBeerTapDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);
		this.getIcons().add(new Image("BeerTap.png"));

		this.setTitle(name);
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);
	}

	private final TextField txfName = new TextField();
	private final TextField txfAmountTap = new TextField();
	private final Button btnOk = new Button("Acceptere");
	private final Button btnCancel = new Button("Annuler");
	private final Label lblError = new Label();

	private void initContent(GridPane pane) {

		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());

		Label lblBeerTap = new Label("Opret nyt anlæg");
		lblBeerTap.setFont(new Font(20));
		pane.add(lblBeerTap, 0, 0);

		HBox box1 = new HBox(10);
		pane.add(box1, 0, 1);
		box1.getChildren().addAll(txfName, txfAmountTap);
		txfName.setPromptText("Navn");
		txfAmountTap.setPromptText("Antal haner");

		HBox boxError = new HBox();
		pane.add(boxError, 0, 2);
		boxError.getChildren().add(lblError);
		boxError.setAlignment(Pos.CENTER);
		lblError.setStyle("-fx-text-fill: red");

		HBox box2 = new HBox(10);
		pane.add(box2, 0, 3);
		box2.setAlignment(Pos.CENTER);
		box2.getChildren().addAll(btnOk, btnCancel);

		btnOk.setOnAction(event -> controller.OkAction());
		btnCancel.setOnAction(event -> controller.cancelAction());

	}

	// -------------------------------------------------------------------------------------------------

	public boolean getResult() {
		return controller.result;
	}

	// -------------------------------------------------------------------------------------------------

	private class Controller {
		private Service service = new Service();
		private boolean result;

		private void OkAction() {
			int tapAmount = -1;
			try {
				tapAmount = Integer.parseInt(txfAmountTap.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (tapAmount <= 0) {
				lblError.setText("Kan ikke være bogstaver eller negative tal");
				txfAmountTap.requestFocus();
				return;
			}

			String name = txfName.getText().trim();
			if (name.length() == 0) {
				lblError.setText("Udfyld venligst navn");
				txfName.requestFocus();
				return;
			}

			service.createBeerTap(name, tapAmount);

			this.resetAction();
			AddBeerTapDialog.this.close();
		}

		private void cancelAction() {
			this.resetAction();
			AddBeerTapDialog.this.close();
		}

		private void resetAction() {
			txfName.clear();
			txfAmountTap.clear();
		}
	}

}
