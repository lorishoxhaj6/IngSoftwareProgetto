package dao;

import java.sql.SQLException;

public interface LastPrescriptionDao {
	String getLastPrescriptionReset() throws SQLException;
	int updateLastPrescriptionReset(String today) throws SQLException;
}
