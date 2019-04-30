package gui.dialog;

import java.time.LocalDate;

import application.model.Order;
import application.model.PaymentMethod;
import application.service.Service;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PaymentDialog extends Stage {
	private final Controller controller = new Controller();

	public PaymentDialog(String name) {
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		this.setTitle(name);
		this.getIcons().add(new Image("Payment.png"));
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);

	}

	private final TextField txfAmount = new TextField();
	private final ComboBox<PaymentMethod> cbbMethod = new ComboBox<>();
	private Button btnPay = new Button("Betal");
	private final Label lblError = new Label();
	private final HBox box2 = new HBox();
	private final ImageView ivwImg = new ImageView();
	private final Image imgInfo = new Image("Information.png");
	private final Tooltip tooltip = new Tooltip("brug antal af klip og ikke værdien");

	public void initContent(GridPane pane) {
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setVgap(10);
		pane.setHgap(10);
		Platform.runLater(() -> pane.requestFocus());

		VBox box = new VBox();
		pane.add(box, 0, 1);
		box.setSpacing(10);
		box.setAlignment(Pos.CENTER);

		box.getChildren().add(cbbMethod);
		cbbMethod.setPromptText("Vælg metode");
		ChangeListener<PaymentMethod> listenerPaymentMethod = (ov, o, n) -> controller.updateInfoLogo();
		cbbMethod.getSelectionModel().selectedItemProperty().addListener(listenerPaymentMethod);

		box.getChildren().add(lblError);
		lblError.setStyle("-fx-text-fill: red");

		box.getChildren().add(btnPay);

		btnPay.setOnAction(event -> controller.payment());

		ivwImg.setImage(imgInfo);
		ivwImg.setFitHeight(20);
		ivwImg.setFitWidth(20);
		Tooltip.install(ivwImg, tooltip);

		pane.add(box2, 0, 0, 2, 1);
		box2.setSpacing(10);
		box2.getChildren().add(txfAmount);
		txfAmount.setPromptText("Beløb");

		controller.fillComboBox();
	}

	// -------------------------------------------------------------------------

	public boolean getResult() {
		return controller.result;
	}

	public void setOrder(Order order) {
		controller.valgtOrder = order;
	}

	private class Controller {
		Service service = new Service();
		private boolean result = false;
		Order valgtOrder;

		public void payment() {
			double amount = -1;
			try {
				amount = Integer.parseInt(txfAmount.getText().trim());
			} catch (NumberFormatException ex) {
				// do nothing
			}

			if (amount < 0) {
				lblError.setText("Angiv venligst en pris");
				return;
			}

			PaymentMethod method = cbbMethod.getSelectionModel().getSelectedItem();
			if (cbbMethod.getItems() == null) {
				lblError.setText("Venligst vælg betalingsmetode");
				return;
			}

			service.createPayment(valgtOrder, amount, method, LocalDate.now());

			txfAmount.clear();
			cbbMethod.getItems().clear();
			this.fillComboBox();
			result = true;
			PaymentDialog.this.close();
		}

		public void updateInfoLogo() {
			if (cbbMethod.getSelectionModel().getSelectedItem() == PaymentMethod.KLIP) {
				box2.getChildren().add(ivwImg);
			} else {
				box2.getChildren().remove(ivwImg);
			}
		}

		public void fillComboBox() {
			cbbMethod.getItems().setAll(service.getAllPaymentMethod());
		}
	}

}
