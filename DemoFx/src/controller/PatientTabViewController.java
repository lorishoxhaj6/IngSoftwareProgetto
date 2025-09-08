package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.AppUtils;
import model.Doctor;
import model.Patient;

public class PatientTabViewController implements Initializable {

    @FXML private ListView<Patient> allPatientsListView;
    @FXML private ListView<Patient> patientsListView;
    @FXML private Button visualizeButton;
    @FXML private Button visualizeButton1;

    private Doctor currentDoctor;

    //  callback impostata dal parent (DoctorController o DoctorDashboardController)
    private Consumer<Patient> onVisualize = p -> {};

    @Override
    public void initialize(URL u, ResourceBundle r) {
        
    }

    @FXML
    public void visualize(ActionEvent event) {
        Patient selected =
            (event.getSource() == visualizeButton)
                ? patientsListView.getSelectionModel().getSelectedItem()
                : allPatientsListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AppUtils.showError("Errore caricamento", "nessun paziente selezionato",
                               "Perfavore seleziona un paziente prima di visualizzare");
            return;
        }
        // delega al parent cosa fare (aprire dashboard o aggiornare)
        onVisualize.accept(selected);
    }

    // --- API pubblica ---
    public void setAllPatients(ObservableList<Patient> all){ allPatientsListView.setItems(all); }
    public void setFilteredPatient(ObservableList<Patient> ps){ patientsListView.setItems(ps); }
    public void setDoctor(Doctor d){ this.currentDoctor = d; }
    public void setOnVisualize(Consumer<Patient> cb) {
        this.onVisualize = (cb != null) ? cb : p -> {};
    }

    public void refresh() { patientsListView.refresh(); }
}


