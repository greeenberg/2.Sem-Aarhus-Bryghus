package gui.calendar;

import java.time.YearMonth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class CalendarPane extends BorderPane {

	private Controller controller = new Controller();

	private YearMonth yearMonth;
	private String[] translatedMonths = { "Januar", "Februar", "Marts", "April", "Maj", "Juni", "Juli", "August",
			"September", "Oktober", "November", "December" };

	public CalendarPane() {
		yearMonth = YearMonth.now();
		InitContent();
	}

	public int getYear() {
		return yearMonth.getYear();
	}

	public int getMonth() {
		return yearMonth.getMonthValue();

	}

	private Label lblMonth = new Label();
	private TextArea txaReservations = new TextArea();

	private void InitContent() {

		this.setPadding(new Insets(20, 20, 20, 20));

		String year = yearMonth.getYear() + " ";
		lblMonth.setText(year + translatedMonths[yearMonth.getMonthValue() - 1]);
		lblMonth.setFont(new Font(30));

		GridPane griddy = new GridPane();
		griddy.setPadding(new Insets(10));
		griddy.setHgap(20);

		Button btnLeft = new Button("<-");
		btnLeft.setOnAction(Event -> controller.leftAction());
		Button btnRight = new Button("->");
		btnRight.setOnAction(Event -> controller.rightAction());

		griddy.add(btnLeft, 0, 0);
		griddy.add(lblMonth, 1, 0);
		griddy.add(btnRight, 2, 0);
		txaReservations.setMaxWidth(200);
		txaReservations.setWrapText(true);
		this.setRight(txaReservations);

		this.setTop(griddy);

		setCalendar();
		BorderPane.setAlignment(getCenter(), Pos.CENTER);
	}

	private void setCalendar() {
		this.setCenter(controller.calendarAction());
	}

	public void updateControls() {
		setCalendar();
	}

	public void textAreaSetText(String text) {
		txaReservations.setText(text);
	}

	private class Controller {

		private void leftAction() {
			yearMonth = yearMonth.minusMonths(1);
			setCalendar();
			String year = yearMonth.getYear() + " ";
			lblMonth.setText(year + translatedMonths[yearMonth.getMonthValue() - 1]);
		}

		private void rightAction() {
			yearMonth = yearMonth.plusMonths(1);
			setCalendar();
			String year = yearMonth.getYear() + " ";
			lblMonth.setText(year + translatedMonths[yearMonth.getMonthValue() - 1]);
		}

		private CalendarMonth calendarAction() {
			CalendarMonth month = new CalendarMonth(yearMonth.getMonth(), yearMonth.isLeapYear(), yearMonth.getYear());
			return month;
		}

	}
}
