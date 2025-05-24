package controller;

import java.net.URL;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewNavigator {
	/*FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene2.fxml"));
	root = loader.load();
	
	Scene2Controller scene2Controller = loader.getController();
	scene2Controller.displayName(username);
	
	stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //prende lo stage corrente
	// event.setSource() restituisce il nodo che ha causato l'evento = il bottone
	// .getScene dice la scena in cui è il bottone
	// .getWindow dice lo stage in cui è la scena
	scene = new Scene(root);
	stage.setScene(scene);
	stage.show();*/
	private static Parent root;
	private static Stage stage;
	private static Scene scene;
	
	public static void setMainStage(Stage s) {
		stage = s;
	}
	
	public static void loadView(String fxml) {
		try {
			URL mainViewUrl = Main.class.getResource("/view/" + fxml);
			FXMLLoader loader = new FXMLLoader(mainViewUrl);
			root = loader.load();
			scene = new Scene(root);
			scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("impossibile caricare la vista");
		}

	}
}
