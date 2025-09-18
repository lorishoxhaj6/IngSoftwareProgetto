package controller;

import java.sql.SQLException;

import facade.AlertService;
import facade.ClinicFacade;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.AppUtils;
import model.Doctor;
import model.Patient;

public class DoctorController extends UserController<Doctor>{
	//usa la superclasse UserController ma il tipo generico T divente di tipo Doctor
	@FXML
	private ListView<Patient> patientsListView;
	@FXML
	private Button visualizeButton;
	@FXML
	PatientTabViewController patientTabViewController;
	
	protected ClinicFacade clinic;
	protected AlertService alertService;
	
	public void setUser(Doctor user) {
        super.setUser(user);

        // passa il doctor al tab
        patientTabViewController.setDoctor(user);
        
        StringBuilder error = null;
		try {
			error = alertService.checkLastFreeDaysIntake(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}

        if (error != null && error.length() > 0) {
            AppUtils.showError(
                "Pazienti che non seguono le prescrizioni",
                "Alcuni pazienti non hanno rispettato la terapia prescritta:",
                error.toString()
            );
        }
        
        // carica tutte le liste 
        try {
			patientTabViewController.setAllPatients(FXCollections.observableArrayList(clinic.loadAllPatients()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
        patientTabViewController.setFilteredPatient(
            FXCollections.observableArrayList(user.getPatients())
        );

        //  quando clicchi "visualize" nel tab 
        //  apri la Dashboard
        patientTabViewController.setOnVisualize(selectedPatient -> {
            try {
                DoctorDashboardController docControl =
                    ViewNavigator.loadViewWithController("doctorViewDashboard.fxml");
                docControl.setClinic(clinic);
                docControl.setAlertService(alertService);
                docControl.setEnviroment(selectedPatient, user);
            } catch (SQLException e) {
                AppUtils.showError("DB error", "Caricamento dashboard fallito", e.getMessage());
            }
        });
    }
	

	public void logout() {
		super.logout();
	}
	
	 public void setClinic(ClinicFacade clinic) {
		this.clinic = clinic;
	 }

	 public void setAlertService(AlertService s) {
		 this.alertService = s;
	 }
	}
	
	