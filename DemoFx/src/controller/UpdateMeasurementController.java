package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.AppUtils;
import model.DatabaseUtil;
import model.Measurement;

public class UpdateMeasurementController {
	@FXML
	private TextField valueTextField;
	@FXML
	private RadioButton dopoPastoRb;
	@FXML
	private RadioButton primaPastoRb;
	@FXML
	private DatePicker myDatePicker;
	@FXML
	private ToggleGroup pasto;
	
	// Callback da invocare dopo update riuscito per aggiornare la view parent
    private Runnable onUpdate;
	
	private Measurement m;
	
	public void setMeasurement(Measurement mSelected) {
		this.m = mSelected;
		valueTextField.setText(String.valueOf(mSelected.getValue()));
		myDatePicker.setValue(mSelected.getDateTime().toLocalDate());
		if(mSelected.getMoment().equals("prima pasto")){
			primaPastoRb.setSelected(true);
		}else {
			dopoPastoRb.setSelected(true);
		}
	}
	
	public void setOnUpdate(Runnable onUpdate) {
		//faccio il set della funzione consumer
		this.onUpdate = onUpdate;
	}
	
	public void update(ActionEvent event) {
		//aggiorno il db con i campi modificati
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
		String sql = "UPDATE measurements SET moment = ?, dateTime = ?, value = ? WHERE id = ?";
		LocalDate date = myDatePicker.getValue();
		LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.now());
		final String moment = primaPastoRb.isSelected() ? "prima_pasto" : "dopo_pasto";
		Stage stage;
		int rows = DatabaseUtil.executeUpdate(sql, ps ->{
			ps.setString(1,moment);
			ps.setString(2, dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setDouble(3, value);
			ps.setInt(4, m.getId());
			
			
			
		});
		if(rows > 0) {
			//aggiorno l'oggetto in memoria attraverso i metodi set per ogni campo
			m.setMoment(moment);
            m.setDateTime(dateTime);
            m.setValue(value);
            
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
