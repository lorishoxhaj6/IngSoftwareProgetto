package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AppUtils;
import model.DatabaseUtil;
import model.Prescription;

public class UpdatePrescriptionController {
	@FXML
	private TextField medicineField;
	@FXML
	private Spinner<Integer> numberOfIntakes;
	@FXML
	private ComboBox<String> measurementUnit;
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
		amount.setText(String.valueOf(p.getDoses()));
		measurementUnit.getItems().addAll(
			    "mg",          // milligrammi
			    "ml",          // millilitri
			    "UI",          // unità internazionali (es. insulina)
			    "compresse",   // numero di compresse
			    "gocce"        // utile per certi farmaci liquidi
			);
		measurementUnit.setValue(p.getMeasurementUnit());
		otherIndication.setText(p.getIndications());
		numberOfIntakes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, p.getQuantity()));
		
	}
	
	public void setOnUpdate(Runnable onUpdate) {
		//faccio il set della funzione consumer
		this.onUpdate = onUpdate;
	}
	
	public void update(ActionEvent event) {
		//aggiorno il db con i campi modificati
		String sql = "UPDATE prescriptions SET doses = ?, measurementUnit = ?, quantity = ?, indications = ?, drug = ? WHERE id = ?";
		String d = amount.getText();
		Double doses = Double.parseDouble(d);
		String mU = measurementUnit.getValue();
		int quantiy = (int) numberOfIntakes.getValue();
		String indications = otherIndication.getText();
		String drug = medicineField.getText();
	
		Stage stage;
		int rows = DatabaseUtil.executeUpdate(sql, ps ->{
			ps.setDouble(1,doses);
			ps.setString(2, mU);
			ps.setInt(3, quantiy);
			ps.setString(4,indications);
			ps.setString(5,drug);
			ps.setInt(6, p.getIdPrescription());
		});
		if(rows > 0) {
			//aggiorno l'oggetto in memoria attraverso i metodi set per ogni campo
            p.setDoses(doses);
            p.setMeasurementUnit(mU);
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
