package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

public class AppUtils {

	public static void showError(String title, String header, String content) {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(content);
		a.showAndWait();
	}

	public static void showConfirmation(String title, String header, String content) {
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(content);
		a.showAndWait();
	}

	public static void showInfo(String title, String header, String content) {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(content);
		a.showAndWait();
	}

	public static boolean showConfirmationWithBoolean(String title, String header, String content) {
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(content);

		Optional<ButtonType> result = a.showAndWait();
		return result.get() == ButtonType.OK;
	}

	public String getDateTimeFormatted(LocalDateTime dateTime) {
		DateTimeFormatter OUT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return (dateTime == null) ? "" : dateTime.format(OUT);
	}

	public static void colorMeasurments(TableColumn<Measurement, Double> valueColumn) {
		// serve per colorare i risultati della colonna value
		valueColumn.setCellFactory(col -> new TableCell<Measurement, Double>() {
			// setCell serve a personalizzare come sono disegnate le celle
			// TableCell è una cella della tabella
			protected void updateItem(Double n, boolean empty) {
				super.updateItem(n, empty);
				// gestione caso cella vuota
				if (empty || n == null) {
					setText(null);
					setTextFill(Color.BLACK);
					return;
				}

				double value = n.doubleValue();
				setText(String.valueOf(value));

				Measurement m = getTableView().getItems().get(getIndex());
				String moment = m.getMoment();

				if ("prima pasto".equals(moment)) {
					if (value >= 80 && value <= 130) {
						setTextFill(Color.GREEN);
					} else if ((value >= 50 && value < 80) || (value > 130 && value <= 160)) {
						setTextFill(Color.ORANGE);
					} else { // <50 o >160
						setTextFill(Color.RED);
					}
				} else { // dopo pasto
					if (value < 180) {
						setTextFill(Color.GREEN);
					} else if (value > 190 && value <= 210) {
						setTextFill(Color.ORANGE);
					} else { // 180-190 inclusi oppure >210
						setTextFill(Color.RED);
					}
				}

			}
		});
	}

}
