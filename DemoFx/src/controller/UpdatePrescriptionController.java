package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.AppUtils;
import model.DatabaseUtil;
import model.Measurement;
import model.Prescription;

public class UpdatePrescriptionController {
	@FXML
	private TextField medicineField;
	@FXML
	private Spinner numberOfIntakes;
	@FXML
	private TextField amount;
	@FXML
	private TextArea otherIndication;
	// Callback da invocare dopo update riuscito per aggiornare la view parent
    private Runnable onUpdate;
	
	private Prescription p;
	
	public void setPrescription(Prescription pSelected) {
		this.p = pSelected;
		medicineField.setText(p.getDrug());
		amount.setText(p.getDoses());
		otherIndication.setText(p.getIndications());
		numberOfIntakes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, p.getQuantity()));
		
	}
	
	public void setOnUpdate(Runnable onUpdate) {
		//faccio il set della funzione consumer
		this.onUpdate = onUpdate;
	}
	
	public void update(ActionEvent event) {
		//aggiorno il db con i campi modificati
		String sql = "UPDATE prescriptions SET doses = ?, quantity = ?, indications = ?, drug = ? WHERE id = ?";
		String doses = amount.getText();
		int quantiy = (int) numberOfIntakes.getValue();
		String indications = otherIndication.getText();
		String drug = medicineField.getText();
	
		Stage stage;
		int rows = DatabaseUtil.executeUpdate(sql, ps ->{
			ps.setString(1,doses);
			ps.setInt(2, quantiy);
			ps.setString(3,indications);
			ps.setString(4,drug);
			ps.setInt(5, p.getIdPrescription());
		});
		if(rows > 0) {
			//aggiorno l'oggetto in memoria attraverso i metodi set per ogni campo
            p.setDoses(doses);
            p.setDrug(drug);
            p.setIndications(indications);
            p.setQuantity(quantiy);
            
            //se onUpdate è stato settato allora posso
            if(onUpdate != null)
            	onUpdate.run();
            
			AppUtils.showConfirmation("ottimo", "measurement updated", " ");
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();
		}
		else
			AppUtils.showError("errore","measurement NOT updated", " ");
	}

}
