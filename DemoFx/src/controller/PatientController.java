package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

import facade.ClinicFacade;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.AppUtils;
import model.Doctor;
import model.Intake;
import model.Measurement;
import model.Patient;
import model.Prescription;
import model.ResetTask;
import model.Symptoms;

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
	// fx:include: puoi iniettare il controller del child così:
	// il child è la view therapyTableView, ovvero la view della tabella delle
	// terapie che dovrà essere condivisa
	@FXML
	private TherapyTableController therapyTableAsController;
	@FXML
	private ComboBox<String> dropList;
	@FXML
	private ComboBox<String> drugDropList;
	@FXML
	private TextField amount;
	@FXML
	private ComboBox<String> unitDropList;
	@FXML
	private DatePicker dataPickerPrescription;
	@FXML
	private TableView<Prescription> table;
	//-----------------------------------------------------------------------
	
	private ClinicFacade clinic;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// collega le colonne della tabella misurazioni ai campi della classe
		// Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormatted"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		dropList.getItems().addAll("assuzione insulina", "assunzione faramaci antidiabetici orali");
		// inizializzio la dropList con le unità di misura std
		AppUtils.intializeMeasurementUnit(unitDropList);

		AppUtils.colorMeasurments(valueColumn);
	}

	public void setUser(Patient user) {
		super.setUser(user);
		loadAndShowDoctorInfo();
		loadAndShowMeasurements();
		loadAndShowSymptoms();
		loadAndShowPrescriptions();
	}
	
	public void setClinic(ClinicFacade clinic) {
	    this.clinic = clinic;
	    //ResetTask.checkAndResetIfNeeded(); // prova
	}

	public void enterInTake() {
		if (dataPickerPrescription.getValue() == null || unitDropList.getValue() == null ||
		        drugDropList.getValue() == null || amount.getText() == null || dropList.getValue() == null) {
		        AppUtils.showError("Error","data are missing","Impossible to insert intake");
		        return;
		    }
		    double doses;
		    try { doses = Double.parseDouble(amount.getText()); }
		    catch (NumberFormatException ex) { AppUtils.showError("Error","invalid number","Check amount"); return; }

		    LocalDateTime when = LocalDateTime.of(dataPickerPrescription.getValue(), LocalTime.now());
		    Intake t = new Intake(0,dropList.getValue(),doses,unitDropList.getValue(),when,user.getPatientId(),
		    		user.getMedicoId(),drugDropList.getValue());
		    try {
		        int idIntake = clinic.addIntake(t);
		        t.setId(idIntake);
		        dataPickerPrescription.setValue(null);
		        unitDropList.getSelectionModel().clearSelection();
		        drugDropList.getSelectionModel().clearSelection();
		        dropList.getSelectionModel().clearSelection();
		        amount.setText("");
		        AppUtils.showConfirmation("Perfect!", "right data", "intake successfully recorded!");
		    } catch (SQLException e) {
		        e.printStackTrace();
		        AppUtils.showError("DB Error","Insert failed", e.getMessage());
		    }
	}

	public void logout() {
		super.logout();
	}

	public void inserisciMisurazione(ActionEvent e) {

		 if (myDatePicker.getValue() == null || valueTextField.getText() == null || pasto.getSelectedToggle() == null) {
		        AppUtils.showError("Errore", "dati mancanti", "Impossibile inserire la misurazione");
		        return;
		    }
		    double value;
		    try { value = Double.parseDouble(valueTextField.getText()); }
		    catch (NumberFormatException ex) { AppUtils.showError("Error","wrong number","Insert a valid number"); return; }

		    String moment = primaPastoRb.isSelected() ? "prima pasto" : "dopo pasto";
		    LocalDateTime dateTime = LocalDateTime.of(myDatePicker.getValue(), LocalTime.now());
		    Measurement m = new Measurement(0, user.getPatientId(), moment, dateTime, value);

		    try {
		        int newId = clinic.addMeasurement(m);
		        m.setId(newId);
		        measurementsTableView.getItems().add(m);
		        myDatePicker.setValue(null); valueTextField.setText(""); pasto.selectToggle(null);
		        AppUtils.showConfirmation("Perfetto!", "dati corretti", "registrazione della misurazione eseguita con successo");
		    } catch (SQLException ex) {
		        ex.printStackTrace();
		        AppUtils.showError("DB Errore","Inserimento fallito", ex.getMessage());
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
		 Measurement sel = measurementsTableView.getSelectionModel().getSelectedItem();
		    if (sel == null) {
		        AppUtils.showError("Attenzione", "measurement not selected", "Please, select a measurement to delete");
		        return;
		    }
		    try {
		        int rows = clinic.deleteMeasurement(sel.getId());
		        if (rows > 0) {
		            measurementsTableView.getItems().remove(sel);
		        } else {
		            AppUtils.showError("Error","impossible to remove","Please select another measurement");
		        }
		    } catch (SQLException ex) {
		        ex.printStackTrace();
		        AppUtils.showError("DB Error","Delete failed", ex.getMessage());
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
		
		String joined = String.join(",", symptomsListView.getItems());
		Symptoms s = new Symptoms(
		        0,  						// id provvisorio
		        user.getPatientId(),
		        user.getMedicoId(),
		        joined,
		        when,
		        symptomsNotes == null ? "" : symptomsNotes.getText().trim()
		    );
		
		try {
			
			int id = clinic.addSymptoms(s); // il DAO ritorna l'id generato
			s.setId(id);					// setto l'id generato dall'aggiunta del sintomo
			symptomsVisualization.getItems().add(s);
			
			//pulizia UI
			symptomsListView.getItems().clear();
			symptomDatePicker.setValue(null);
		    if (symptomsNotes != null) symptomsNotes.clear();
			
		    AppUtils.showConfirmation("Perfect!", "right data", "symptoms successfully recorded!");
		}catch (SQLException e) {
			e.printStackTrace();
			AppUtils.showError("DB Error","Insert failed", e.getMessage());
		}
		
	}

	public void resolveSymptoms() {

		 if (symptomsVisualization.getItems().isEmpty()) {
		        AppUtils.showError("No Symptoms to resolve","Unable to resolve","Add at least one symptom");
		        return;
		    }
		    Symptoms sel = symptomsVisualization.getSelectionModel().getSelectedItem();
		    if (sel == null) {
		        AppUtils.showError("No Symptom Selected","Unable to resolve","Select a symptom first");
		        return;
		    }
		    try {
		        int rows = clinic.resolveSymptoms(sel.getSymptomId(), LocalDateTime.now());
		        if (rows > 0) {
		            symptomsVisualization.getItems().remove(sel);
		            AppUtils.showConfirmation("Perfect!", "right resolution", "symptom successfully resolved!");
		        } else {
		            AppUtils.showError("Not Found","Symptom not updated","Could not find the selected symptom");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        AppUtils.showError("DB Error","Update failed", e.getMessage());
		    }

	}
	// modica la colonna che dice se ha preso o no il medicinale
	public void preso(ActionEvent event) throws SQLException {
		Prescription pSelected = therapyTableAsController.getSelectedItem();
		String newPreso = "Yes";
		int rows;
		if (pSelected != null) {
			if(pSelected.getTaken().equals(newPreso))
				newPreso = "No";
			rows = clinic.updatePrescriptionPreso(newPreso, pSelected.getIdPrescription());
			if(rows > 0) {
				//aggiorno l'oggetto in memoria attraverso i metodi set
	            pSelected.setTaken(newPreso);
			}
		} else {
			AppUtils.showError("Error", "you must select an Item",
					"Please, select an item if you would like to modify it");
			return;
		}
		therapyTableAsController.refresh();
	}

	private void loadAndShowDoctorInfo() {
		// 1) prendo l'id medico dall'oggetto Patient
		final int medicoId = user.getMedicoId();

		if (medicoId <= 0) {
			doctorLabel.setText("n/d");
			return;
		}
		
		try {
			Doctor d = clinic.loadDoctorInfo(medicoId);
			
			if(d != null) {
				 Platform.runLater(() ->
	                doctorLabel.setText(d.getUsername() + "  -  email: " + d.getEmail())
	            );
			}else {
				 Platform.runLater(() -> doctorLabel.setText("n/d"));
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void loadAndShowMeasurements() {
		
		try {
			ObservableList<Measurement> list = FXCollections.observableArrayList(clinic.loadMeasurements(user.getPatientId()));
			measurementsTableView.setItems(list);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadAndShowSymptoms() {
		try {
	        ObservableList<Symptoms> list = FXCollections.observableArrayList(clinic.loadOpenSymptoms(user.getPatientId()));
	        symptomsVisualization.setItems(list);
	    } catch (SQLException e) { e.printStackTrace(); }
	}

	private ObservableList<Prescription> loadAndShowPrescriptions() {
		 try {
			 	ObservableList<Prescription> list = FXCollections.observableArrayList(clinic.loadPrescriptions(user.getPatientId()));
		        therapyTableAsController.setItems(list);

		        // aggiorna le combo
		        drugDropList.getItems().clear();
		        for (Prescription p : list) {
		            if (!drugDropList.getItems().contains(p.getDrug())) drugDropList.getItems().add(p.getDrug());
		            if (!unitDropList.getItems().contains(p.getMeasurementUnit())) unitDropList.getItems().add(p.getMeasurementUnit());
		        }
		        return list;
		    } catch (SQLException e) { e.printStackTrace(); return FXCollections.observableArrayList(); }
	}
}