package controller;

import java.io.IOException;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import model.AppUtils;
import model.DatabaseUtil;
import model.Doctor;
import model.Measurement;
import model.Patient;
import model.Prescription;
import model.SharedDataModelDoc;
import model.Symptoms;

public class DoctorDashboardController extends DoctorController implements Initializable {


	@FXML
	private TextField amount;

	@FXML
	private LineChart<String, Number> bloodSugarGraph;

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
	private Spinner<Integer> numberOfIntakes;

	@FXML
	private TextArea otherIndication;

	@FXML
	private TextField pathologiesField;

	@FXML
	private ListView<Patient> patientsListView;

	@FXML
	private Button saveButton;

	@FXML
	private ListView<Symptoms> symptomsMedicinesView;
	@FXML
	private TherapyTableController therapyTableAsController; 

	private ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();

	@FXML
	private Button visualizeButton;
	@FXML
	private Label namePatientLabel;
	@FXML
	private TableView<Measurement> measurementsTableView;
	@FXML
	private TableColumn<Measurement, String> dateColumn;
	@FXML
	private TableColumn<Measurement, String> momentColumn;
	@FXML
	private TableColumn<Measurement, Double> valueColumn;
	@FXML
	private Label doctorNameLabel;
	@FXML
	private TabPane tabPane1;

	private Doctor doctor;
	private Patient patient;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// collega le colonne della tabella misurazioni ai campi della classe
		// Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormatted"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

		// faccio il setup dello spinner con un range(min,max,init value)
		numberOfIntakes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

		AppUtils.colorMeasurments(valueColumn);

	}

	@FXML
	void logout(ActionEvent event) {
		super.logout();
	}

	public void setEnviroment(SharedDataModelDoc instance, Patient selectedPatient, Doctor doctor) throws SQLException {
		if(doctor == null) // fixato bug che a volte ha doctror == null
			doctor = instance.getDoctor();
		this.instance = instance;
		this.doctor = doctor;
		this.patient = selectedPatient;
		// aggiorno label di benvenuto
		doctorNameLabel.setText("Benvenuto " + this.doctor.toString());
		// aggiono label col nome del paziente che sto visualizzando
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
		// popola la listView delle segnalazioni dei sintomi e delle cure concomitanti
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

		symptomsMedicinesView.setItems(symptoms);

		// visualizzo la tableView
		String sqlMeasurments = "SELECT id,dateTime, moment, value FROM measurements WHERE patientId = ?";
		ObservableList<Measurement> measurments = FXCollections.observableArrayList();

		try {
			measurments = DatabaseUtil.queryList(sqlMeasurments, ps -> {
				try {
					ps.setInt(1, patient.getPatientId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}, rs -> {
				int id = rs.getInt("id");
				String raw = rs.getString("dateTime");
				LocalDateTime date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				String moment = rs.getString("moment");
				double value = rs.getDouble("value");
				return new Measurement(id, patient.getPatientId(), moment, date, value);
			});

		} catch (SQLException e) {
			e.printStackTrace();
		}
		measurementsTableView.setItems(measurments);

		// visualizza le presciptions
		String sqlPrescriptions = "SELECT id, doses, quantity, indications, drug FROM prescriptions WHERE patientId = ?";

		try {
			prescriptions = DatabaseUtil.queryList(sqlPrescriptions, ps -> {
				try {
					ps.setInt(1, patient.getPatientId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}, rs -> {
				int id = rs.getInt("id");
				String doses = rs.getString("doses");
				int quantity = rs.getInt("quantity");
				String indications = rs.getString("indications");
				int patientId = patient.getPatientId();
				int doctorId = this.doctor.getMedicoId();
				String drug = rs.getString("drug");
				return new Prescription(id, doses, quantity, indications, patientId, doctorId, drug);
			});

		} catch (SQLException e) {
			e.printStackTrace();
		}
		therapyTableAsController.setItems(prescriptions);
	}

	public void updateNotes(ActionEvent event) {
		boolean confermation = AppUtils.showConfirmationWithBoolean("data update ", "data updated",
				"sure to update data?");
		String newText = infoPatients.getText();
		String sql = "UPDATE patients SET informations = ? WHERE id = ?";
		if (confermation) {
			int rows = DatabaseUtil.executeUpdate(sql, ps -> {
				ps.setString(1, newText);
				ps.setInt(2, patient.getPatientId());
			});
			if (rows > 0)
				AppUtils.showInfo("data updated! ", "data updated", "new data has been saved");
			else
				AppUtils.showError("Errore caricamento dati", "nessun dato è stato aggionato",
						"modifica la casella di testo per aggiornare le note sul paziente");
		} else {
			loadInformations();
		}
		event.consume();
	}

	public void updateGraphBloodSugar(List<Measurement> measurements) {
		// ripulisce il grafico dai dati
		bloodSugarGraph.getData().clear();

		if (measurements.isEmpty()) {
			return;
		}

		// 1. Definisci il periodo mensile
		LocalDate today = LocalDate.now();
		LocalDate monthAgo = today.minusDays(30);

		// 2. Filtra le misurazioni per includere solo quelle dell'ultimo mese
		List<Measurement> monthlyMeasurements = measurements.stream().filter(m -> {
			LocalDate measurementDate = m.getDateTime().toLocalDate();
			return !measurementDate.isBefore(monthAgo) && !measurementDate.isAfter(today);
		}).collect(Collectors.toList());

		// Se la lista filtrata è vuota, non fare nulla
		if (monthlyMeasurements.isEmpty()) {
			return;
		}

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Andamento Glicemia Mensile");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM", Locale.ITALY);

		// Ordina solo le misurazioni settimanali
		monthlyMeasurements.sort((m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()));

		for (Measurement m : monthlyMeasurements) {
			String dataFormattata = m.getDateTime().format(formatter);
			// data formatta punto x e m.getValue punto y nel piano xy
			XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dataFormattata, m.getValue());
			series.getData().add(dataPoint);
		}

		bloodSugarGraph.getData().add(series);
	}

	public void insertTherapy(ActionEvent event) {
		// controllo se non ci sono errori di input
		if (medicineField.getText() == null || numberOfIntakes.getValue() == null || amount.getText() == null
				|| otherIndication.getText() == null) {
			AppUtils.showError("Error", "data are missing", "Impossible to insert prescription");
			return;
		}

		// variabili che mi servono per inserire la terapia/prescrizione
		String sql = "INSERT INTO prescriptions (doses, quantity, indications, patientId,doctorId,drug) VALUES (?,?,?,?,?,?)";

		int patientId = patient.getPatientId();
		int doctorId = doctor.getMedicoId();
		String doses = amount.getText();
		int quantity = numberOfIntakes.getValue();
		String indications = otherIndication.getText();
		String drug = medicineField.getText();
		int idPrescription = -1;

		// inserisco nel database la nuova misurazione
		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql,java.sql.Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, doses);
			ps.setInt(2, quantity);
			ps.setString(3, indications);
			ps.setInt(4, patientId);
			ps.setInt(5, doctorId);
			ps.setString(6, drug);

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) { // ottengo la primaryKey id della nuova prescrizione
				if (rs.next())
					idPrescription = rs.getInt(1);
			}

			Prescription p = new Prescription(idPrescription, doses, quantity, indications, patientId, doctorId, drug);
			prescriptions.add(p);

			// pulisco tutti i campi dell'inserimento
			medicineField.setText("");
			numberOfIntakes.getValueFactory().setValue(1);
			amount.setText("");
			otherIndication.setText("");
			AppUtils.showConfirmation("Perfect!", "right data", "prescription successfully performed!");

		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public void modifyTherapy(ActionEvent e) throws IOException {
		Prescription pSelected = therapyTableAsController.getSelectedItem();
		UpdatePrescriptionController controller;
		
		if(pSelected != null) {
			controller = ViewNavigator.loadViewOver("updatePrescriptionView.fxml","Update");
			controller.setPrescription(pSelected);
			//passo una task -> oggetto runnable, per aggiornare la tabella nella classe UpdateMeasurementController
			controller.setOnUpdate(() -> {therapyTableAsController.refresh();});
		}
		else {
			AppUtils.showError("Error", "you must select an Item", "Please, select an item if you would like to modify it");
			return;
		}
		
	}

	public void deleteTherapy(ActionEvent event) {
		Prescription pSelected = therapyTableAsController.getSelectedItem();
		if (pSelected == null) {
			AppUtils.showError("Attenzione", "prescription not selected", "Please, select a prescription to delete");
			event.consume();
			return;
		}

		String sql = "DELETE FROM prescriptions WHERE id = ?";
		int rows = DatabaseUtil.executeUpdate(sql, ps -> ps.setInt(1, pSelected.getIdPrescription()));

		if (rows > 0) {
			prescriptions.remove(pSelected); // lista condivisa -> la tabella si aggiorna
		} else {
			AppUtils.showError("Error", "impossible to remove this prescription", "Please select another prescription");
		}

		//event.consume();
	}

	private void loadInformations() {
		String sql = "SELECT informations FROM patients WHERE id = ?";
		// aggiorno textArea con le note fatte dal dottore
		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, patient.getPatientId());

			ResultSet rs = ps.executeQuery();
			if (rs.next())
				infoPatients.setText(rs.getString("informations"));

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void visualize(ActionEvent event) throws SQLException {
		super.visualize(event);
	}

}
