package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.AppUtils;
import model.DatabaseUtil;
import model.Measurement;
import model.Patient;
import model.Prescription;
import model.Symptoms;
import model.Unit;

public class PatientController extends UserController<Patient> implements Initializable {
	// usa superclasse ma con Patient e non con un tipo generico
	// ------------ FXML ------------
	@FXML
	private Label doctorLabel;
	@FXML
	private TextField valueTextField;
	@FXML
	private RadioButton primaPastoRb, dopoPastoRb;
	@FXML
	private DatePicker myDatePicker, symptomDatePicker;
	@FXML
	private ToggleGroup pasto;
	@FXML
	private TableView<Measurement> measurementsTableView;
	@FXML
	private TableColumn<Measurement, String> dateColumn;
	@FXML
	private TableColumn<Measurement, String> momentColumn;
	@FXML
	private TableColumn<Measurement, Double> valueColumn;
	@FXML
	private ListView<String> symptomsListView;
	@FXML
	private ListView<Symptoms> symptomsVisualization;
	@FXML
	private TextField symptomsTextField;
	@FXML
	private Button symptompsAddButton, symptompsEnter;
	@FXML
	private TextArea symptomsNotes;
	@FXML
	private ToggleButton symptomsTb1, symptomsTb2, symptomsTb3, symptomsTb4, symptomsTb5;
	@FXML
	private TabPane tabPane1;
	@FXML
	private Button saveButton;
	@FXML
	private AnchorPane patientPane;
	// Magia di fx:include: puoi iniettare il controller del child così:
	// il child è la view therapyTableView, ovvero la view della tabella delle
	// terapie che dovrà essere condivisa
	@FXML
	private TherapyTableController therapyTableAsController;
	@FXML 
	private ComboBox<String> dropList;
	@FXML
	private ComboBox<Prescription> drugDropList;
	@FXML
	private TextField amount;
	@FXML
	private ComboBox<Unit> unitDropList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// collega le colonne della tabella misurazioni ai campi della classe
		// Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormatted"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		dropList.getItems().addAll("assuzione insulina","assunzione faramaci antidiabetici orali");
		
		
		AppUtils.colorMeasurments(valueColumn);
	}

	public void setUser(Patient user) {
		super.setUser(user);
		ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
		
		loadAndShowDoctorInfo(user);
		loadAndShowMeasurements();
		loadAndShowSymptoms();
		prescriptions = loadAndShowPrescriptions(prescriptions);
		drugDropList.getItems().addAll(prescriptions);
	}

	public void logout() {
		super.logout();
	}

	public void inserisciMisurazione(ActionEvent e) {

		// controllo se non ci sono errori di input
		if (myDatePicker.getValue() == null || valueTextField.getText() == null || pasto.getSelectedToggle() == null) {
			AppUtils.showError("Error", "data are missing", "Impossible to insert measurement");
			return;
		}

		double value;
		try {
			value = Double.parseDouble(valueTextField.getText());
		} catch (NumberFormatException e1) {
			AppUtils.showError("Error", "data are missing", "Impossible to insert measurement");
			return;
		}

		// variabili che mi servono per inserire la misurazione
		String sql = "INSERT INTO measurements (patientId, moment, dateTime, value) VALUES (?,?,?,?)";
		int idMeasurement = -1;
		int userId = user.getPatientId();
		String moment = "";
		LocalDate date = myDatePicker.getValue();
		LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.now());
		// inserisco nel database la nuova misurazione
		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userId);
			if (primaPastoRb.isSelected()) {
				ps.setString(2, "prima pasto");
				moment = "prima pasto";
			} else {
				ps.setString(2, "dopo pasto");
				moment = "dopo pasto";
			}
			ps.setString(3, dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setDouble(4, value);

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) { // ottengo la primaryKey id della nuova misurazione
				if (rs.next())
					idMeasurement = rs.getInt(1);
			}

			Measurement m = new Measurement(idMeasurement, userId, moment, dateTime, value);
			measurementsTableView.getItems().add(m); // aggiungo la nuova misurazione alla tabella

			// Sistema di segnalazione per registrazioni oltre le soglie a seconda della
			// gravità
			/*
			 * if(moment.equals("prima pasto")) { if( value >= 60 && value <= 70 || value >=
			 * 140 && value <= 150) { // Invia alert dottore con codice verde
			 * 
			 * }else { if(value >= 50 && value < 60 || value > 150 && value <= 160) {
			 * //Invia alert dottore con codice giallo }else { if(value < 50 || value > 160)
			 * //Invia alert dottore codice rosso } }
			 * 
			 * } else { if(value > 180 && value <= 190) { //codice bianco }else { if(value
			 * >190 && value <= 200) { //codice arancio } //codice rosso } }
			 */

			// pulisco tutti i campi dell'inserimento
			myDatePicker.setValue(null);
			valueTextField.setText("");
			pasto.selectToggle(null);
			AppUtils.showConfirmation("Perfect!", "right data", "measurement successfully performed!");

		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public void modifyElement(ActionEvent e) throws IOException {
		Measurement mSelected = measurementsTableView.getSelectionModel().getSelectedItem();
		UpdateMeasurementController controller;

		if (mSelected != null) {
			controller = ViewNavigator.loadViewOver("updateMeasurementView.fxml", "Update");
			controller.setMeasurement(mSelected);
			// passo una task -> oggetto runnable, per aggiornare la tabella nella classe
			// UpdateMeasurementController
			controller.setOnUpdate(() -> {
				measurementsTableView.refresh();
			});
		} else {
			AppUtils.showError("Error", "you must select an Item",
					"Please, select an item if you would like to modify it");
			return;
		}

	}

	public void deleteMeasurement(ActionEvent e) {
		Measurement mSelected = measurementsTableView.getSelectionModel().getSelectedItem();
		if (mSelected != null) {
			String sql = "DELETE FROM measurements WHERE id = ?";

			int rows = DatabaseUtil.executeUpdate(sql, ps -> {
				ps.setInt(1, mSelected.getId());
			});
			if (rows > 0) {
				measurementsTableView.getItems().remove(mSelected);
			} else
				AppUtils.showError("Error", "impossible to remove this measurement",
						"Please select another measurement");
			measurementsTableView.getItems().remove(mSelected);
		} else {
			AppUtils.showError("Attenzione", "measurement not selected", "Please, select a measurement to delete");
		}
	}

	public void insertToggleSymptoms() {

		if (symptomsTb1.isSelected()) {
			symptomsListView.getItems().add(symptomsTb1.getText());
			symptomsTb1.setSelected(false);
		}
		if (symptomsTb2.isSelected()) {
			symptomsListView.getItems().add(symptomsTb2.getText());
			symptomsTb2.setSelected(false);
		}
		if (symptomsTb3.isSelected()) {
			symptomsListView.getItems().add(symptomsTb3.getText());
			symptomsTb3.setSelected(false);
		}
		if (symptomsTb4.isSelected()) {
			symptomsListView.getItems().add(symptomsTb4.getText());
			symptomsTb4.setSelected(false);
		}
		if (symptomsTb5.isSelected()) {
			symptomsListView.getItems().add(symptomsTb5.getText());
			symptomsTb5.setSelected(false);
		}

	}

	public void insertSymptomsOnButtonClick() {

		if (symptomsTextField.getText().isEmpty()) {
			AppUtils.showError("None symptom selected", "Symptom", "Please, write a symptom and then click +");
		}

		symptomsListView.getItems().add(symptomsTextField.getText());

		symptomsTextField.clear();

	}

	public void deleteSymptomSelected() {

		if (symptomsListView.getItems().isEmpty()) {
			AppUtils.showError("No Symptoms Available", "Unable to Delete",
					"Please add at least one symptom before attempting to delete.");
			return;
		}

		String sel = symptomsListView.getSelectionModel().getSelectedItem();
		if (sel == null || sel.isBlank()) {
			AppUtils.showError("No Symptom Selected", "Unable to Delete Symptom",
					"Please select a symptom from the list before attempting to delete it.");
			return;
		} else {
			symptomsListView.getItems().remove(sel);
		}

	}

	public void enterSymptoms() {

		// 1) Validazioni preliminari
		if (symptomsListView.getItems().isEmpty()) {
			AppUtils.showError("No Symptoms Available", "Unable to enter the symptoms",
					"Please add at least one symptom before attempting to enter.");
			return;
		}

		// Verifica che la data sia selezionata PRIMA di creare LocalDateTime
		LocalDate selectedDate = symptomDatePicker.getValue();
		if (selectedDate == null) {
			AppUtils.showError("No date selected", "Unable to enter the symptoms",
					"Please select a Date before attempting to enter");
			return;
		}

		// 2) Costruisci il timestamp (ora attuale nel giorno scelto)
		LocalDateTime when = LocalDateTime.of(selectedDate, LocalTime.now());

		// 3) Prepara SQL
		String sql = "INSERT INTO symptoms (patient_id, doctor_id, symptoms, startDateTime, notes) VALUES (?,?,?,?,?)";

		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

			// Unisci i sintomi in una stringa
			ObservableList<String> listSymptoms = symptomsListView.getItems();
			String symptomsText = String.join(",", listSymptoms);

			// 4) Parametri
			ps.setInt(1, user.getPatientId()); // patient_id
			ps.setInt(2, user.getMedicoId()); // doctor_id
			ps.setString(3, symptomsText); // symptoms

			ps.setString(4, when.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

			String notes = (symptomsNotes != null) ? symptomsNotes.getText() : "";
			ps.setString(5, notes);

			ps.executeUpdate();

			AppUtils.showConfirmation("Perfect!", "right data", "symptoms successfully recorded!");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					int generatedId = rs.getInt(1);
					Symptoms s = new Symptoms(generatedId, user.getPatientId(), user.getMedicoId(), symptomsText, when,
							notes);
					symptomsVisualization.getItems().add(s);
				}
			}
			// 5) Pulizia UI
			symptomsListView.getItems().clear();
			symptomDatePicker.setValue(null);
			if (symptomsNotes != null)
				symptomsNotes.clear();

		} catch (SQLException e) {
			System.out.println("Errore inserimento sintomi in db");
			e.printStackTrace();
		}
	}

	public void resolveSymptoms() {

		if (symptomsVisualization.getItems().isEmpty()) {
			AppUtils.showError("No Symptoms to resolve", "Unable to resolve the symptoms",
					"Please add at least one symptom before attempting to resolve one.");
			return;
		}

		Symptoms selectedSymptom = symptomsVisualization.getSelectionModel().getSelectedItem();
		LocalDateTime when = LocalDateTime.now();

		String sql = "UPDATE symptoms SET endDateTime = ? WHERE id = ?";
		int rows = DatabaseUtil.executeUpdate(sql, ps -> {
			ps.setString(1, when.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setInt(2, selectedSymptom.getSymptomId());
		});

		if (rows > 0) {
			AppUtils.showConfirmation("Perfect!", "right resolution", "symptom successfully resolved!");
			symptomsVisualization.getItems().remove(selectedSymptom);
		} else {
			AppUtils.showError("Not Found", "Symptom not updated",
					"Could not find the selected symptom in the database.");
		}

	}

	private void loadAndShowDoctorInfo(Patient p) {
		// 1) prendo l'id medico dall'oggetto Patient
		final int medicoId = p.getMedicoId();

		if (medicoId <= 0) {
			doctorLabel.setText("n/d");
			return;
		}

		final String sql = "SELECT username, email FROM doctors WHERE id = ?";

		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, medicoId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String doctorUser = rs.getString("username");
					String doctorEmail = rs.getString("email");

					// Aggiorno la UI sul thread JavaFX
					Platform.runLater(() -> doctorLabel.setText(doctorUser + "  -  email: " + doctorEmail));
				} else {
					Platform.runLater(() -> doctorLabel.setText("n/d"));
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			Platform.runLater(() -> doctorLabel.setText("Dottore: errore nel caricamento"));
		}
	}

	private void loadAndShowMeasurements() {
		String sqlMeasurments = "SELECT id,dateTime, moment, value FROM measurements WHERE patientId = ?";
		ObservableList<Measurement> measurments = FXCollections.observableArrayList();
		try {
			measurments = DatabaseUtil.queryList(sqlMeasurments, ps -> {
				try {
					ps.setInt(1, user.getPatientId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}, rs -> {
				int id = rs.getInt("id");
				String raw = rs.getString("dateTime");
				LocalDateTime date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				String moment = rs.getString("moment");
				double value = rs.getDouble("value");
				return new Measurement(id, user.getPatientId(), moment, date, value);
			});

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// provato a riaggiornale la tabella ma non funziona
		measurementsTableView.setItems(measurments);

	}

	private void loadAndShowSymptoms() {
		String sqlSymptoms = "SELECT id,symptoms, startDateTime, notes FROM symptoms WHERE patient_id = ? AND endDateTime IS NULL";
		ObservableList<Symptoms> symptoms = FXCollections.observableArrayList();
		try {
			symptoms = DatabaseUtil.queryList(sqlSymptoms, ps -> {
				try {
					ps.setInt(1, user.getPatientId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}, rs -> {
				int symptomId = rs.getInt("id");
				String raw = rs.getString("startDateTime");
				LocalDateTime date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				String sympt = rs.getString("symptoms");
				String notes = rs.getString("notes");

				return new Symptoms(symptomId, user.getMedicoId(), user.getPatientId(), sympt, date, notes);
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
		symptomsVisualization.setItems(symptoms);
	}

	private ObservableList<Prescription> loadAndShowPrescriptions(ObservableList<Prescription> prescriptions) {
		// PRESCRIPTIONS
		String sqlPrescriptions = "SELECT id, doses, measurementUnit, quantity, indications, drug, doctorId "
				+ "FROM prescriptions WHERE patientId = ?";
		

		try {
			prescriptions = DatabaseUtil.queryList(sqlPrescriptions, ps -> {
				try {
					ps.setInt(1, user.getPatientId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}, rs -> {
				int id = rs.getInt("id");
				Double doses = rs.getDouble("doses");
				String mU = rs.getString("measurementUnit");
				int quantity = rs.getInt("quantity");
				String indications = rs.getString("indications");
				int patientId = user.getPatientId();
				int doctorId = rs.getInt("doctorId"); // meglio dal DB
				String drug = rs.getString("drug");
				return new Prescription(id, doses,mU, quantity, indications, patientId, doctorId, drug);
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}

		therapyTableAsController.setItems(prescriptions);
		return prescriptions;
	}
}