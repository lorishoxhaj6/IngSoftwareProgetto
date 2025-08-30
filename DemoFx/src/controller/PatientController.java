package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AppUtils;
import model.DatabaseConnection;
import model.Measurement;
import model.Patient;

public class PatientController extends UserController<Patient> implements Initializable{
	//usa superclasse ma con Patient e non con un tipo generico
	
	@FXML
	TextField valueTextField;
	@FXML
	RadioButton primaPastoRb, dopoPastoRb;
	@FXML
	DatePicker myDatePicker;
	@FXML
	ToggleGroup pasto;
	@FXML
	TableView<Measurement> measurementsTableView;
	@FXML
	TableColumn<Measurement, LocalDate> dateColumn;
	@FXML
	TableColumn<Measurement, String> momentColumn;
	@FXML
	TableColumn<Measurement, Double> valueColumn;
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//collega le colonne della tabella alla ai campi della classe Measurement
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		momentColumn.setCellValueFactory(new PropertyValueFactory<>("moment"));
	    valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
	   
	}
	
	public void visualizza(ActionEvent e) {
		//carico i dati dal Db 
	    ObservableList<Measurement> data = loadMeasurementsFromDB();
	    measurementsTableView.setItems(data);
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
			AppUtils.showError("bene", "dati giusti", "musuraione eseguita con successo!");
			System.out.println("misurazione inserita!");
			
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


	
}
