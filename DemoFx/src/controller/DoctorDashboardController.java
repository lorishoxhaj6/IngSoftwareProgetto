package controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Measurement;
import model.Patient;
import model.SharedDataModelDoc;

public class DoctorDashboardController extends DoctorController implements Initializable{

    @FXML
    private TextField allergyField;

    @FXML
    private TextField amount;

    @FXML
    private LineChart<String, Number> bloodSugarGraph;

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
    
    public void setEnviroment(SharedDataModelDoc instance, Patient selectedPatient) throws SQLException {
    	this.instance = instance;
    	patientsListView.setItems(instance.getItemList());
    	
    	if (selectedPatient != null) {
            // Recupera le misurazioni del paziente e aggiorna il grafico
            List<Measurement> misurazioni = selectedPatient.getMeasurementBloodSugar(selectedPatient); 
            updateGraphBloodSugar(misurazioni);
        }
    	
    }

	@FXML
	public void visualize() throws SQLException {
		super.visualize();
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
    
  

    public void updateGraphBloodSugar(List<Measurement> measurements) {
        bloodSugarGraph.getData().clear();

        if (measurements.isEmpty()) {
            return;
        }

        // 1. Definisci il periodo settimanale
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        // 2. Filtra le misurazioni per includere solo quelle dell'ultima settimana
        List<Measurement> weeklyMeasurements = measurements.stream()
                .filter(m -> {
                    LocalDate measurementDate = m.getDateTime().toLocalDate();
                    return !measurementDate.isBefore(sevenDaysAgo) && !measurementDate.isAfter(today);
                })
                .collect(Collectors.toList());
                
        // Se la lista filtrata è vuota, non fare nulla
        if (weeklyMeasurements.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Andamento Glicemia Settimanale");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM", Locale.ITALY);

        // Ordina solo le misurazioni settimanali
        weeklyMeasurements.sort((m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()));

        for (Measurement m : weeklyMeasurements) {
            String dataFormattata = m.getDateTime().format(formatter);
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dataFormattata, m.getValue());
            series.getData().add(dataPoint);
        }

        bloodSugarGraph.getData().add(series);
    }

}
