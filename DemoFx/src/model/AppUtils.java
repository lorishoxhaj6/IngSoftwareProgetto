package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

public class AppUtils {

//prova	
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

	public static void intializeMeasurementUnit(ComboBox<String> s) {
		s.getItems().addAll(
			    "mg",          // milligrammi
			    "ml",          // millilitri
			    "UI",          // unità internazionali (es. insulina)
			    "compresse",   // numero di compresse
			    "gocce"        // utile per certi farmaci liquidi
			);
		
	}

	public static List<Patient> findAllPatient(int doctorId) throws SQLException {
		Connection con = DatabaseUtil.connect();
		List<Patient> pazienti = new ArrayList<>();
        String sqlPazienti = "SELECT * FROM patients WHERE doctor_id = ?";
        
        try (PreparedStatement ps2 = con.prepareStatement(sqlPazienti)) {
            ps2.setInt(1, doctorId);

            try (ResultSet rs2 = ps2.executeQuery()) {
                while (rs2.next()) {
                    pazienti.add(new Patient(
                        rs2.getString("username"),
                        rs2.getString("password"),
                        rs2.getInt("id"),
                        rs2.getInt("doctor_id"),
                        rs2.getString("name"),
    	                rs2.getString("surname")
                    ));
                }
            }
        }

        // 3) Creo oggetto dottore con la lista pazienti
        return pazienti;
		
	}

}
