package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.AppUtils;
import model.DatabaseUtil;
import model.Doctor;
import model.Measurement;
import model.Patient;
import model.SharedDataModelDoc;
import model.Symptoms;

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
    private ListView<Symptoms> historyView;

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
    @FXML
    private Label namePatientLabel;
    
    private Doctor doctor;
    private Patient patient;
    

    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
    
    public void setEnviroment(SharedDataModelDoc instance, Patient selectedPatient, Doctor doctor) throws SQLException {
    	this.instance = instance;
    	this.doctor = doctor;
    	this.patient = selectedPatient;
    	//aggiono label col nome del paziente che sto visualizzando
    	namePatientLabel.setText("nome Paziente: " + selectedPatient.toString());
    	// qui serve per avere la lista dei pazienti
    	patientsListView.setItems(instance.getItemList());
    	
    	if (selectedPatient != null) {
            // Recupera le misurazioni del paziente e aggiorna il grafico
            List<Measurement> misurazioni = selectedPatient.getMeasurementBloodSugar(selectedPatient); 
            // serve per ternere il grafico aggiornato
            updateGraphBloodSugar(misurazioni);
        }
    	// aggiorno textArea con le note fatte dal dottore
    	loadInformations();
    	//popola la listView delle segnalazioni dei sintomi e delle cure concomitanti
    	String sqlSymptoms = "SELECT id,symptoms, startDateTime, notes FROM symptoms WHERE patient_id = ? AND endDateTime IS NULL";
		ObservableList<Symptoms> symptoms = FXCollections.observableArrayList();
    	try {
			symptoms = DatabaseUtil.queryList(sqlSymptoms, ps -> {
				try {
					ps.setInt(1, patient.getPatientId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}, rs -> {
				int symptomId = rs.getInt("id");
				String raw = rs.getString("startDateTime");
				LocalDateTime date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				String sympt = rs.getString("symptoms");
				String notes = rs.getString("notes");
	
				return new Symptoms(symptomId, patient.getMedicoId(), patient.getPatientId(), sympt, date, notes);
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
    	historyView.setItems(symptoms);
    	
    }

	@FXML
	public void visualize() throws SQLException {
		super.visualize();
	}

	public void updateNotes(ActionEvent event) {
		boolean confermation = AppUtils.showConfirmationWithBoolean("data update ", "data updated", "sure to update data?");
		System.out.println("prova");
		String newText = infoPatients.getText();
		String sql = "UPDATE patients SET informations = ? WHERE id = ?";
		if(confermation) {
			try(Connection con = DatabaseUtil.connect();
	    			PreparedStatement ps = con.prepareStatement(sql)){
	    		ps.setString(1, newText);
				ps.setInt(2, patient.getPatientId());
	    		
	    		int rowAffected = ps.executeUpdate();
	    		if(rowAffected > 0)
	    			AppUtils.showInfo("data updated! ", "data updated", "new data has been saved");
	    		else
	    			AppUtils.showError("Errore caricamento dati", "nessun dato è stato aggionato", "modifica la casella di testo per aggiornare le note sul paziente");
	    	}
	
		    catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
		else {
			loadInformations();
		}
		event.consume();	
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
        //ripulisce il grafico dai dati
    	bloodSugarGraph.getData().clear();

        if (measurements.isEmpty()) {
            return;
        }

        // 1. Definisci il periodo settimanale
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(30);

        // 2. Filtra le misurazioni per includere solo quelle dell'ultimo mese
        List<Measurement> weeklyMeasurements = measurements.stream()
                .filter(m -> {
                    LocalDate measurementDate = m.getDateTime().toLocalDate();
                    return !measurementDate.isBefore(monthAgo) && !measurementDate.isAfter(today);
                })
                .collect(Collectors.toList());
                
        // Se la lista filtrata è vuota, non fare nulla
        if (weeklyMeasurements.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Andamento Glicemia Mensile");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM", Locale.ITALY);

        // Ordina solo le misurazioni settimanali
        weeklyMeasurements.sort((m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()));

        for (Measurement m : weeklyMeasurements) {
            String dataFormattata = m.getDateTime().format(formatter);
            //data formatta punto x e m.getValue punto y nel piano xy
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dataFormattata, m.getValue());
            series.getData().add(dataPoint);
        }

        bloodSugarGraph.getData().add(series);
    }
    
    private void loadInformations() {
    	String sql = "SELECT informations FROM patients WHERE id = ?";
    	// aggiorno textArea con le note fatte dal dottore
    	try(Connection con = DatabaseUtil.connect();
    			PreparedStatement ps = con.prepareStatement(sql)){
    		ps.setInt(1, patient.getPatientId());
    		
    		ResultSet rs = ps.executeQuery();
    		if(rs.next())
    			infoPatients.setText(rs.getString("informations"));
    		
    	}

	    catch (SQLException e) {
	        e.printStackTrace();
	    }
    }

}
