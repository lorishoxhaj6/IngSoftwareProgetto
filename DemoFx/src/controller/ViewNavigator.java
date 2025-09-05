package controller;

import java.io.IOException;
import java.net.URL;

import application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
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
	
	public static <T> T loadViewOver(String fxml,String title) throws IOException {
		URL mainViewUrl = Main.class.getResource("/view/" + fxml);
		FXMLLoader loader = new FXMLLoader(mainViewUrl);
		root = loader.load();
		T controller = loader.getController();
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(new Scene(root));
		stage.initModality(Modality.APPLICATION_MODAL); // blocca interazione con la finestra sotto
		stage.show();
		return controller;
	}
	
	public static <T> T loadViewWithController(String fxml) {
		try {
			URL mainViewUrl = Main.class.getResource("/view/" + fxml);
			FXMLLoader loader = new FXMLLoader(mainViewUrl);
			root = loader.load();
			T controller = loader.getController();
			scene = new Scene(root);
			scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			return controller;
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("impossibile caricare la vista");
			return null;
		}
	}
}
