package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
	
	@FXML
	private TextField userTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Hyperlink regestratiHyperlink;
	
	
	public void home(ActionEvent event) {
		ViewNavigator.loadView("homeView.fxml");
	}
	
	public void register(ActionEvent event) {
		ViewNavigator.loadView("registerView.fxml");
	}
	
}
