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
import model.Doctor;
import model.Prescription;

public class UpdatePrescriptionController extends DoctorController{
	
	
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

	    if (p == null) {
	        AppUtils.showError("Errore", "Nessuna prescrizione selezionata",
	            "Seleziona una prescrizione da modificare.");
	        return;
	    }

	    // Validazioni UI
	    if (medicineField.getText() == null || medicineField.getText().isBlank()
	        || amount.getText() == null || amount.getText().isBlank()
	        || measurementUnit.getValue() == null
	        || numberOfIntakes.getValue() == null) {
	        AppUtils.showError("Error", "data are missing", "Impossible to update prescription");
	        return;
	    }

	    final double doses;
	    try {
	        doses = Double.parseDouble(amount.getText());
	    } catch (NumberFormatException nfe) {
	        AppUtils.showError("Error", "invalid number", "Check amount");
	        return;
	    }

	    final String mU = measurementUnit.getValue();
	    final int quantity = numberOfIntakes.getValue();
	    final String indications = otherIndication.getText() == null ? "" : otherIndication.getText();
	    final String drug = medicineField.getText();
	    final String usernameDoc = user.getUsername();
	    
	    final String sql =
	        "UPDATE prescriptions SET doses = ?, measurementUnit = ?, quantity = ?, " +
	        "indications = ?, drug = ?, lastModifiedBy = ? WHERE id = ?";

	    int rows = DatabaseUtil.executeUpdate(sql, ps -> {
	        ps.setDouble(1, doses);
	        ps.setString(2, mU);
	        ps.setInt(3, quantity);
	        ps.setString(4, indications);
	        ps.setString(5, drug);
	        ps.setString(6, usernameDoc);       // <- medico che modifica
	        ps.setInt(7, p.getIdPrescription());    // <- id prescrizione
	    });

	    if (rows > 0) {
	        // aggiorna oggetto in memoria
	        p.setDoses(doses);
	        p.setMeasurementUnit(mU);
	        p.setDrug(drug);
	        p.setIndications(indications);
	        p.setQuantity(quantity);
	        p.setLastModifiedBy(usernameDoc);

	        if (onUpdate != null) onUpdate.run();

	        AppUtils.showConfirmation("ottimo", "measurement updated", " ");
	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        stage.close();
	    } else {
	        AppUtils.showError("errore", "measurement NOT updated", " ");
	    }
	}

	public void setUser(Doctor user) {
		super.user = user;
	}
}
