package gui.panes;

import java.io.File;
import java.text.DecimalFormat;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class FridayJamPane extends GridPane {
	private final Controller controller = new Controller();

	public FridayJamPane() {
		this.initContent();
	}

	// -----------------------------------------------------------------------------

	private final Button btnPlay = new Button();
	private final Button btnPause = new Button();
	private final Button btnStop = new Button();
	private final Label lblTotalDuration = new Label();
	private final Label lblCurrentDuration = new Label();
	private final DecimalFormat dfFormatter = new DecimalFormat("00.0");
	private final SliderBar timeSlider = new SliderBar();
	private Duration totalTime;

	private final Slider volSlider = new Slider(0, 1, 1);
	private final Label lblvolume = new Label();

	private final File file = new File("src/Sydpo.mp3");

	private final Media song = new Media(file.toURI().toString());

	private final MediaPlayer player = new MediaPlayer(song);

	private final MediaView mediaView = new MediaView();

	private final Label lblCurrentlyPlaying = new Label();
	private final int FILE_EXTENSION_LEN = 3;

	public void initContent() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(10);
		this.setHgap(10);
		this.setAlignment(Pos.CENTER);

		this.add(lblCurrentlyPlaying, 0, 0);

		HBox box1 = new HBox(10);
		this.add(box1, 0, 1);
		box1.getChildren().addAll(timeSlider, lblCurrentDuration, lblTotalDuration);

		HBox.setHgrow(timeSlider, Priority.ALWAYS);

		HBox box2 = new HBox(10);
		this.add(box2, 0, 2);
		box2.getChildren().addAll(btnPlay, btnPause, btnStop);

		Label volume = new Label("Volume");
		HBox box3 = new HBox(10);
		this.add(box3, 0, 3);
		box3.getChildren().addAll(volume, volSlider, lblvolume);
		controller.volume();

		btnPlay.setGraphic(controller.buildImage("play.png"));
		btnPlay.setOnAction(event -> controller.play());

		btnPause.setGraphic(controller.buildImage("pause.png"));
		btnPause.setOnAction(event -> controller.pause());

		btnStop.setGraphic(controller.buildImage("Stop.png"));
		btnStop.setOnAction(event -> controller.stop());

		player.volumeProperty().bindBidirectional(volSlider.valueProperty());
		ChangeListener<Number> listener1 = (ov, o, n) -> controller.volume();
		volSlider.valueProperty().addListener(listener1);

		// Update the label of the current playing song
		mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
			@Override
			public void changed(ObservableValue<? extends MediaPlayer> observableValue, MediaPlayer oldPlayer,
					MediaPlayer newPlayer) {
				controller.updateCurrentPlaying(newPlayer);
			}
		});

		controller.updateCurrentPlaying(player);
		controller.progressbar(player);
	}

	// ----------------------------------------------------------------------------

	public void updateControls() {
		controller.updateControls();
	}

	// -----------------------------------------------------------------------------

	private class Controller {
		private void updateControls() {

		}

		private void play() {
			player.play();
		}

		private void pause() {
			player.pause();
		}

		private void stop() {
			player.stop();
		}

		private void volume() {
			lblvolume.setText(String.valueOf(dfFormatter.format(volSlider.valueProperty().getValue() * 100)) + " %");
		}

		/**
		 * Update the label of the current playing song
		 */
		private void updateCurrentPlaying(MediaPlayer player) {
			String source = player.getMedia().getSource();
			source = source.substring(0, source.length() - FILE_EXTENSION_LEN);
			source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
			lblCurrentlyPlaying.setText("Now Playing: " + source);
		}

		/**
		 * Update the progressbar
		 */
		private void progressbar(MediaPlayer player) {
			player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					timeSlider.sliderValueProperty().set(newValue.divide(totalTime.toMillis()).toMillis() * 100.0);
					lblCurrentDuration.setText(String.valueOf(dfFormatter.format(newValue.toSeconds())));
				}
			});

			player.setOnReady(() -> {
				// set the total duration
				totalTime = player.getMedia().getDuration();
				lblTotalDuration.setText(" / " + String.valueOf(dfFormatter.format(Math.floor(totalTime.toSeconds()))));
			});

			timeSlider.sliderValueProperty().addListener((ov) -> {
				if (timeSlider.isTheValueChanging()) {
					if (player != null) {
						// multiply duration by percentage calc by slider pos
						player.seek(totalTime.multiply(timeSlider.sliderValueProperty().getValue() / 100.0));
					}
				}
			});
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
	}

	private class SliderBar extends StackPane {
		private Slider slider = new Slider();

		private ProgressBar progressBar = new ProgressBar();

		public SliderBar() {
			getChildren().addAll(progressBar, slider);
			bindValues();
		}

		private void bindValues() {
			progressBar.prefWidthProperty().bind(slider.widthProperty());
			progressBar.progressProperty().bind(slider.valueProperty().divide(100));
		}

		public DoubleProperty sliderValueProperty() {
			return slider.valueProperty();
		}

		public boolean isTheValueChanging() {
			return slider.isValueChanging();
		}
	}

}
