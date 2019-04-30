package gui.calendar;

import java.time.Month;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class CalendarMonth extends GridPane {

    private int days;
    private CalendarCell[] cells;
    private CalendarCell specificCell = null;
    private CalendarCell formerCell = null;

    public CalendarMonth(Month month, boolean isLeapYear, int year) {
        this.days = month.length(isLeapYear);
        cells = new CalendarCell[this.days];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new CalendarCell(i + 1, month.getValue(), year);
        }
        InitContent();

    }

    public int getDays() {
        return days;
    }

    public CalendarCell[] getCells() {
        return cells;
    }

    public CalendarCell getSpecificCell() {
        return specificCell;
    }

    public void setSpecificCell(CalendarCell specificCell) {
        if (formerCell != null)
            formerCell.changeCellStyle("#dddddd");
        if (specificCell != null)

            this.formerCell = specificCell;
        this.specificCell = specificCell;
        this.specificCell.changeCellStyle("whitesmoke");
    }

    public CalendarCell getFormerCell() {
        return formerCell;
    }

    public void setFormerCell(CalendarCell formerCell) {
        this.formerCell = formerCell;
    }

    public void InitContent() {
        this.setPadding(new Insets(20, 20, 20, 20));
        this.setStyle("-fx-border-stroke: #000000");
        this.setStyle("-fx-background-color: #cccccc");
        this.setAlignment(Pos.CENTER);

        int rows = 0;
        int col = 0;
        for (int i = 0; i < cells.length; i++) {
            if (i % 7 == 0) {
                rows++;
                col = 0;
            }
            this.add(cells[i], col, rows);
            col++;
        }

    }

}
