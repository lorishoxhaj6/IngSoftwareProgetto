package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AppUtils {

	
	public static void showError(String title, String header, String content) {
	    Alert a = new Alert(Alert.AlertType.ERROR);
	    a.setTitle(title);
	    a.setHeaderText(header);
	    a.setContentText(content);
	    a.showAndWait();
	}
	
	public static void showConfirmation(String title, String header, String content) {
	    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
	    a.setTitle(title);
	    a.setHeaderText(header);
	    a.setContentText(content);
	    a.showAndWait();
	}
	
	public static void showInfo(String title, String header, String content) {
	    Alert a = new Alert(Alert.AlertType.INFORMATION);
	    a.setTitle(title);
	    a.setHeaderText(header);
	    a.setContentText(content);
	    a.showAndWait();
	}
	
	public static boolean showConfirmationWithBoolean(String title, String header, String content) {
	    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
	    a.setTitle(title);
	    a.setHeaderText(header);
	    a.setContentText(content);
	    
	    Optional<ButtonType> result = a.showAndWait();
	    return  result.get() == ButtonType.OK;
	}
	
	public String getDateTimeFormatted(LocalDateTime dateTime) {
	    DateTimeFormatter OUT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    return (dateTime == null) ? "" : dateTime.format(OUT);
	}

}
