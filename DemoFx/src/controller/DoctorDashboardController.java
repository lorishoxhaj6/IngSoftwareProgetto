package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Patient;
import model.SharedDataModelDoc;

public class DoctorDashboardController extends DoctorController implements Initializable{

    @FXML
    private TextField allergyField;

    @FXML
    private TextField amount;

    @FXML
    private LineChart<?, ?> bloodSugarGraph;

    @FXML
    private TextField comorbitField;

    @FXML
    private Button delTherapyBut;

    @FXML
    private ListView<?> historyView;

    @FXML
    private TextArea infoPatients;

    @FXML
    private ListView<?> mailBox;

    @FXML
    private TextField medicineField;

    @FXML
    private Button modifyTherapyBut;

    @FXML
    private TextField numberOfIntakes;

    @FXML
    private TextArea otherIndication;

    @FXML
    private TextField pathologiesField;

    @FXML
    private ListView<Patient> patientsListView;

    @FXML
    private TextField riskField;

    @FXML
    private Button saveButton;

    @FXML
    private ListView<?> symptomsMedicinesView;

    @FXML
    private TableView<?> tableTherapyView;

    @FXML
    private Button visualizeButton;
    

    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		
	}
    
    public void setEnviroment(SharedDataModelDoc instance, Patient selectedPatient) {
    	this.instance = instance;
    	patientsListView.setItems(instance.getItemList());
    	
    }

	@FXML
	public void visualize(ActionEvent event) {
		patientsListView.setItems(instance.getItemList());
	}

	@FXML
    void deleteTherapy(ActionEvent event) {

    }

    @FXML
    void insertInMedicalHistory(ActionEvent event) {

    }

    @FXML
    void insertTherapy(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {
    	super.logout();
    }

    @FXML
    void modifyTherapy(ActionEvent event) {

    }

    @FXML
    void removeFromMedicalHistory(ActionEvent event) {

    }

}
