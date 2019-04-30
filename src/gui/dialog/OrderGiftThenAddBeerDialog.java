package gui.dialog;

import java.util.ArrayList;
import java.util.List;

import application.model.Gift;
import application.model.Order;
import application.model.OrderLine;
import application.model.PriceList;
import application.model.Product;
import application.service.Service;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OrderGiftThenAddBeerDialog extends Stage {
    private final Controller controller = new Controller();
    private final Order order;
    private Gift gift;
    private PriceList priceList;
    Service service = new Service();
    private boolean result = false;

    public OrderGiftThenAddBeerDialog(String name, Order order) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.order = order;

        this.setTitle(name);
        GridPane pane = new GridPane();
        this.initContent(pane);

        Scene scene = new Scene(pane);
        this.setScene(scene);
    }

    private int beerRemaining = 0;
    private int originalAmount = 0;
    private final ListView<Product> lvwProducts = new ListView<>();
    private final ListView<OrderLine> lvwItems = new ListView<>();
    private final TextField txfAmountToAdd = new TextField();
    private final Label lblAmountLeft = new Label();

    private void initContent(GridPane pane) {

        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setVgap(10);
        pane.setHgap(10);

        pane.add(lvwProducts, 0, 0);
        lvwProducts.getItems().setAll(controller.getProperProducts());
        VBox boxItems = new VBox(10);
        pane.add(boxItems, 1, 0);
        pane.add(lvwItems, 2, 0);

        Button btnAdd = new Button("Tilføj");
        Button btnRemove = new Button("Fjern");
        txfAmountToAdd.setPromptText("Antal");

        boxItems.getChildren().addAll(lblAmountLeft, btnAdd, txfAmountToAdd, btnRemove);
        btnAdd.setOnAction(Event -> controller.addAction());
        btnRemove.setOnAction(Event -> controller.removeAction());

        Button btnClose = new Button("Udregn & Luk");
        pane.add(btnClose, 0, 1);
        btnClose.setOnAction(Event -> controller.closeAction());

        controller.setValues();
        lblAmountLeft.setText("Manglende øl: " + beerRemaining);

    }

    public boolean getResult() {
        return result;
    }

    private class Controller {

        private List<Product> getProperProducts() {
            List<Product> newList = new ArrayList<>();
            for (Product product : service.getAllProducts()) {
                if (product.getType().trim().toLowerCase().compareTo("flaske") == 0) {
                    newList.add(product);
                }
            }

            return newList;
        }

        private void addAction() {
            Product product = lvwProducts.getSelectionModel().getSelectedItem();
            int amount = Integer.parseInt(txfAmountToAdd.getText());

            if (product == null) {
                return;
            }

            if (beerRemaining > 0) {
                OrderLine line = service.createOrderLine(order, product, amount, priceList);
                lvwItems.getItems().add(line);
                beerRemaining -= amount;
                lblAmountLeft.setText("Manglende øl: " + beerRemaining);
            }

        }

        private void removeAction() {

            OrderLine ol = lvwItems.getSelectionModel().getSelectedItem();
            int amount = Integer.parseInt(txfAmountToAdd.getText());

            if (ol == null) {
                return;
            }

            if (beerRemaining <= originalAmount) {
                if (ol.getAmount() < 1) {
                    lvwItems.getItems().remove(ol);
                } else {
                    ol.setAmount(ol.getAmount() - amount);
                    lvwItems.getItems().set(lvwItems.getSelectionModel().getSelectedIndex(), ol);
                }
                beerRemaining += amount;
                lblAmountLeft.setText("Manglende øl: " + beerRemaining);
            }

        }

        private void closeAction() {
            double amountToReduceWith = 0;

            for (OrderLine ol : lvwItems.getItems()) {
                amountToReduceWith += (ol.getPrice() * ol.getAmount());
            }
            order.discountFullPrice(amountToReduceWith);
            result = true;
            lvwItems.getItems().clear();
            txfAmountToAdd.clear();
            OrderGiftThenAddBeerDialog.this.close();
        }

        private void setValues() {
            for (OrderLine ol : order.getOrderLines()) {
                if (ol.getProduct() instanceof Gift) {
                    gift = (Gift) ol.getProduct();
                    priceList = ol.getPricelist();
                    break;
                }
            }
            beerRemaining = gift.getAmountBeer();
            originalAmount = gift.getAmountBeer();
        }

    }

}
