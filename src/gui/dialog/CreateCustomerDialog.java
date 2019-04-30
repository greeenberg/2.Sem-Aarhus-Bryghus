package gui.dialog;

import application.service.Service;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateCustomerDialog extends Stage {
	private final Controller controller = new Controller();

	public CreateCustomerDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		this.getIcons().add(new Image("Customer.png"));
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	// ------------------------------------------------------------------------------

	private final TextField txfName = new TextField();
	private final TextField txfPhone = new TextField();
	private final Button btnOk = new Button("Opret");
	private final Button btnCancel = new Button("Annuller");
	private final Label lblError = new Label();

	public void initContent(GridPane pane) {
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());

		VBox box1 = new VBox();
		box1.setSpacing(10);
		pane.add(box1, 0, 0, 2, 1);
		box1.getChildren().add(txfName);
		txfName.setPromptText("Navn");
		box1.getChildren().add(txfPhone);
		txfPhone.setPromptText("Telefon nummer");

		HBox box2 = new HBox();
		box2.setSpacing(10);
		pane.add(box2, 0, 1);
		box2.getChildren().add(btnOk);
		btnOk.setOnAction(event -> controller.okAction());
		box2.getChildren().add(btnCancel);
		btnCancel.setOnAction(event -> controller.cancelAction());

		pane.add(lblError, 0, 2);
		lblError.setStyle("-fx-text-fill: red");

	}

	// -------------------------------------------------------------------------------

	public boolean getResult() {
		return controller.result;
	}

	// -------------------------------------------------------------------------------

	private class Controller {
		Service service = new Service();
		private boolean result = false;

		public void okAction() {
			String name = txfName.getText().trim();
			if (name.length() == 0) {
				lblError.setText("Udfyld venligst navn");
				return;
			}

			int phone = -1;
			try {
				phone = Integer.parseInt(txfPhone.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (phone < 0) {
				lblError.setText("Udfyld venligst telefon nummer");
				return;
			}

			service.createCustomer(name, phone);

			txfName.clear();
			txfPhone.clear();
			lblError.setText("");
			result = true;
			CreateCustomerDialog.this.close();
		}

		public void cancelAction() {
			CreateCustomerDialog.this.close();
		}

	}

}
