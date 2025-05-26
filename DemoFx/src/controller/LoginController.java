package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	public void authentication(ActionEvent event) throws ClassNotFoundException, SQLException {

		String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

		// connect
		Class.forName("com.mysql.cj.jdbc.Driver");

		// step 2 Establish the connection
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AuthDB_Hospital", "root", "Root1234");

		PreparedStatement ps = con.prepareStatement(query);

		ps.setString(1, userTextField.getText());
		ps.setString(2, passwordField.getText());

		System.out.println("conessione effettuata!");

		ResultSet ris = ps.executeQuery();

		if (ris.next()) {
			System.out.println("login riuscito");
			System.out.println(ris.getString("password_hash"));
		} else {
			System.out.println("login non riuscito");
		}
		


	}

}
