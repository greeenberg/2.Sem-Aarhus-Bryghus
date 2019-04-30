package gui.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import application.model.Customer;
import application.service.Service;
import gui.dialog.CreateCustomerDialog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateGuidedTourReservation extends Stage {

    private final Controller controller = new Controller();
    private boolean result = false;

    public CreateGuidedTourReservation() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.setTitle("Opret rundvisnings reservation");

        this.getIcons().add(new Image("Guide.png"));

        GridPane pane = new GridPane();
        this.initContent(pane);

        Scene scene = new Scene(pane);
        this.setScene(scene);

    }

    public boolean getResult() {
        return result;
    }

    private Service service = new Service();
    private final TextField txfTimeHour = new TextField();
    private final TextField txfTimeMin = new TextField();
    private final TextField txfAmountPeople = new TextField();
    private final TextField txfPricePerPerson = new TextField();
    private final Label lblError = new Label();
    private final DatePicker dtpDate = new DatePicker();
    private final ComboBox<Customer> cbbCustomer = new ComboBox<>();
    private final Button btnCreateCustomer = new Button("Opret kunde");

    private void initContent(GridPane pane) {

        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(10);
        Platform.runLater(() -> pane.requestFocus());
        // pane.setGridLinesVisible(true);

        Label lblGuidedTour = new Label("Opret ny Rundvisning");
        lblGuidedTour.setFont(new Font(20));
        pane.add(lblGuidedTour, 0, 0);

        pane.add(txfPricePerPerson, 0, 1);
        txfPricePerPerson.setPromptText("Pris");
        txfPricePerPerson.setMaxWidth(80);
        txfPricePerPerson.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txfPricePerPerson.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }

        });

        pane.add(dtpDate, 0, 2);
        dtpDate.setPromptText("Vælg dato");

        HBox boxTime = new HBox(5);
        Label lblTime = new Label(":");
        txfTimeHour.setMaxWidth(40);
        txfTimeMin.setMaxWidth(40);
        txfTimeHour.setPromptText("12");
        txfTimeMin.setPromptText("00");
        boxTime.getChildren().addAll(txfTimeHour, lblTime, txfTimeMin);
        pane.add(boxTime, 0, 3);

        HBox boxAmount = new HBox(10);
        Label lblAmountPeople = new Label("Antal Personer:");
        boxAmount.getChildren().addAll(lblAmountPeople, txfAmountPeople);
        pane.add(boxAmount, 0, 4);
        txfAmountPeople.setMaxWidth(40);

        pane.add(cbbCustomer, 0, 5);
        cbbCustomer.getItems().setAll(service.getAllCustomers());
        cbbCustomer.setPromptText("Vælg kunde");

        pane.add(btnCreateCustomer, 0, 6);
        btnCreateCustomer.setOnAction(event -> controller.openCreateCustomerDialog());

        lblError.setStyle("-fx-text-fill: red");
        pane.add(lblError, 0, 7);

        HBox boxButtons = new HBox(10);
        Button btnOk = new Button("Tilføj");
        Button btnCancel = new Button("Annuller");

        boxButtons.getChildren().addAll(btnOk, btnCancel);
        pane.add(boxButtons, 0, 8);

        btnOk.setOnAction(Event -> {
            try {
                controller.okAction();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        btnCancel.setOnAction(Event -> controller.cancelAction());

    }

    public void setDate(LocalDate date) {
        dtpDate.setValue(date);
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

        private void resetDialog() {
            lblError.setText(null);
            txfTimeHour.setText(null);
            txfTimeMin.setText(null);
            dtpDate.setValue(null);

        }

        private void okAction() throws Exception {
            int hour = 11;
            int min = 0;
            int amountPeople = 0;
            double pricePerPerson = 0;

            try {
                hour = Integer.parseInt(txfTimeHour.getText().trim());
                min = Integer.parseInt(txfTimeMin.getText().trim());
                amountPeople = Integer.parseInt(txfAmountPeople.getText().trim());
                pricePerPerson = Double.parseDouble(txfPricePerPerson.getText());
                Customer customer = cbbCustomer.getSelectionModel().getSelectedItem();

                if (hour < 12 || hour > 18) {
                    lblError.setText("Rundvisning ikke i gyldig tidsperiode");
                    throw new Exception("Tour not within time limits");
                }

                if (amountPeople <= 0) {
                    lblError.setText("Antal besøgende har en ugyldig værdi");
                    throw new Exception("Too few people");
                }

                if (customer == null) {
                    lblError.setText("Vælg en kunde");
                    throw new Exception("No customer chosen");
                }

                LocalDateTime dateAndTime = LocalDateTime.of(dtpDate.getValue(), LocalTime.of(hour, min));

                service.reserveGuidedTour(dateAndTime, amountPeople, pricePerPerson, customer);
                resetDialog();
                result = true;
                CreateGuidedTourReservation.this.close();

            } catch (NumberFormatException ex) {
                lblError.setText("Indtast gyldig tal");
            }

        }

        private void cancelAction() {
            resetDialog();
            result = false;
            CreateGuidedTourReservation.this.close();
        }
    }
}
