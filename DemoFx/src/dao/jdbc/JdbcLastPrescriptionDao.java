package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.LastPrescriptionDao;
import model.DatabaseUtil;

public class JdbcLastPrescriptionDao implements LastPrescriptionDao{

	
	public String getLastPrescriptionReset() throws SQLException {
		String selectSql = "SELECT date FROM lastPrescriptionReset LIMIT 1";
		String lastReset = "";
		try (Connection con = DatabaseUtil.connect(); 
				PreparedStatement psSelect = con.prepareStatement(selectSql);) {
			try (ResultSet rs = psSelect.executeQuery()) {
				if (rs.next()) {
					lastReset = rs.getString("date");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lastReset;
	}

	@Override
	public int updateLastPrescriptionReset(String today) throws SQLException {
		String updateSql = "UPDATE lastPrescriptionReset SET date = ?"; // aggiorna data reset
		return DatabaseUtil.executeUpdate(updateSql, ps -> {
			ps.setString(1, today);
		});
	}
	
}
