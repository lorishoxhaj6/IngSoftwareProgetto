package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.DatabaseConnection;

public class LoginController implements Initializable {

	@FXML
	private TextField userTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private RadioButton rbPatient;
	@FXML
	private RadioButton rbDoctor;
	@FXML
	private ToggleGroup RoleGroup;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	public void handleLogin(ActionEvent event) throws ClassNotFoundException {

		final String sqlQuery = "SELECT COUNT(*) FROM patient WHERE username = ? AND password = ?";

		try (Connection con = DatabaseConnection.connect(); PreparedStatement ps = con.prepareStatement(sqlQuery)) {

			ps.setString(1, userTextField.getText());
			ps.setString(2, passwordField.getText());

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) { // indica ho trovato un utente che ha username e pw corrispondenti
					if (rbPatient.isSelected()) {
						PatientController patient = ViewNavigator.loadViewWithController("patientView.fxml");
						patient.setUser(userTextField.getText());
					} else {
						if (rbDoctor.isSelected()) {
							DoctorController doctor = ViewNavigator.loadViewWithController("doctorView.fxml");
							//doctor.setUser(userTextField.getText());
						}
						authenticationError(); // in caso l'utente non ha selezionato nessuno degli rbutton facciamo partire un alert
					}
				}else {
					authenticationError(); // in caso password username errate/Null
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}

	private void authenticationError() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		if (RoleGroup.getSelectedToggle() == null) {
			alert.setTitle("Errore di autenticazione");
			alert.setHeaderText("Ruolo non selezionato");
			alert.setContentText("Per favore selezionare 'Patient' o 'Doctor'prima di continuare");
			alert.showAndWait();
		} else {
			alert.setTitle("Errore di autenticazione");
			alert.setHeaderText("Errore inserimento dati");
			alert.setContentText("Username e password non corrispondenti");
			alert.showAndWait(); 
		}
	}

}
