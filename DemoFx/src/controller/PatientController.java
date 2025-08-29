package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PatientController /*implements Controller*/{
	@FXML
	Label userLabel;
	
	public void setUser(String username) {
		userLabel.setText(username);
	}
	
	public void logout() {
		ViewNavigator.loadView("loginView.fxml");
	}
}
