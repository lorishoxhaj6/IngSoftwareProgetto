package dao;

import java.sql.SQLException;
import java.util.List;

import model.Measurement;


public interface MeasurementDao {
	/**
	 * Salva la misurazione effettuata dal paziente
	 * e restituisce l'id della misurazione
	 * @param m (la misurazione)
	 * @return int (id della misurazione)
	 */
	int saveMeasurement(Measurement m) throws SQLException;
	/**
	 * Elimina la misurazione dal db in base all'id 
	 * @param id (id della misurazione da eliminare)
	 * @return int il numero di righe che ha eliminato
	 */
	int deleteMeasurement(int id) throws SQLException;
	/**
	 * Trova tutte le misurazioni nel Db che corrispondono al id del paziente
	 * passato come parametro
	 * @param patientId 
	 * @return list<Measurement>
	 */
	List<Measurement> findByPatient(int patientId) throws SQLException;
}
