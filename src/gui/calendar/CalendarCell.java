package gui.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.model.BeerTapReservation;
import application.model.GuidedTourReservation;
import application.model.Reservation;
import application.service.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CalendarCell extends StackPane {

    private Controller controller = new Controller();
    Service service = new Service();

    private int date;
    private int month;
    private int year;
    private List<Reservation> times = new ArrayList<>();

    public CalendarCell(int date, int month, int year) {
        this.date = date;
        this.month = month;
        this.year = year;
        InitContent();
    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void addReservation(Reservation reservation) {
        times.add(reservation);
    }

    public LocalDate getLocalDate() {
        return LocalDate.of(year, month, date);
    }

    public List<Reservation> getTimes() {
        return times;
    }

    final ContextMenu contextMenu = new ContextMenu();
    final Image image = new Image("Transparency100.png");
    private int height = 80;
    private int width = 80;

    public void InitContent() {
        updateReservations();
        this.setMinSize(width, height);
        this.setStyle("-fx-background-color: #dddddd;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
                + "-fx-border-color: grey;");
        if (!times.isEmpty()) {
            ImageView image = buildImage("NotificationFlag.png");
            this.getChildren().add(image);
            StackPane.setAlignment(image, Pos.TOP_RIGHT);
        }

        Label lblDate = new Label(date + "");
        this.getChildren().add(lblDate);
        StackPane.setAlignment(lblDate, Pos.CENTER);
        lblDate.setFont(new Font(30));
        lblDate.setMinSize(width, height);
        lblDate.setContextMenu(contextMenu);

        this.onMouseClickedProperty().set(Event -> mouseClickedCell(Event, this));

        MenuItem createReservationGuidedTour = new MenuItem("Opret rundvisnings reservation");
        MenuItem createReservationBeerTap = new MenuItem("Opret anlægs reservation");
        contextMenu.getItems().addAll(createReservationGuidedTour, createReservationBeerTap);

        createReservationGuidedTour.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("I clicked guidedtour on " + date);
                controller.createGuidedTourReservation();

            }

        });

        createReservationBeerTap.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("I clicked beertap on " + date);
                controller.createBeerTapReservation();
            }

        });
    }

    private ImageView buildImage(String imgPatch) {
        Image i = new Image(imgPatch);
        ImageView imageView = new ImageView();
        // You can set width and height
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.setImage(i);
        return imageView;
    }

    private void mouseClickedCell(MouseEvent me, StackPane pane) {
        CalendarMonth parent = (CalendarMonth) this.getParent();
        parent.setSpecificCell(this);
        CalendarPane cPane = (CalendarPane) pane.getParent().getParent();
        if (cPane != null) {
            cPane.textAreaSetText(getReservationInfo());
        }

        if (me.isSecondaryButtonDown()) {
            contextMenu.show(pane, me.getScreenX(), me.getScreenY());
        }

    }

    private String getReservationInfo() {
        StringBuilder sb = new StringBuilder();
        for (Reservation r : times) {
            sb.append("Reservation: \n" + r.toString() + "\n");
        }
        return sb.toString();
    }

    public void changeCellStyle(String style) {
        this.setStyle("-fx-background-color: " + style + "; -fx-border-style: solid inside;" + "-fx-border-width: 2;"
                + "-fx-border-color: grey;");
    }

    private void updateCalendar() {
        CalendarPane papa = (CalendarPane) this.getParent().getParent();
        papa.updateControls();
    }

    public void updateReservations() {
        for (Reservation r : service.getAllReservations()) {
            if (r instanceof BeerTapReservation
                    && this.getLocalDate().isEqual(((BeerTapReservation) r).getStartDate())) {
                this.addReservation(r);
            }
            if (r instanceof GuidedTourReservation && this.getLocalDate()
                    .compareTo(((GuidedTourReservation) r).getReservationTime().toLocalDate()) == 0) {
                this.addReservation(r);
            }
        }
    }

    private class Controller {
        CreateGuidedTourReservation reservationGuidedTour;
        CreateBeerTapReservation reservationBeerTap;

        private void createGuidedTourReservation() {
            reservationGuidedTour = new CreateGuidedTourReservation();
            Stage stage = new Stage();
            reservationGuidedTour.initOwner(stage);
            reservationGuidedTour.setDate(getLocalDate());
            reservationGuidedTour.showAndWait();

            boolean isCreated = reservationGuidedTour.getResult();
            if (isCreated) {
                updateCalendar();
            }
        }

        private void createBeerTapReservation() {
            reservationBeerTap = new CreateBeerTapReservation();
            Stage stage = new Stage();
            reservationBeerTap.initOwner(stage);
            reservationBeerTap.setStartDate(getLocalDate());
            reservationBeerTap.showAndWait();

            boolean isCreated = reservationBeerTap.getResult();
            if (isCreated) {
                updateCalendar();
            }
        }
    }

}
