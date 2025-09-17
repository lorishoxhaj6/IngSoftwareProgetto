package dao;

import java.sql.SQLException;
import java.util.List;

import model.Intake;

public interface IntakeDao {
	/**
	 * salva l'assunzione nel db
	 * @param p (assunzione da parte del paziente)
	 * @return int (return Sql statement)
	 * @throws SQLException
	 */
	int insert(Intake t) throws SQLException;
	/**
	 * 
	 * @param idPatient
	 * @return la lista delle assunzioni del paziente con id = ?
	 * @throws SQLException
	 */
	List<Intake> findByPatient(int idPatient) throws SQLException;
	/**
	 * elimina l'assunzione nel db
	 * @param intakeId id dell'assunzione
	 * @return int (return Sql statement)
	 * @throws SQLException
	 */
	int delete(int intakeId) throws SQLException;
	/**
	 * Ritorna boolean se il paziente ha preso i farmaci gli ultimi 3 giorni
	 * @param patientId
	 * @return True/false
	 * @throws SQLException
	 */
	boolean fetchLastThreeDaysIntakes(int patientId) throws SQLException;
}
