package gui.calendar;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.model.BeerTap;
import application.model.Customer;
import application.model.Product;
import application.service.Service;
import gui.dialog.CreateCustomerDialog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateBeerTapReservation extends Stage {

    private final Controller controller = new Controller();
    private boolean result = false;

    public CreateBeerTapReservation() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.setTitle("Opret anlægs reservation");

        this.getIcons().add(new Image("BeerTap.png"));

        GridPane pane = new GridPane();
        this.initContent(pane);

        Scene scene = new Scene(pane);
        this.setScene(scene);

    }

    public boolean getResult() {
        return result;
    }

    Service service = new Service();
    private final ComboBox<Product> cbbTapType = new ComboBox<>();
    private final Label lblError = new Label();
    private final DatePicker dtpDateStart = new DatePicker(), dtpDateEnd = new DatePicker();
    private final ComboBox<Customer> cbbCustomer = new ComboBox<>();
    private final TextField txfPrice = new TextField();
    private final Button btnCreateCustomer = new Button("Opret kunde");

    private void initContent(GridPane pane) {

        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(10);
        // pane.setGridLinesVisible(true);
        Platform.runLater(() -> pane.requestFocus());

        Label lblBeerTap = new Label("Opret ny Anlægs Reservation");
        lblBeerTap.setFont(new Font(20));
        pane.add(lblBeerTap, 0, 0);

        VBox box1 = new VBox();
        box1.setSpacing(10);
        pane.add(box1, 0, 1);

        box1.getChildren().add(cbbTapType);
        cbbTapType.setPromptText("Vælg anlæg");
        cbbTapType.getItems().setAll(controller.getTaps());

        box1.getChildren().add(txfPrice);
        txfPrice.setPromptText("Dags pris");

        box1.getChildren().add(dtpDateStart);
        dtpDateStart.setPromptText("Satrt dato");

        box1.getChildren().add(dtpDateEnd);
        dtpDateEnd.setPromptText("Slut dato");

        box1.getChildren().add(cbbCustomer);
        cbbCustomer.setPromptText("Vælg kunde");

        box1.getChildren().add(btnCreateCustomer);
        btnCreateCustomer.setOnAction(event -> controller.openCreateCustomerDialog());

        box1.getChildren().add(lblError);
        lblError.setStyle("-fx-text-fill: red");

        HBox boxButtons = new HBox();
        boxButtons.setSpacing(10);
        Button btnOk = new Button("Tilføj");
        Button btnCancel = new Button("Annuller");

        boxButtons.getChildren().addAll(btnOk, btnCancel);
        pane.add(boxButtons, 0, 2);

        btnOk.setOnAction(Event -> {
            try {
                controller.okAction();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        btnCancel.setOnAction(Event -> controller.cancelAction());

        controller.fillCustomerCombo();

    }

    public void setStartDate(LocalDate date) {
        dtpDateStart.setValue(date);
    }

    private class Controller {
        CreateCustomerDialog createCustomerDialog;

        private void openCreateCustomerDialog() {
            if (createCustomerDialog == null) {
                createCustomerDialog = new CreateCustomerDialog("Opret ny kunde");
                Stage stage = (Stage) cbbCustomer.getScene().getWindow();
                createCustomerDialog.initOwner(stage);
            }

            createCustomerDialog.showAndWait();

            boolean isCreated = createCustomerDialog.getResult();
            if (isCreated) {
                cbbCustomer.getItems().setAll(service.getAllCustomers());
                int index = cbbCustomer.getItems().size() - 1;
                cbbCustomer.getSelectionModel().select(index);

            }
        }

        private List<Product> getTaps() {
            List<Product> newList = new ArrayList<>();
            for (Product p : service.getAllProducts()) {
                if (p.getType().compareTo("Anlæg") == 0) {
                    newList.add(p);
                }
            }
            return newList;
        }

        private void fillCustomerCombo() {
            cbbCustomer.getItems().setAll(service.getAllCustomers());
        }

        private void resetDialog() {
            lblError.setText(null);
            dtpDateStart.setValue(null);
            dtpDateEnd.setValue(null);
            cbbTapType.getItems().setAll(getTaps());
            cbbTapType.getSelectionModel().select(null);
            fillCustomerCombo();

        }

        private void okAction() throws Exception {

            try {

                Product beerTap = cbbTapType.getSelectionModel().getSelectedItem();
                if (beerTap == null) {
                    lblError.setText("Vælg et anlæg");
                    throw new Exception("BeerTap not selected");
                }

                double dayPrice = -1;
                try {
                    dayPrice = Double.parseDouble(txfPrice.getText().trim());
                } catch (NumberFormatException ex) {
                    // do nothing
                }

                if (dayPrice <= 0) {
                    lblError.setText("Angiv venligst en pris");
                    return;
                }

                LocalDate start = dtpDateStart.getValue();
                LocalDate end = dtpDateEnd.getValue();
                if (end.isBefore(start)) {
                    lblError.setText("Start dato må ikke være efter slut dato");
                    throw new Exception("Start date cannot be after end date");
                }
                if (start == null) {
                    lblError.setText("Angiv venligst en start dato");
                    return;
                }

                Customer customer = cbbCustomer.getSelectionModel().getSelectedItem();
                if (customer == null) {
                    lblError.setText("Vælg en kunde");
                    throw new Exception("Customer not chosen");
                }

                service.reserveBeerTap((BeerTap) beerTap, start, end, customer, dayPrice);
                resetDialog();
                result = true;
                CreateBeerTapReservation.this.close();

            } catch (DateTimeException ex) {
                lblError.setText(ex.getMessage());
            }

        }

        private void cancelAction() {
            resetDialog();
            result = false;
            CreateBeerTapReservation.this.close();
        }
    }
}
