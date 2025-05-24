package controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class HomeController {
			
	public void login(ActionEvent event) {
		ViewNavigator.loadView("loginView.fxml");
	}
	
	public void register() {
		ViewNavigator.loadView("registerView.fxml");
	}
	
}
