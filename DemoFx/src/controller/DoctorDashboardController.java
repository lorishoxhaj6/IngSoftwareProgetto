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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
	private Spinner<Integer> numberOfIntakes;

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
	private ListView<Symptoms> symptomsMedicinesView;

	@FXML
	private TableView<Prescription> tableTherapyView;

	@FXML
	private TableColumn<Prescription, String> drugColumn;
	@FXML
	private TableColumn<Prescription, Integer> quantityColumn;
	@FXML
	private TableColumn<Prescription, String> dosesColumn;
	@FXML
	private TableColumn<Prescription, String> indicationColumn;
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
	
	private Doctor doctor;
	private Patient patient;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// collega le colonne della tabella misurazioni ai campi della classe
		// Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormatted"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		
		// collega le colonne della tabella tableTherapyView ai campi della classe Prescription
		drugColumn.setCellValueFactory(new PropertyValueFactory<>("drug"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		dosesColumn.setCellValueFactory(new PropertyValueFactory<>("doses"));
		indicationColumn.setCellValueFactory(new PropertyValueFactory<>("indications"));
		
		
		// faccio il setup dello spinner con un range(min,max,init value)
		numberOfIntakes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
		
		
		// serve per colorare i risultati della colonna value
		valueColumn.setCellFactory(col -> new TableCell<Measurement, Double>() {
			// setCell serve a personalizzare come sono disegnate le celle
			// TableCell è una cella della tabella
			protected void updateItem(Double n, boolean empty) {
				super.updateItem(n, empty);
				// gestione caso cella vuota
				if (empty || n == null) {
					setText(null);
					setTextFill(Color.BLACK);
					return;
				}

				double value = n.doubleValue();
				setText(String.valueOf(value));

				Measurement m = getTableView().getItems().get(getIndex());
				String moment = m.getMoment();

				if (moment.equals("prima pasto")) {
					if (value >= 80 && value <= 130) {
						// codice verde
						setTextFill(Color.GREEN);
					} else {
						if (value >= 50 && value < 80 || value > 130 && value <= 160) {
							// codice arancione
							setTextFill(Color.ORANGE);
						} else {
							if (value < 50 || value > 160)
								// codice rosso
								setTextFill(Color.RED);
						}
					}

				} else {
					if (value < 180) {
						// codice verde
						setTextFill(Color.GREEN);
					} else {
						if (value > 190 && value <= 210) {
							// codice arancio
							setTextFill(Color.ORANGE);
						}
						// codice rosso
						setTextFill(Color.RED);
					}
				}

			}
		});

	}

	@FXML
	void logout(ActionEvent event) {
		super.logout();
	}

	public void setEnviroment(SharedDataModelDoc instance, Patient selectedPatient, Doctor doctor) throws SQLException {
		this.instance = instance;
		this.doctor = doctor;
		this.patient = selectedPatient;
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
	}

	

	public void updateNotes(ActionEvent event) {
		boolean confermation = AppUtils.showConfirmationWithBoolean("data update ", "data updated",
				"sure to update data?");
		System.out.println("prova");
		String newText = infoPatients.getText();
		String sql = "UPDATE patients SET informations = ? WHERE id = ?";
		if (confermation) {
			try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, newText);
				ps.setInt(2, patient.getPatientId());

				int rowAffected = ps.executeUpdate();
				if (rowAffected > 0)
					AppUtils.showInfo("data updated! ", "data updated", "new data has been saved");
				else
					AppUtils.showError("Errore caricamento dati", "nessun dato è stato aggionato",
							"modifica la casella di testo per aggiornare le note sul paziente");
			}

			catch (SQLException e) {
				e.printStackTrace();
			}
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

		// 1. Definisci il periodo settimanale
		LocalDate today = LocalDate.now();
		LocalDate monthAgo = today.minusDays(30);

		// 2. Filtra le misurazioni per includere solo quelle dell'ultimo mese
		List<Measurement> weeklyMeasurements = measurements.stream().filter(m -> {
			LocalDate measurementDate = m.getDateTime().toLocalDate();
			return !measurementDate.isBefore(monthAgo) && !measurementDate.isAfter(today);
		}).collect(Collectors.toList());

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
			// data formatta punto x e m.getValue punto y nel piano xy
			XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dataFormattata, m.getValue());
			series.getData().add(dataPoint);
		}

		bloodSugarGraph.getData().add(series);
	}

	@FXML
	void insertTherapy(ActionEvent event) {
		// controllo se non ci sono errori di input
		if (medicineField.getText() == null || numberOfIntakes.getValue() == null || amount.getText() == null
				|| otherIndication.getText() == null) {
			AppUtils.showError("Error", "data are missing", "Impossible to insert measurement");
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
		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

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
			tableTherapyView.getItems().add(p); // aggiungo la nuova Prescrizione alla tabella

			// pulisco tutti i campi dell'inserimento
			
			medicineField.setText("");
			numberOfIntakes.getValueFactory().setValue(1);
			amount.setText("");
			otherIndication.setText("");
			AppUtils.showConfirmation("Perfect!", "right data", "measurement successfully performed!");

		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	@FXML
	void modifyTherapy(ActionEvent event) {

	}

	@FXML
	void deleteTherapy(ActionEvent event) {
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

}
