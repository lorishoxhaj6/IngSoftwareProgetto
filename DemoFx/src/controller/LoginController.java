package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable{

	@FXML
	private TextField userTextField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private ChoiceBox<String> loginChoiceBox;
	private String[] tipi = {"Patient", "Doctor"};

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loginChoiceBox.getItems().addAll(tipi);
		//loginChoiceBox.setOnAction();
		//loginChoiceBox.getValue;
		
	}

	public void login(ActionEvent event) throws ClassNotFoundException, SQLException {

	}

}
