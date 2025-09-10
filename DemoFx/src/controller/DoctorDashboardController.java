package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AppUtils;
import model.Doctor;
import model.Measurement;
import model.Patient;
import model.Prescription;
import model.Symptoms;

public class DoctorDashboardController extends DoctorController implements Initializable {

    @FXML private LineChart<String, Number> bloodSugarGraph;
    @FXML private ListView<Symptoms> historyView;
    @FXML private TextArea infoPatients;
    @FXML private ListView<?> mailBox;
    @FXML private TextField medicineField;
    @FXML private Button modifyTherapyBut;
    @FXML private Spinner<Integer> numberOfIntakes;
    @FXML private TextArea otherIndication;
    @FXML private TextField pathologiesField;
    @FXML private ListView<Patient> patientsListView;
    @FXML private Button saveButton;
    @FXML private ListView<Symptoms> symptomsMedicinesView;
    @FXML private TherapyTableController therapyTableAsController;
    @FXML private Button visualizeButton;
    @FXML private Label namePatientLabel;
    @FXML private TableView<Measurement> measurementsTableView;
    @FXML private TableColumn<Measurement, String> dateColumn;
    @FXML private TableColumn<Measurement, String> momentColumn;
    @FXML private TableColumn<Measurement, Double> valueColumn;
    @FXML private Label doctorNameLabel;
    @FXML private TabPane tabPane1;
    @FXML private ComboBox<String> measurementUnitDropList;
    @FXML private TextField amount;
    @FXML private PatientTabViewController patientTabViewController;

    private ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
    private Doctor doctor;
    private Patient patient;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // setup colonne tabella misurazioni
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormatted"));
        momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // setup spinner
        numberOfIntakes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        AppUtils.intializeMeasurementUnit(measurementUnitDropList);

        // colorazione condizionale
        AppUtils.colorMeasurments(valueColumn);
    }

    @FXML
    void logout(ActionEvent event) {
        super.logout();
    }

    /**
     * Configura la dashboard con paziente e dottore.
     */
    public void setEnviroment(Patient selectedPatient, Doctor currentDoc) throws SQLException {
        this.doctor = currentDoc;
        this.patient = selectedPatient;

        // header
        doctorNameLabel.setText("Benvenuto " + (doctor != null ? doctor.toString() : "—"));
        namePatientLabel.setText("nome Paziente: " + (patient != null ? patient.toString() : "—"));

        // setup del tab pazienti incluso
        if (patientTabViewController != null && doctor != null) {
            patientTabViewController.setDoctor(doctor);
            //POPOLA la lista "Tutti i pazienti"
            patientTabViewController.setAllPatients(FXCollections.observableArrayList(clinic.loadAllPatients()));
            //POPOLA la lista paziente filtrati per medicoCorrente
            patientTabViewController.setFilteredPatient(
                FXCollections.observableArrayList(doctor.getPatients())
            );
            // callback per aggiornare la dashboard in-place
            patientTabViewController.setOnVisualize(p -> {
                try {
                    setEnviroment(p, doctor);
                } catch (SQLException e) {
                    AppUtils.showError("DB error", "Aggiornamento dashboard fallito", e.getMessage());
                }
            });
        }

        if (patient != null) {
            // aggiorna grafico
            List<Measurement> misurazioni = patient.getMeasurementBloodSugar(patient);
            updateGraphBloodSugar(misurazioni);
        }

        // note, sintomi, misurazioni, prescrizioni
        loadInformations();
        loadSymptoms();
        loadMeasurements();
        loadPrescriptions();
    }

    private void loadSymptoms() {
         try {
            ObservableList<Symptoms> symptoms = FXCollections.observableArrayList(clinic.loadOpenSymptoms(patient.getPatientId()));
            symptomsMedicinesView.setItems(symptoms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMeasurements() {
        try {
            ObservableList<Measurement> measurments = FXCollections.observableArrayList(clinic.loadMeasurements(patient.getPatientId()));
            measurementsTableView.setItems(measurments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPrescriptions() {
       try {
            prescriptions = FXCollections.observableArrayList(clinic.loadPrescriptions(patient.getPatientId()));
            therapyTableAsController.setItems(prescriptions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInformations() {
        try {
        	String info = clinic.getInformation(patient.getPatientId());
            infoPatients.setText(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNotes(ActionEvent event) {
        boolean confirmation = AppUtils.showConfirmationWithBoolean("data update ", "data updated",
                "sure to update data?");
        String newText = infoPatients.getText();
         if (confirmation) {
            int rows = 0;
			try {
				rows = clinic.updateInformation(patient.getPatientId(), newText);
			} catch (SQLException e) {
				e.printStackTrace();
			}
            if (rows > 0)
                AppUtils.showInfo("data updated! ", "data updated", "new data has been saved");
            else
                AppUtils.showError("Errore caricamento dati", "nessun dato è stato aggiornato",
                        "modifica la casella di testo per aggiornare le note sul paziente");
        } else {
            loadInformations();
        }
        event.consume();
    }

    public void updateGraphBloodSugar(List<Measurement> measurements) {
        bloodSugarGraph.getData().clear();
        if (measurements.isEmpty()) return;

        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(30);

        List<Measurement> monthlyMeasurements = measurements.stream()
                .filter(m -> {
                    LocalDate measurementDate = m.getDateTime().toLocalDate();
                    return !measurementDate.isBefore(monthAgo) && !measurementDate.isAfter(today);
                })
                .sorted((m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()))
                .collect(Collectors.toList());

        if (monthlyMeasurements.isEmpty()) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Andamento Glicemia Mensile");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM", Locale.ITALY);
        for (Measurement m : monthlyMeasurements) {
            series.getData().add(new XYChart.Data<>(m.getDateTime().format(formatter), m.getValue()));
        }
        bloodSugarGraph.getData().add(series);
    }

    public void insertTherapy(ActionEvent event) {
        // validazioni minime UI
        if (medicineField.getText() == null || medicineField.getText().isBlank() ||
            amount.getText() == null || amount.getText().isBlank() ||
            measurementUnitDropList.getValue() == null ||
            numberOfIntakes.getValue() == null) {
            AppUtils.showError("Error", "data are missing", "Impossible to insert prescription");
            return;
        }

        final double doses;
        try {
            doses = Double.parseDouble(amount.getText());
        } catch (NumberFormatException nfe) {
            AppUtils.showError("Error", "invalid number", "Check amount");
            return;
        }

        final int qty = numberOfIntakes.getValue();
        final String unit = measurementUnitDropList.getValue();
        final String indications = (otherIndication.getText() == null) ? "" : otherIndication.getText();
        final String drug = medicineField.getText();
        
        Prescription p = new Prescription(
        		0,
        		doses,
        		unit,
        		qty,
        		indications,
        		patient.getPatientId(),
                doctor.getMedicoId(), 
        		drug,
        		"No"
         );
        try {
            int id = clinic.insertPrescription(p);
            p.setId(id);
            
            // aggiorna la lista UI
            prescriptions.add(p);

            // pulizia campi
            medicineField.clear();
            numberOfIntakes.getValueFactory().setValue(1);
            amount.clear();
            otherIndication.clear();
            AppUtils.showConfirmation("Perfect!", "right data", "prescription successfully performed!");

        } catch (SQLException e) {
            AppUtils.showError("DB error", "Insert prescription failed", e.getMessage());
        }
    }

    public void modifyTherapy(ActionEvent e) throws IOException {
        Prescription pSelected = therapyTableAsController.getSelectedItem();
        if (pSelected != null) {
            UpdatePrescriptionController controller =
                ViewNavigator.loadViewOver("updatePrescriptionView.fxml", "Update");
            controller.setPrescription(pSelected);
            controller.setOnUpdate(() -> therapyTableAsController.refresh());
        } else {
            AppUtils.showError("Error", "you must select an Item",
                    "Please, select an item if you would like to modify it");
        }
    }

    public void deleteTherapy(ActionEvent event) {
        Prescription pSelected = therapyTableAsController.getSelectedItem();
        if (pSelected == null) {
            AppUtils.showError("Attenzione", "prescription not selected",
                    "Please, select a prescription to delete");
            event.consume();
            return;
        }

        try {
            int rows = clinic.deletePrescription(pSelected.getIdPrescription());
            if (rows > 0) {
                prescriptions.remove(pSelected);
            } else {
                AppUtils.showError("Error", "impossible to remove this prescription",
                        "Please select another prescription");
            }
        } catch (SQLException e) {
            AppUtils.showError("DB error", "Delete prescription failed", e.getMessage());
        }
    }
}
