package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DoctorController {
	@FXML
	Label userLabel;
	
	public void setUser(String username) {
		userLabel.setText(username);
	}
}
