package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Classe che implementa un "Singleton parametrizzato" (simile a un Multiton).
 * 
 * La sua funzione è fornire un'unica istanza condivisa di dati (lista di pazienti)
 * relativa a un certo medico. 
 * 
 * In questo modo, due o più view che si riferiscono allo stesso medico vedono 
 * la stessa ObservableList<Patient> e quindi rimangono sincronizzate.
 */
public class SharedDataModelDoc {
    
    // Istanza statica della classe: garantisce che ci sia un solo oggetto "attivo" alla volta.
    private static SharedDataModelDoc instance = null;
    
    // Lista osservabile di pazienti, condivisa tra più view. 
    // ObservableList è un tipo speciale che notifica automaticamente le view quando cambia.
    private final ObservableList<Patient> itemList;
    
    // Il medico a cui è associato questo modello di dati.
    private final Doctor doctor;

    /**
     * Costruttore privato: impedisce la creazione diretta dall'esterno.
     * Viene chiamato solo da getInstance().
     */
    private SharedDataModelDoc(Doctor d) {
        this.doctor = d;
        // Inizializza la lista osservabile con i pazienti del medico passato.
        itemList = FXCollections.observableArrayList(d.getPatients());
    }

    /**
     * Metodo di accesso globale all'istanza.
     * Se non esiste ancora, viene creata.
     * Se esiste ma è associata a un altro medico, viene ricreata con il nuovo medico.
     *
     * @param d il medico per cui si vuole ottenere la lista condivisa
     * @return l'istanza di SharedDataModelDoc associata a quel medico
     */
    public static SharedDataModelDoc getInstance(Doctor d){
        if (instance == null || instance.doctor.getMedicoId() != d.getMedicoId()) {
            // Ricrea l'istanza se è la prima volta o se cambia il medico
            instance = new SharedDataModelDoc(d);
        }
        return instance;
    }

    /**
     * Restituisce la lista osservabile dei pazienti associati al medico.
     * Essendo osservabile, le view collegate si aggiornano automaticamente 
     * quando la lista viene modificata (aggiunte, rimozioni, ecc.).
     */
    public ObservableList<Patient> getItemList() {
        return itemList;
    }
    
    public Doctor getDoctor() {
    	return this.doctor;
    }
}
