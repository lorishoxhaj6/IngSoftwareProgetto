package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Doctor;

public class DoctorController extends UserController<Doctor>{
	//usa la superclasse UserController ma il tipo genetico T divente di tipo Doctor
	
	public void setUser(Doctor user) {
		super.setUser(user);
	}
}
