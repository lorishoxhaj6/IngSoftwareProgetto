package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dao.InTakeDao;
import model.DatabaseUtil;

public class JdbcInTakeDao implements InTakeDao {

	public int saveInTake(String type, double doses, String unit, LocalDateTime when, int patientId, int doctorId,
			String drug) throws SQLException {
		final String sql = "INSERT INTO patientIntake (type,doses,measurementUnit,dateTime,patientId,doctorId,drug) "
				+ "VALUES (?,?,?,?,?,?,?)";
		try (Connection c = DatabaseUtil.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, type);
			ps.setDouble(2, doses);
			ps.setString(3, unit);
			ps.setString(4, when.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setInt(5, patientId);
			ps.setInt(6, doctorId);
			ps.setString(7, drug);
			return ps.executeUpdate();
		}
	}

	

}
