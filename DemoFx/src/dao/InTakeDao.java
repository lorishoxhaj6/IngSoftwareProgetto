package dao;

import java.sql.SQLException;
import java.time.LocalDateTime;

import model.Intake;

public interface InTakeDao {
	/**
	 * salva l'assunzione nel db
	 * @param p (assunzione da parte del paziente)
	 * @return int (return Sql statement)
	 * @throws SQLException
	 */
	int saveInTake(String type, double doses, String unit, LocalDateTime when, int patientId, int doctorId,
			String drug) throws SQLException;
	
}
