package gui.panes;

import application.model.Product;
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

public class ProductIndexPane extends GridPane {
	private final Controller controller = new Controller();
	private final Service service = new Service();

	public ProductIndexPane() {
		this.initContent();
	}

	// -----------------------------------------------------------------------------
	private final TextField txfSearch = new TextField();
	private final Button btnSearch = new Button("Søg");
	private final ListView<Product> lvwProducts = new ListView<>();
	private final Button btnDelete = new Button("Slet produkt");

	public void initContent() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(10);
		this.setHgap(10);

		txfSearch.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				lvwProducts.getItems().setAll(service.getAllProducts(txfSearch.getText().trim()));
			}

		});

		Label lblHeadLine = new Label("Sortiment oversigt");
		lblHeadLine.setFont(new Font(30));
		this.add(lblHeadLine, 0, 0);

		HBox box1 = new HBox(10);
		this.add(box1, 0, 1);
		box1.getChildren().addAll(txfSearch, btnSearch);

		HBox box2 = new HBox(10);
		this.add(box2, 0, 2);
		box2.getChildren().addAll(lvwProducts);

		HBox box3 = new HBox(10);
		this.add(box3, 0, 3);
		box3.getChildren().addAll(btnDelete);

		btnDelete.setOnAction(event -> controller.deleteProduct());

		controller.updateControls();

	}

	// -----------------------------------------------------------------------------

	public void updateControls() {
		controller.updateControls();
	}

	// -----------------------------------------------------------------------------

	private class Controller {

		private void updateControls() {
			lvwProducts.getItems().setAll(service.getAllProducts());
		}

		private void deleteProduct() {
			Product product = lvwProducts.getSelectionModel().getSelectedItem();
			if (product != null) {
				service.removeProduct(product);
			}
			updateControls();
		}

	}

}
