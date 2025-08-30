package controller;

import java.awt.TextArea;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import model.AppUtils;
import model.DatabaseConnection;
import model.Measurement;
import model.Patient;

public class PatientController extends UserController<Patient> implements Initializable{
	//usa superclasse ma con Patient e non con un tipo generico
	
	@FXML
	private TextField valueTextField;
	@FXML
	private Circle avatarCircle;
	@FXML
	private RadioButton primaPastoRb, dopoPastoRb;
	@FXML
	private DatePicker myDatePicker;
	@FXML
	private ToggleGroup pasto;
	@FXML
	private TableView<Measurement> measurementsTableView;
	@FXML
	private TableColumn<Measurement, LocalDate> dateColumn;
	@FXML
	private TableColumn<Measurement, String> momentColumn;
	@FXML
	private TableColumn<Measurement, Double> valueColumn;
	@FXML
	private ListView<String> symptomsListView;
	@FXML
	private TextField symptomsTextField;
	@FXML
	private Button symptompsAddButton,symptompsEnter;
	@FXML
	private TextArea symptompsNotes;
	@FXML
	private ToggleButton symptomsTb1,symptomsTb2,symptomsTb3,symptomsTb4,symptomsTb5;
	
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//collega le colonne della tabella alla ai campi della classe Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
	    valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
	    
	    Image img = new Image(getClass().getResource("/Image/AvatarPatient.png").toExternalForm());
	    avatarCircle.setFill(new ImagePattern(img));
	    
	    
	   
	}
	
	public void logout() {
		super.logout();
	}
	
	public void inserisciMisurazione(ActionEvent e) {
		// controllo se non ci sono errori di input
		if(myDatePicker.getValue()== null || valueTextField.getText() == null || 
				pasto.getSelectedToggle()== null) {
			AppUtils.showError("Errore", "dati mancanti", "Impossibile caricare misurazione");
			return;
		}
		
		double value;
		try {
			value = Double.parseDouble(valueTextField.getText());
		} catch (NumberFormatException e1){
			AppUtils.showError("Errore", "dati mancanti", "Impossibile caricare misurazione");
			return;
		}
		
		// variabili che mi servono per inserire la misurazione
		String sql = "INSERT INTO measurements (patientId, moment, date, value) VALUES (?,?,?,?)";
		int idMeasurement = -1;
		int userId = user.getId();
		String moment = "";
		LocalDate date = myDatePicker.getValue();
		
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
			ps.setString(3, date.toString());
			ps.setDouble(4, value);
			
			ps.executeUpdate();
			
			try (ResultSet rs = ps.getGeneratedKeys()) { // ottengo la primaryKey id della nuova misurazione
		        if (rs.next()) idMeasurement = rs.getInt(1);
		    }
			
			Measurement m = new Measurement(idMeasurement, userId,moment, date, value);
			measurementsTableView.getItems().add(m);  // aggiungo la nuova misurazione alla tabella
			
			//pulisco tutti i campi dell'inserimento
			myDatePicker.setValue(null);
			valueTextField.setText("");
			pasto.selectToggle(null);
			AppUtils.showConfirmation("bene", "dati giusti", "musurazione eseguita con successo!");
			
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
	    String sql = "SELECT date, moment, value FROM measurements WHERE patientId = ?";

	    try (Connection conn = DatabaseConnection.connect();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, user.getId()); // <-- l'id del paziente loggato
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            LocalDate date = LocalDate.parse(rs.getString("date"));
	            String moment = rs.getString("moment");
	            double value = rs.getDouble("value");

	            list.add(new Measurement(10, user.getId(),moment, date, value));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public void setUser(Patient user) {
		super.setUser(user);
		ObservableList<Measurement> data = loadMeasurementsFromDB();
		measurementsTableView.setItems(data);
	}

	public void insertToggleSymptoms() {
		
		if(symptomsTb1.isSelected()) {
			symptomsListView.getItems().add(symptomsTb1.getText());
		}
		if(symptomsTb2.isSelected()) {
			symptomsListView.getItems().add(symptomsTb2.getText());
		}
		if(symptomsTb3.isSelected()) {
			symptomsListView.getItems().add(symptomsTb3.getText());
		}
		if(symptomsTb4.isSelected()) {
			symptomsListView.getItems().add(symptomsTb4.getText());
		}
		if(symptomsTb5.isSelected()) {
			symptomsListView.getItems().add(symptomsTb5.getText());
		}
		
		
	}
	
	public void insertSymptomsOnButtonClick() {
		
		if(symptomsTextField.getText().isEmpty()) {
			AppUtils.showError("Nessun sintomo indicato", "Sintomo", "scrivere un sintomo e poi cliccare +");
		}
		
		symptomsListView.getItems().add(symptomsTextField.getText());
		
		symptomsTextField.clear();
		
	}
	
	
	
}
