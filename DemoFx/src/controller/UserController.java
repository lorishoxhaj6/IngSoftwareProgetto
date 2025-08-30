package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;

public abstract class UserController <T extends User> {
	//significa che il tipo T deve essere una sottoclasse di User
	protected T user;
	@FXML
	Label userLabel;
	
	public void setUser(T user) {
		this.user = user;
		userLabel.setText(user.getUsername()); //se non avessi messo extends User qui 
		//avrei dovuto fare il cast per trovare il metodo getUsername -> ((User)user).getUsername())
	}
	
	public void logout() {
		ViewNavigator.loadView("loginView.fxml");
	}
	
}
