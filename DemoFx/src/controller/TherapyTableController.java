package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Prescription;

public class TherapyTableController implements Initializable {

	@FXML
	private TableView<Prescription> table;
	@FXML
	private TableColumn<Prescription, Double> dosesColumn;
	
	@FXML
	private TableColumn<Prescription,String> unitColumn;
	@FXML
	private TableColumn<Prescription,String> takenColumn;

	@FXML
	private TableColumn<Prescription, String> drugColumn;

	@FXML
	private TableColumn<Prescription, String> indicationColumn;

	@FXML
	private TableColumn<Prescription, Integer> quantityColumn;

	// binding = collegamento automatico tra due property
	// contenitore osservabile che può tenere al suo interno qualsiasi oggetto di
	// tipo T, è una proprety più generica
	// rispetto StringProporty..., in questo caso ci metto al suo interno oggetti
	// Consumer<Prescription>
	// passando un consumer diamo la possibilità al parent di passare una funzione
	// quando succede qualcosa
	private final ObjectProperty<Consumer<Prescription>> onSelect = new SimpleObjectProperty<>();

	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//equivalente all'injection che viene fatta per iniettare i valori di un oggetto all'interno di una colonna
		dosesColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper(data.getValue().getDoses()));
		unitColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMeasurementUnit()));
		indicationColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIndications()));
		takenColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTaken()));
		quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
		drugColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDrug()));
		
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, it) -> {
            if (it != null && onSelect.get() != null) onSelect.get().accept(it);
        });
	}

	// API pubblica per i parent
    public void setItems(ObservableList<Prescription> newPrescriptions) {
        table.setItems(newPrescriptions); // condivido la stessa lista, in tutte le view in cui implemento la tabella therapy
    }

    public void setOnSelect(Consumer<Prescription> handler) {
        onSelect.set(handler);
    }
    
    //restituisce l'elemento selezionato
    public Prescription getSelectedItem() {
        return table.getSelectionModel().getSelectedItem();
    }
    
    //restituisce gli elementi contenuti nella tableView sotto forma di ObservableList
    public ObservableList<Prescription> getItems() {
        return table.getItems();
    }
    
    //effettua il refresh della table
    public void refresh() {
        table.refresh();
    }
    
}
