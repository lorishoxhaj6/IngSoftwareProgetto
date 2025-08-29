package controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import model.Misurazione;
import model.Patient;

public class PatientController extends UserController<Patient>{
	//usa superclasse ma con Patient e non con un tipo generico
	
	@FXML
	TextField valueTextField;
	@FXML
	RadioButton primaPastoRb, dopoPastoRb;
	@FXML
	DatePicker myDatePicker;
	
	public void inserisciMisurazione() {
		//fai Query
	}
	
	
}
