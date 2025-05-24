package application;
	
import java.net.URL;

import controller.ViewNavigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	//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
	public void start(Stage stage) {
		try {
			ViewNavigator.setMainStage(stage);
			//carica la homeView
			ViewNavigator.loadView("HomeView.fxml");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
