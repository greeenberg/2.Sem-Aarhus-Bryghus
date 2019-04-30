package gui.panes;

import java.time.LocalDate;
import java.util.List;

import application.model.Order;
import application.service.Service;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class SoldClipPane extends GridPane {
	private final Controller controller = new Controller();

	public SoldClipPane() {
		this.initContent();
	}

	// ----------------------------------------------------------------------------

	private final DatePicker startDate = new DatePicker();
	private final DatePicker endDate = new DatePicker();

	private final TextField txfSoldClip = new TextField();
	private final TextField txfUsedClip = new TextField();
	private final TextField txfReamingClips = new TextField();

	private final Button btnSearh = new Button("Søg");

	private final Label lblError = new Label();

	public void initContent() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(10);
		this.setHgap(10);

		Label lblHeadLine = new Label("Solgte klip i periode");
		lblHeadLine.setFont(new Font(30));
		this.add(lblHeadLine, 0, 0);

		HBox box1 = new HBox(10);
		this.add(box1, 0, 1);
		box1.getChildren().addAll(startDate, endDate, btnSearh, lblError);
		startDate.setPromptText("Start dato");
		endDate.setPromptText("Slut dato");
		lblError.setStyle("-fx-text-fill: red");

		btnSearh.setOnAction(event -> controller.fillTextFields());

		Label lblSoldClip = new Label("Solgte klip");
		lblSoldClip.setFont(new Font(20));

		VBox box2 = new VBox(10);
		this.add(box2, 0, 2);
		box2.getChildren().addAll(lblSoldClip, txfSoldClip);
		txfSoldClip.setEditable(false);

		Label lblUsedClip = new Label("Forbrugte klip");
		lblUsedClip.setFont(new Font(20));

		VBox box3 = new VBox(10);
		this.add(box3, 0, 3);
		box3.getChildren().addAll(lblUsedClip, txfUsedClip);
		txfUsedClip.setEditable(false);

		Label lblReamingClip = new Label("Tilbageværende klip");
		lblReamingClip.setFont(new Font(20));

		VBox box4 = new VBox(10);
		this.add(box4, 0, 4);
		box4.getChildren().addAll(lblReamingClip, txfReamingClips);
		txfReamingClips.setEditable(false);

	}

	// -------------------------------------------------------------------------------

	public void updateControls() {
		controller.updateControls();
	}

	// ---------------------------------------------------------------------------------

	private class Controller {
		Service service = new Service();

		public void updateControls() {

		}

		public void fillTextFields() {
			LocalDate start = startDate.getValue();
			LocalDate end = endDate.getValue();

			if (start == null || end == null) {
				lblError.setText("Datoerne er ikke udfyldt");
				return;
			} else {
				lblError.setText("");
				List<Order> orders = service.getOrdersInPeriod(start, end);

				int amountSoldClip = service.getClipsBoughtInOrderList(orders);

				txfSoldClip.setText(amountSoldClip + "");

				int amountUsedClip = service.getClipsUsedInOrderList(orders);
				txfUsedClip.setText(amountUsedClip + "");

				int amountReamining = service.remainingClipsInPeriod(start, end);
				txfReamingClips.setText(amountReamining + "");
			}
		}

		// public void fillSoldClip() {
		// LocalDate start = startDate.getValue();
		// LocalDate end = endDate.getValue();
		// List<Order> orders = service.getOrdersInPeriod(start, end);
		// List<LocalDate> Dates = service.calcDays(start, end);
		// int amount = service.getClipsBoughtInOrderList(orders);
		//
		//
		// }
	}

}
