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

		// 1) Validazioni base
	    String username = userTextField.getText();
	    String password = passwordField.getText();

	    if (username == null || username.isBlank() || password == null || password.isBlank()) {
	        showError("Errore di autenticazione", "Dati mancanti", "Inserisci username e password.");
	        return;
	    }
	    if (RoleGroup.getSelectedToggle() == null) {
	        showError("Errore di autenticazione", "Ruolo non selezionato", "Seleziona Patient o Doctor.");
	        return;
	    }

	    // 2) Query in base al ruolo scelto
	    final boolean isPatient = rbPatient.isSelected();
	    final String sqlQuery = isPatient
	            ? "SELECT COUNT(*) FROM patient WHERE username = ? AND password = ?"
	            : "SELECT COUNT(*) FROM doctor  WHERE username = ? AND password = ?";

	    try (Connection con = DatabaseConnection.connect();
	         PreparedStatement ps = con.prepareStatement(sqlQuery)) {

	        ps.setString(1, username);
	        ps.setString(2, password);

	        try (ResultSet rs = ps.executeQuery()) {
	            boolean found = rs.next() && rs.getInt(1) > 0;
	            if (!found) {
	                showError("Errore di autenticazione", "Credenziali non valide", "Username o password errati.");
	                return;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        showError("Errore", "Problema di connessione", "Impossibile contattare il database.");
	        return;
	    }

	    // 3) Routing
	    try {
	        if (isPatient) {
	        	/*Patient patientObj = new Patient();*/
	            PatientController patient = ViewNavigator.loadViewWithController("patientView.fxml");
	            patient.setUser(username);
	        } else {
	        	/*Doctor doctorObj = new Doctor();*/
	            DoctorController doctor = ViewNavigator.loadViewWithController("doctorView.fxml");
	            // doctor.setUser(username);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        showError("Errore", "Caricamento vista", "Impossibile aprire la schermata.");
	    }
	}

	private void showError(String title, String header, String content) {
	    Alert a = new Alert(Alert.AlertType.ERROR);
	    a.setTitle(title);
	    a.setHeaderText(header);
	    a.setContentText(content);
	    a.showAndWait();
	}

}
