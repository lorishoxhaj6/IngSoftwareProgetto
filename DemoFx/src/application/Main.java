package application;
	
import java.net.URL;
import java.sql.SQLException;

import controller.ViewNavigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DatabaseConnection;


public class Main extends Application {
	@Override
	//Loris: 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
	//Andrew: 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
	public void start(Stage stage) {
		try {
			ViewNavigator.setMainStage(stage);
			//carica la homeView
			ViewNavigator.loadView("loginView.fxml");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SQLException {
		DatabaseConnection.connect();
		launch(args);
	}
}
