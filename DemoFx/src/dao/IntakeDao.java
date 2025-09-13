package dao;

import java.sql.SQLException;

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
	 * elimina l'assunzione nel db
	 * @param p (assunzione da parte del paziente)
	 * @return int (return Sql statement)
	 * @throws SQLException
	 */
	
}
