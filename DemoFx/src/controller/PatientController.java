package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Patient;

public class PatientController /*implements Controller*/{
	@FXML
	Label userLabel;
	
	public void setUser(Patient patient) {
		userLabel.setText(patient.getUsername());
	}
	
	public void logout() {
		ViewNavigator.loadView("loginView.fxml");
	}
}
