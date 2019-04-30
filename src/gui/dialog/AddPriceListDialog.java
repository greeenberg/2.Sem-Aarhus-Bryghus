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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddPriceListDialog extends Stage {
	private final Controller controller = new Controller();

	public AddPriceListDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		this.getIcons().add(new Image("Liste.png"));
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	private final TextField txfName = new TextField();
	private final Button btnOk = new Button("Accepter");
	private final Button btnCancel = new Button("Annuler");
	private final Label lblError = new Label();

	public void initContent(GridPane pane) {
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());

		pane.add(txfName, 0, 0, 2, 1);
		txfName.setPromptText("Navn");

		pane.add(lblError, 0, 1, 2, 1);
		lblError.setStyle("-fx-text-fill: red");

		HBox box1 = new HBox();
		pane.add(box1, 0, 2, 2, 1);
		box1.setAlignment(Pos.CENTER);
		box1.setSpacing(10);
		box1.getChildren().add(btnOk);
		box1.getChildren().add(btnCancel);

		btnOk.setOnAction(event -> controller.okAction());
		btnCancel.setOnAction(event -> controller.cancelAction());

	}

	// -----------------------------------------------------------------------------
	public boolean getResult() {
		return controller.result;
	}

	// -----------------------------------------------------------------------------
	private class Controller {
		private Service service = new Service();
		private boolean result = false;

		public void okAction() {
			String name = txfName.getText().trim();
			if (name.length() == 0) {
				lblError.setText("Udfyld navn");
				return;
			}

			service.createPriceList(name);

			txfName.clear();
			AddPriceListDialog.this.hide();
		}

		public void cancelAction() {
			AddPriceListDialog.this.hide();
		}
	}
}
