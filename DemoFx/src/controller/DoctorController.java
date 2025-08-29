package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Doctor;

public class DoctorController {
	@FXML
	Label userLabel;
	
	public void setUser(Doctor doctor) {
		userLabel.setText(doctor.getUsername());
	}
}
