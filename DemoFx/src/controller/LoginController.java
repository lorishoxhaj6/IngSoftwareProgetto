package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import dao.jdbc.JdbcIntakeDao;
import dao.jdbc.JdbcMeasurementDao;
import dao.jdbc.JdbcPatientDao;
import dao.jdbc.JdbcPrescriptionDao;
import dao.jdbc.JdbcSymptomDao;
import facade.ClinicFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
	@FXML
	private Button loginButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// quando premo ENTER è come se premessi il loginButton
		loginButton.setDefaultButton(true);
	}

	public void handleLogin(ActionEvent event) throws ClassNotFoundException {
	    String username = userTextField.getText();
	    String password = passwordField.getText();
	    ClinicFacade clinic = new ClinicFacade(new JdbcPatientDao(), new JdbcMeasurementDao(), 
	    		new JdbcSymptomDao(), new JdbcPrescriptionDao(), new JdbcIntakeDao());
	    
	    if (username == null || username.isBlank() || password == null || password.isBlank()) {
	        AppUtils.showError("Errore di autenticazione", "Dati mancanti", "Inserisci username e password.");
	        return;
	    }
	    if (RoleGroup.getSelectedToggle() == null) {
	        AppUtils.showError("Errore di autenticazione", "Ruolo non selezionato", "Seleziona Patient o Doctor.");  
	        return;
	    }

	    final boolean isPatient = rbPatient.isSelected();

	    try (Connection con = DatabaseUtil.connect()) {

	        if (isPatient) {
	            loginAsPatient(con, username, password,clinic);
	            
	        } else {
	            loginAsDoctor(con, username, password,clinic);  
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

	private void loginAsPatient(Connection con, String username, String password,ClinicFacade clinic) throws Exception {
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
	                rs.getInt("doctor_id"),
	                rs.getString("name"),
	                rs.getString("surname")
	            );
	            
	            PatientController controller = ViewNavigator.loadViewWithController("patientView.fxml");
	            controller.setClinic(clinic);
	            controller.setUser(patientObj);
	   
	        }
	    }
	}
	
	private void loginAsDoctor(Connection con, String username, String password, ClinicFacade clinic) throws Exception {
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
	            List<Patient> pazienti = AppUtils.findAllPatient(doctorId);
	            Doctor doctorObj = new Doctor(username, password, doctorId, pazienti,email);
	            
	            DoctorController controller = ViewNavigator.loadViewWithController("doctorView.fxml");
	            controller.setClinic(clinic);
	            controller.setUser(doctorObj);
	        }
	    }
	}


	

}