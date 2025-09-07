package dao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import model.Symptoms;

public interface SymptomDao {
	/**
	 * Salva il sintomo nel Db e restituisce l'id
	 * del sintomo
	 * @param s (Sintomo)
	 * @return int (id del sintomo)
	 */
	int saveSymptom(Symptoms s) throws SQLException;
	/**
	 * Risolve il sintomo, in quanto passato al paziente, e registra
	 * la data e il tempo di fine sintomo
	 * @param dateTime
	 * @param patientId
	 * @return int le righe aggiornate
	 * @throws SQLException
	 */
	int resolveSymptom(int patientId,LocalDateTime dateTime) throws SQLException;
	/**
	 * Trova tutti i sintomy nel db legati al paziente ancora aperti ovvero non risolti
	 * @param patientId
	 * @return List<Symptoms> 
	 * @throws SQLException
	 */
	List<Symptoms> findOpenByPatient(int patientId) throws SQLException;
}
