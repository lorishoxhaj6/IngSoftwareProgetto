package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.AppUtils;
import model.Doctor;
import model.Patient;
import model.SharedDataModelDoc;

public class DoctorController extends UserController<Doctor>{
	//usa la superclasse UserController ma il tipo genetico T divente di tipo Doctor
	@FXML
	private ListView<Patient> patientsListView;
	@FXML
	private Button visualizeButton;
	
	protected SharedDataModelDoc instance;
	
	
	
	public void setUser(Doctor user, SharedDataModelDoc docModel) {
		super.setUser(user);
		this.instance = docModel;
		// serve per visualizzare i pazienti
		patientsListView.setItems(instance.getItemList());
	}
	
	public void visualize(ActionEvent e) {
		Patient selectedPatient = (Patient) patientsListView.getSelectionModel().getSelectedItem();
		if(selectedPatient != null) {
			DoctorDashboardController docControl = ViewNavigator.loadViewWithController("doctorViewDashboard.fxml");
			docControl.setEnviroment(this.instance,selectedPatient);
		}
		else
			AppUtils.showError("Errore caricamento", "no patient selected", "Please. select a patient to visualize");
			
	}
	
}
