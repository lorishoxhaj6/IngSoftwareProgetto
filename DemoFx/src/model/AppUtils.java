package model;

import javafx.scene.control.Alert;

public class AppUtils {

	
	public static void showError(String title, String header, String content) {
	    Alert a = new Alert(Alert.AlertType.ERROR);
	    a.setTitle(title);
	    a.setHeaderText(header);
	    a.setContentText(content);
	    a.showAndWait();
	}
}
