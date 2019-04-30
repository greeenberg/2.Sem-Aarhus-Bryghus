package gui;

import application.service.Service;
import gui.calendar.CalendarPane;
import gui.calendar.CreateBeerTapReservation;
import gui.calendar.CreateGuidedTourReservation;
import gui.dialog.AddBeerTapDialog;
import gui.dialog.AddCarbonDioxideDialog;
import gui.dialog.AddGiftDialog;
import gui.dialog.AddKegDialog;
import gui.dialog.AddPriceListDialog;
import gui.dialog.TilføjØlDialog;
import gui.panes.CustomerIndexPane;
import gui.panes.FridayJamPane;
import gui.panes.FridayPane;
import gui.panes.PeriodSalePane;
import gui.panes.ProductIndexPane;
import gui.panes.SalePane;
import gui.panes.SoldClipPane;
import gui.panes.TodaysSalePane;
import gui.panes.WareHousePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	private final Controller controller = new Controller();

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Aarhus Bryghus");
		stage.getIcons().add(new Image("Bryghus.png"));
		BorderPane pane = new BorderPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void init() {
		Service.loadStorage();
	}

	@Override
	public void stop() {
		Service.saveStorage();
	}

	private final TabPane tabPane = new TabPane();

	private void initContent(BorderPane pane) {

		this.initTabPane(tabPane);
		pane.setCenter(tabPane);
		tabPane.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> controller.updatePane());

		MenuBar menubar = new MenuBar();
		this.initMenuBar(menubar);
		pane.setTop(menubar);

	}

	private void initTabPane(TabPane tabPane) {
		tabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);

		// -------------SALESPANE------------------------------------------------------

		Tab tabSale = new Tab("Salg");
		tabPane.getTabs().add(tabSale);
		tabSale.setGraphic(controller.buildImage("CashRegister.png"));
		tabSale.setClosable(false);

		SalePane sale = new SalePane();
		tabSale.setContent(sale);
		tabSale.setOnSelectionChanged(event -> sale.updateControls());

		// -------------FRIDAYPANE----------------------------------------------------

		Tab tabFriday = new Tab("Fredagsbar");
		tabPane.getTabs().add(tabFriday);
		tabFriday.setGraphic(controller.buildImage("Bar.png"));
		tabFriday.setClosable(false);

		FridayPane friday = new FridayPane();
		tabFriday.setContent(friday);
		tabFriday.setOnSelectionChanged(event -> friday.updateControls());

		// -------------CALENDERPANE----------------------------------------------------------

		Tab tabCalendar = new Tab("Kalender");
		tabPane.getTabs().add(tabCalendar);
		tabCalendar.setGraphic(controller.buildImage("Calendar.png"));
		tabCalendar.setClosable(false);

		CalendarPane calendar = new CalendarPane();
		tabCalendar.setContent(calendar);
		tabCalendar.setOnSelectionChanged(Event -> calendar.updateControls());

		// -------------STOCKPANE----------------------------------------------------------

		Tab tabWareHouse = new Tab("Lager");
		tabPane.getTabs().add(tabWareHouse);
		tabWareHouse.setClosable(false);
		tabWareHouse.setGraphic(controller.buildImage("Warehouse.png"));

		WareHousePane wareHousePane = new WareHousePane();
		tabWareHouse.setContent(wareHousePane);
		tabWareHouse.setOnSelectionChanged(Event -> wareHousePane.updateControls());

		// -------------FRIDAYJAM-----------------------------------------------------------------------

		Tab tabJam = new Tab("Fredags jam");
		tabPane.getTabs().add(tabJam);
		tabJam.setClosable(false);
		tabJam.setGraphic(controller.buildImage("music.png"));

		FridayJamPane fridayJam = new FridayJamPane();
		tabJam.setContent(fridayJam);
		tabJam.setOnSelectionChanged(Event -> fridayJam.updateControls());

	}

	private void initMenuBar(MenuBar menubar) {
		Menu menu1 = new Menu("Tilføj");
		Menu menu2 = new Menu("Statistik");
		Menu menu3 = new Menu("Reservation");
		Menu menu4 = new Menu("Oversigt");

		menubar.getMenus().addAll(menu1, menu3, menu2, menu4);

		// ----------- Create-----------------------------------------
		MenuItem tilføjØl = new MenuItem("Tilføj Øl");
		tilføjØl.setOnAction(event -> controller.tilføjØl());

		MenuItem addPriceList = new MenuItem("Tilføj Prisliste");
		addPriceList.setOnAction(event -> controller.addPriceList());

		MenuItem addKeg = new MenuItem("Tilføj Fustage");
		addKeg.setOnAction(event -> controller.addKeg());

		MenuItem addGift = new MenuItem("Tilføj Gave");
		addGift.setOnAction(event -> controller.addGift());

		MenuItem addCarbonDioxide = new MenuItem("Tilføj Kulsyre");
		addCarbonDioxide.setOnAction(event -> controller.addCarbonDioxide());

		MenuItem addBeerTap = new MenuItem("Tilføj Anlæg");
		addBeerTap.setOnAction(event -> controller.addBeerTap());

		menu1.getItems().addAll(tilføjØl, addKeg, addGift, addPriceList, addCarbonDioxide, addBeerTap);

		// ----------Statistik------------------------------------------

		MenuItem oneDay = new MenuItem("Dagens salg");
		oneDay.setOnAction(event -> controller.openTabTodaySale());

		MenuItem period = new MenuItem("Salg i Periode");
		period.setOnAction(event -> controller.openTabPeriodSale());

		MenuItem soldClip = new MenuItem("Solgte klip");
		soldClip.setOnAction(event -> controller.openTabSoldClip());

		menu2.getItems().addAll(oneDay, period, soldClip);

		// ----------Reservation----------------------------------------

		Menu guidedTour = new Menu("Rundvisning");
		Menu beerTap = new Menu("Anlæg");
		menu3.getItems().addAll(guidedTour, beerTap);

		MenuItem guidedTourReservation = new MenuItem("Reserver rundvisning");
		guidedTourReservation.setOnAction(event -> controller.createGuidedTourReservation());
		MenuItem guidedTourCalender = new MenuItem("Reservations kalender");
		guidedTour.getItems().addAll(guidedTourReservation, guidedTourCalender);

		MenuItem beerTapReservation = new MenuItem("Reserver anlæg");
		beerTapReservation.setOnAction(EventHandler -> controller.createBeerTapReservation());
		MenuItem beerTapCalender = new MenuItem("Reservations kalender");
		beerTap.getItems().addAll(beerTapReservation, beerTapCalender);

		// ------------Oversigt----------------------------------------------

		MenuItem customerIndex = new MenuItem("Kunde oversigt");
		MenuItem productIndex = new MenuItem("Sortiment oversigt");
		menu4.getItems().addAll(customerIndex, productIndex);

		customerIndex.setOnAction(event -> controller.openTabCustomerIndex());

		productIndex.setOnAction(event -> controller.openTabProductIndex());
	}

	// ---------------------------------------------------------------------------
	private class Controller {
		private TilføjØlDialog tilføjØlDialog;
		private AddGiftDialog addGiftDialog;
		private AddPriceListDialog addPriceListDialog;
		private AddCarbonDioxideDialog addCarbonDioxideDialog;
		private AddBeerTapDialog addBeerTapDialog;
		private CreateGuidedTourReservation createGuidedTourReservationDialog;
		private CreateBeerTapReservation createBeerTapReservationDialog;
		private AddKegDialog addKegDialog;

		// Kig på cloasable virker ikke
		public void openTabTodaySale() {
			Tab tabTodaySale = new Tab("Dagens salg");
			tabPane.getTabs().add(tabTodaySale);
			tabTodaySale.setClosable(true);
			tabTodaySale.setGraphic(buildImage("Calculator.png"));

			TodaysSalePane todaySale = new TodaysSalePane();
			tabTodaySale.setContent(todaySale);
			tabTodaySale.setOnSelectionChanged(event -> todaySale.updateControls());
			tabPane.getSelectionModel().select(tabTodaySale);
		}

		public void openTabPeriodSale() {
			Tab tabPeriodSale = new Tab("Periode salg");
			tabPane.getTabs().add(tabPeriodSale);
			tabPeriodSale.setClosable(true);
			tabPeriodSale.setGraphic(buildImage("Statictik.png"));

			PeriodSalePane periodSale = new PeriodSalePane();
			tabPeriodSale.setContent(periodSale);
			tabPeriodSale.setOnSelectionChanged(event -> periodSale.updateControls());
			tabPane.getSelectionModel().select(tabPeriodSale);
		}

		public void openTabSoldClip() {
			Tab tabSoldClip = new Tab("Solgte klip");
			tabPane.getTabs().add(tabSoldClip);
			tabSoldClip.setClosable(true);
			tabSoldClip.setGraphic(buildImage("Ticket.png"));

			SoldClipPane soldClip = new SoldClipPane();
			tabSoldClip.setContent(soldClip);
			tabSoldClip.setOnSelectionChanged(event -> soldClip.updateControls());
			tabPane.getSelectionModel().select(tabSoldClip);
		}

		public void openTabCustomerIndex() {
			Tab tabCustomerIndex = new Tab("Kunder");
			tabPane.getTabs().add(tabCustomerIndex);
			tabCustomerIndex.setClosable(true);
			tabCustomerIndex.setGraphic(buildImage("Customer.png"));

			CustomerIndexPane customerIndex = new CustomerIndexPane();
			tabCustomerIndex.setContent(customerIndex);
			tabCustomerIndex.setOnSelectionChanged(event -> customerIndex.updateControls());
			tabPane.getSelectionModel().select(tabCustomerIndex);
		}

		public void openTabProductIndex() {
			Tab tabProductIndex = new Tab("Sortiment");
			tabPane.getTabs().add(tabProductIndex);
			tabProductIndex.setClosable(true);
			tabProductIndex.setGraphic(buildImage("ProductIndex.png"));

			ProductIndexPane productIndex = new ProductIndexPane();
			tabProductIndex.setContent(productIndex);
			tabProductIndex.setOnSelectionChanged(event -> productIndex.updateControls());
			tabPane.getSelectionModel().select(tabProductIndex);
		}

		public void tilføjØl() {
			if (tilføjØlDialog == null) {
				tilføjØlDialog = new TilføjØlDialog("Opret ny øl");
				Stage stage = new Stage();
				tilføjØlDialog.initOwner(stage);
			}

			tilføjØlDialog.showAndWait();

			boolean isCreated = tilføjØlDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void addGift() {
			if (addGiftDialog == null) {
				addGiftDialog = new AddGiftDialog("Opret sampakning");
				Stage stage = new Stage();
				addGiftDialog.initOwner(stage);
			}
			addGiftDialog.showAndWait();

			boolean isCreated = addGiftDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void addPriceList() {
			if (addPriceListDialog == null) {
				addPriceListDialog = new AddPriceListDialog("Opret prisliste");
				Stage stage = new Stage();
				addPriceListDialog.initOwner(stage);
			}

			addPriceListDialog.showAndWait();

			boolean isCreated = addPriceListDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void addKeg() {
			if (addKegDialog == null) {
				addKegDialog = new AddKegDialog("Opret Fustage");
				Stage stage = new Stage();
				addKegDialog.initOwner(stage);
			}

			addKegDialog.showAndWait();

			boolean isCreated = addKegDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void addCarbonDioxide() {
			if (addCarbonDioxideDialog == null) {
				addCarbonDioxideDialog = new AddCarbonDioxideDialog("Opret kulsyre");
				Stage stage = new Stage();
				addCarbonDioxideDialog.initOwner(stage);
			}

			addCarbonDioxideDialog.showAndWait();

			boolean isCreated = addCarbonDioxideDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void addBeerTap() {
			if (addBeerTapDialog == null) {
				addBeerTapDialog = new AddBeerTapDialog("Opret anlæg");
				Stage stage = new Stage();
				addBeerTapDialog.initOwner(stage);
			}

			addBeerTapDialog.showAndWait();

			boolean isCreated = addBeerTapDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void createGuidedTourReservation() {
			if (createGuidedTourReservationDialog == null) {
				createGuidedTourReservationDialog = new CreateGuidedTourReservation();
				Stage stage = new Stage();
				createGuidedTourReservationDialog.initOwner(stage);
			}

			createGuidedTourReservationDialog.showAndWait();

			boolean isCreated = createGuidedTourReservationDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		public void createBeerTapReservation() {
			if (createBeerTapReservationDialog == null) {
				createBeerTapReservationDialog = new CreateBeerTapReservation();
				Stage stage = new Stage();
				createBeerTapReservationDialog.initOwner(stage);
			}

			createBeerTapReservationDialog.showAndWait();

			boolean isCreated = createBeerTapReservationDialog.getResult();
			if (isCreated) {
				// Do something - reload tabs
			}
		}

		// Helper method to add image in tabs
		private ImageView buildImage(String imgPatch) {
			Image i = new Image(imgPatch);
			ImageView imageView = new ImageView();
			// You can set width and height
			imageView.setFitHeight(16);
			imageView.setFitWidth(16);
			imageView.setImage(i);
			return imageView;
		}

		private void updatePane() {
			Tab t1 = tabPane.getSelectionModel().getSelectedItem();

			if (t1.getContent() instanceof SalePane) {
				SalePane p1 = (SalePane) t1.getContent();
				p1.updateControls();
			}

			if (t1.getContent() instanceof FridayPane) {
				FridayPane p1 = (FridayPane) t1.getContent();
				p1.updateControls();
			}

			if (t1.getContent() instanceof CalendarPane) {
				CalendarPane p1 = (CalendarPane) t1.getContent();
				p1.updateControls();
			}

			if (t1.getContent() instanceof TodaysSalePane) {
				TodaysSalePane p1 = (TodaysSalePane) t1.getContent();
				p1.updateControls();
			}
		}
	}

}
