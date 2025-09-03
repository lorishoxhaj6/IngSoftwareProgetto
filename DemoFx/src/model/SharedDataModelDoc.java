package model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SharedDataModelDoc {

    private static  SharedDataModelDoc instance = null;
    private final ObservableList<Patient> itemList;
    private final Doctor doctor;

    private SharedDataModelDoc(Doctor d) {
        this.doctor = d;
        itemList = FXCollections.observableArrayList(d.getPatients());

    }

    public static SharedDataModelDoc getInstance(Doctor d){
        if(instance == null || instance.doctor.getMedicoId() != d.getMedicoId()) {
        	instance = new SharedDataModelDoc(d);
        	
        }
        	
        return instance;
        
    }

   

	public ObservableList<Patient> getItemList() {
        return itemList;
    }
}