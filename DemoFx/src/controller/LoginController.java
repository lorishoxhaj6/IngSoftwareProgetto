package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.AppUtils;
import model.DatabaseUtil;
import model.Doctor;
import model.Patient;

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
	    String username = userTextField.getText();
	    String password = passwordField.getText();
	    
	    if (username == null || username.isBlank() || password == null || password.isBlank()) {
	        AppUtils.showError("Errore di autenticazione", "Dati mancanti", "Inserisci username e password.");
	        cleanFillAutentication();
	        return;
	    }
	    if (RoleGroup.getSelectedToggle() == null) {
	        AppUtils.showError("Errore di autenticazione", "Ruolo non selezionato", "Seleziona Patient o Doctor.");
	        cleanFillAutentication();
	        return;
	    }

	    final boolean isPatient = rbPatient.isSelected();

	    try (Connection con = DatabaseUtil.connect()) {

	        if (isPatient) {
	            loginAsPatient(con, username, password);
	            
	        } else {
	            loginAsDoctor(con, username, password);
	            
	        }
	        
	        cleanFillAutentication();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        AppUtils.showError("Errore", "Problema di connessione", "Impossibile contattare il database.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        AppUtils.showError("Errore", "Caricamento vista", "Impossibile aprire la schermata.");
	    }
	}

	private void cleanFillAutentication() {
		userTextField.clear();
        passwordField.clear();
        RoleGroup.selectToggle(null);
	}

	private void loginAsPatient(Connection con, String username, String password) throws Exception {
	    String sql = "SELECT * FROM patients WHERE username = ? AND password = ?";
	    
	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, username);
	        ps.setString(2, password);

	        try (ResultSet rs = ps.executeQuery()) {
	            
	        	if (!rs.next()) {
	                AppUtils.showError("Errore di autenticazione", "Credenziali non valide", "Username o password errati.");
	                return;
	            }

	            // Creo oggetto paziente dal DB
	            Patient patientObj = new Patient(
	                rs.getString("username"),
	                rs.getString("password"),
	                rs.getInt("id"),
	                rs.getInt("doctor_id")
	            );
	            
	            PatientController controller = ViewNavigator.loadViewWithController("patientView.fxml");
	            controller.setUser(patientObj);
	   
	        }
	    }
	}
	
	private void loginAsDoctor(Connection con, String username, String password) throws Exception {
	    String sql = "SELECT * FROM doctors WHERE username = ? AND password = ?";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, username);
	        ps.setString(2, password);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) {
	                AppUtils.showError("Errore di autenticazione", "Credenziali non valide", "Username o password errati.");
	                return;
	            }

	            int doctorId = rs.getInt("id");  // qui prendi la PK "id" del medico
	            String email = rs.getString("email");
	            
	            // 2) Recupero lista pazienti associati
	            List<Patient> pazienti = new ArrayList<>();
	            String sqlPazienti = "SELECT * FROM patients WHERE doctor_id = ?";

	            try (PreparedStatement ps2 = con.prepareStatement(sqlPazienti)) {
	                ps2.setInt(1, doctorId);

	                try (ResultSet rs2 = ps2.executeQuery()) {
	                    while (rs2.next()) {
	                        pazienti.add(new Patient(
	                            rs2.getString("username"),
	                            rs2.getString("password"),
	                            rs2.getInt("doctor_id"),
	                            rs2.getInt("id")
	                        ));
	                    }
	                }
	            }

	            // 3) Creo oggetto dottore con la lista pazienti
	            Doctor doctorObj = new Doctor(username, password, doctorId, pazienti,email);

	            DoctorController controller = ViewNavigator.loadViewWithController("doctorView.fxml");
	            controller.setUser(doctorObj);
	        }
	    }
	}


	

}
