package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.AppUtils;
import model.Doctor;
import model.Patient;

public class DoctorController extends UserController<Doctor>{
	//usa la superclasse UserController ma il tipo genetico T divente di tipo Doctor
	@FXML
	ListView patientsListView;
	@FXML
	Button visualizeButton;
	
	
	public void setUser(Doctor user) {
		super.setUser(user);
		// serve per visualizzare i pazienti
		ObservableList<Patient> observableList = FXCollections.observableArrayList(user.getPatients());
		patientsListView.setItems(observableList);
	}
	
	public void  visualize(ActionEvent e) {
		Patient selectedPatient = (Patient) patientsListView.getSelectionModel().getSelectedItem();
		if(selectedPatient != null) {
			DoctorDashboardController docControl = ViewNavigator.loadViewWithController("doctorViewDashboard.fxml");
			//docControl.setUser(selectedPatient); metodo ancora da creare
		}
		else
			AppUtils.showError("Errore caricamento", "no patient selected", "Please. select a patient to visualize");
			
	}
	
}
