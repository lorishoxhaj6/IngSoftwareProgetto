package controller;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.AppUtils;
import model.DatabaseUtil;
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
	
	
	public void setUser(Doctor user) {
        super.setUser(user);

        // passa il doctor al tab
        patientTabViewController.setDoctor(user);

        // carica tutte le liste 
        patientTabViewController.setAllPatients(loadAllPatients());
        patientTabViewController.setFilteredPatient(
            FXCollections.observableArrayList(user.getPatients())
        );

        //  quando clicchi "visualize" nel tab 
        //  apri la Dashboard
        patientTabViewController.setOnVisualize(selectedPatient -> {
            try {
                DoctorDashboardController docControl =
                    ViewNavigator.loadViewWithController("doctorViewDashboard.fxml");
               
                docControl.setEnviroment(selectedPatient, user);
            } catch (SQLException e) {
                AppUtils.showError("DB error", "Caricamento dashboard fallito", e.getMessage());
            }
        });
    }
	

	public void logout() {
		super.logout();
	}
	
	 protected ObservableList<Patient> loadAllPatients() {
	        try {
	            return DatabaseUtil.queryList(
	                "SELECT id, username, password, doctor_id, name, surname FROM patients",
	                null,
	                rs -> new Patient(
	                    rs.getString("username"),
	                    rs.getString("password"),
	                    rs.getInt("id"),
	                    rs.getInt("doctor_id"),
	                    rs.getString("name"),
	                    rs.getString("surname")
	                )
	            );
	        } catch (SQLException e) {
	            AppUtils.showError("DB error", "Caricamento pazienti fallito", e.getMessage());
	            return FXCollections.observableArrayList();
	        }
	    }
	}
	
	