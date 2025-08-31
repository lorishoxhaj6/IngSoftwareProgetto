package controller;


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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AppUtils;
import model.DatabaseConnection;
import model.Measurement;
import model.Patient;
import model.Symptoms;

public class PatientController extends UserController<Patient> implements Initializable{
	//usa superclasse ma con Patient e non con un tipo generico
	
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
	private Button symptompsAddButton,symptompsEnter;
	@FXML
	private TextArea symptomsNotes;
	@FXML
	private ToggleButton symptomsTb1,symptomsTb2,symptomsTb3,symptomsTb4,symptomsTb5;
	
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//collega le colonne della tabella alla ai campi della classe Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormatted"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
	    valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
 


	}
	
	public void logout() {
		super.logout();
	}
	
	public void inserisciMisurazione(ActionEvent e) {
		// controllo se non ci sono errori di input
		if(myDatePicker.getValue()== null || valueTextField.getText() == null || 
				pasto.getSelectedToggle()== null) {
			AppUtils.showError("Error", "data are missing", "Impossible to insert measurement");
			return;
		}
		
		double value;
		try {
			value = Double.parseDouble(valueTextField.getText());
		} catch (NumberFormatException e1){
			AppUtils.showError("Error", "data are missing", "Impossible to insert measurement");
			return;
		}
		
		// variabili che mi servono per inserire la misurazione
		String sql = "INSERT INTO measurements (patientId, moment, dateTime, value) VALUES (?,?,?,?)";
		int idMeasurement = -1;
		int userId = user.getId();
		String moment = "";
		LocalDate date = myDatePicker.getValue();
		LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.now());
		
		// inserisco nel database la nuova misurazione
		try(Connection con = DatabaseConnection.connect();   
				PreparedStatement ps = con.prepareStatement(sql)){
			
			ps.setInt(1,userId);
			if(primaPastoRb.isSelected()) {
				ps.setString(2, "prima pasto");
				moment = "prima pasto";}
			else {
				ps.setString(2, "dopo pasto");
				moment = "dopo pasto";}
			ps.setString(3, dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setDouble(4, value);
			
			ps.executeUpdate();
			
			try (ResultSet rs = ps.getGeneratedKeys()) { // ottengo la primaryKey id della nuova misurazione
		        if (rs.next()) idMeasurement = rs.getInt(1);
		    }
			
			Measurement m = new Measurement(idMeasurement, userId,moment, dateTime, value);
			measurementsTableView.getItems().add(m);  // aggiungo la nuova misurazione alla tabella
			
			//pulisco tutti i campi dell'inserimento
			myDatePicker.setValue(null);
			valueTextField.setText("");
			pasto.selectToggle(null);
			AppUtils.showConfirmation("Perfect!", "right data", "measurement successfully performed!");
			
			/*Sistema di segnalazione per registrazioni oltre le soglie a seconda della gravità
			if(moment.equals("prima pasto")) {
					if( value >= 60 && value <= 70 || value >= 140 && value <= 150) { // codice bianco
						// Invia alert dottore con codice bianco
					}else {
						if(value >= 50 && value < 60 || value > 150 && value <= 160) {
							//Invia alert dottore con codice giallo
						}else {
							if(value < 50 || value > 160)
								//Invia alert dottore codice rosso
						}
					}
					
			}
			else {
				if(value > 180 && value <= 190) {
					//codice bianco
				}else {
					if(value >190 && value <= 200) {
						//codice arancio
					}
					//codice rosso
				}
			}*/
			//System.out.println("misurazione inserita!");
			
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	public ObservableList<Measurement> loadMeasurementsFromDB() {
	    ObservableList<Measurement> list = FXCollections.observableArrayList();
	    String sql = "SELECT dateTime, moment, value FROM measurements WHERE patientId = ?";

	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, user.getId()); // <-- l'id del paziente loggato
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	        	String raw = rs.getString("dateTime");
	        	LocalDateTime date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	            String moment = rs.getString("moment");
	            double value = rs.getDouble("value");

	            list.add(new Measurement(10, user.getId(),moment, date, value)); // perché si passa 10 in id?
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public void setUser(Patient user) {
		super.setUser(user);
		ObservableList<Measurement> measurments = loadMeasurementsFromDB();
		ObservableList<Symptoms> symptoms = loadSymptomsFromDB();
		measurementsTableView.setItems(measurments);
		symptomsVisualization.setItems(symptoms);
	}

	public void insertToggleSymptoms() {
		
		if(symptomsTb1.isSelected()) {
			symptomsListView.getItems().add(symptomsTb1.getText());
			symptomsTb1.setSelected(false);
		}
		if(symptomsTb2.isSelected()) {
			symptomsListView.getItems().add(symptomsTb2.getText());
			symptomsTb2.setSelected(false);
		}
		if(symptomsTb3.isSelected()) {
			symptomsListView.getItems().add(symptomsTb3.getText());
			symptomsTb3.setSelected(false);
		}
		if(symptomsTb4.isSelected()) {
			symptomsListView.getItems().add(symptomsTb4.getText());
			symptomsTb4.setSelected(false);
		}
		if(symptomsTb5.isSelected()) {
			symptomsListView.getItems().add(symptomsTb5.getText());
			symptomsTb5.setSelected(false);
		}
		
		
	}
	
	public void insertSymptomsOnButtonClick() {
		
		if(symptomsTextField.getText().isEmpty()) {
			AppUtils.showError("None symptom selected", "Symptom", "Please, write a symptom and then click +");
		}
		
		symptomsListView.getItems().add(symptomsTextField.getText());
		
		symptomsTextField.clear();
		
	}
	
	public void deleteSymptomSelected() {
		
		if(symptomsListView.getItems().isEmpty()) {
			AppUtils.showError("No Symptoms Available",
				    "Unable to Delete",
				    "Please add at least one symptom before attempting to delete.");
			return;
		}
		
		String sel = symptomsListView.getSelectionModel().getSelectedItem();
		if(sel == null || sel.isBlank()) {
			AppUtils.showError("No Symptom Selected",
				    "Unable to Delete Symptom",
				    "Please select a symptom from the list before attempting to delete it.");
			return;
		}else {
			symptomsListView.getItems().remove(sel);
		}
		
	}
	
	public void enterSymptoms() {

	    // 1) Validazioni preliminari
	    if (symptomsListView.getItems().isEmpty()) {
	        AppUtils.showError("No Symptoms Available",
	                "Unable to enter the symptoms",
	                "Please add at least one symptom before attempting to enter.");
	        return;
	    }

	    // Verifica che la data sia selezionata PRIMA di creare LocalDateTime
	    LocalDate selectedDate = symptomDatePicker.getValue();
	    if (selectedDate == null) {
	        AppUtils.showError("No date selected",
	                "Unable to enter the symptoms",
	                "Please select a Date before attempting to enter");
	        return;
	    }

	    // 2) Costruisci il timestamp (ora attuale nel giorno scelto)
	    LocalDateTime when = LocalDateTime.of(selectedDate, LocalTime.now());

	    // 3) Prepara SQL
	    String sql = "INSERT INTO symptoms (patient_id, doctor_id, symptoms, startDateTime, notes) VALUES (?,?,?,?,?)";

	    try (Connection con = DatabaseConnection.connect();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        // Unisci i sintomi in una stringa
	        ObservableList<String> listSymptoms = symptomsListView.getItems();
	        String symptomsText = String.join(",", listSymptoms);

	        // 4) Parametri
	        ps.setInt(1, user.getId());                        // patient_id
	        ps.setInt(2, user.getMedicoId());                  // doctor_id
	        ps.setString(3, symptomsText);                     // symptoms

	        
	        ps.setString(4, when.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));                  

	       
	        String notes = (symptomsNotes != null) ? symptomsNotes.getText() : "";
	        ps.setString(5, notes);

	        ps.executeUpdate();

	        AppUtils.showConfirmation("Perfect!", "right data", "symptoms successfully recorded!");
	        
	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                int generatedId = rs.getInt(1);
	                Symptoms s = new Symptoms(generatedId,user.getId(),user.getMedicoId(),symptomsText, when, notes);
	                symptomsVisualization.getItems().add(s);
	            }
	        }
	        // 5) Pulizia UI
	        symptomsListView.getItems().clear();
	        symptomDatePicker.setValue(null);
	        if (symptomsNotes != null) symptomsNotes.clear();

	    } catch (SQLException e) {
	        System.out.println("Errore inserimento sintomi in db");
	        e.printStackTrace();
	    }
	}
	
	public ObservableList<Symptoms> loadSymptomsFromDB() {
		
	    ObservableList<Symptoms> list = FXCollections.observableArrayList();
	    String sql = "SELECT id,symptoms, startDateTime, notes FROM symptoms WHERE patient_id = ?";

	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, user.getId()); // <-- l'id del paziente loggato
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	        	int symptomId = rs.getInt("id");
	        	String raw = rs.getString("startDateTime");
	        	LocalDateTime date = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	            String symptoms = rs.getString("symptoms");
	            String notes = rs.getString("notes");

	            list.add(new Symptoms(symptomId,user.getMedicoId(), user.getId(),symptoms, date, notes));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public void resolveSymptoms() {
	
	    if (symptomsVisualization.getItems().isEmpty()) {
	        AppUtils.showError("No Symptoms to resolve",
	                "Unable to resolve the symptoms",
	                "Please add at least one symptom before attempting to resolve one.");
	        return;
	    }

	    
	    Symptoms selectedSymptom = symptomsVisualization.getSelectionModel().getSelectedItem();
	    LocalDateTime when = LocalDateTime.now();

	    String sql = "UPDATE symptoms SET endDateTime = ? WHERE id = ?";

	    try (Connection con = DatabaseConnection.connect();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, when.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	        ps.setInt(2, selectedSymptom.getSymptomId());
	       
	        int rows = ps.executeUpdate();
	        
	        if(rows>0) {
	        	AppUtils.showConfirmation("Perfect!", "right resolution", "symptom successfully resolved!");
	        	symptomsVisualization.getItems().remove(selectedSymptom);
	        }else {
	        	AppUtils.showError("Not Found",
	                    "Symptom not updated",
	                    "Could not find the selected symptom in the database.");
	        }
	    } catch (SQLException e) {
	        System.out.println("Errore inserimento sintomi in db");
	        e.printStackTrace();
	    }
		
	}

	
}
