package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.IntakeDao;
import model.DatabaseUtil;
import model.Intake;
import model.Prescription;

public class JdbcIntakeDao implements IntakeDao {

	public int insert(Intake t) throws SQLException {
		final String sql = "INSERT INTO patientIntake (type,doses,measurementUnit,dateTime,patientId,doctorId,drug) "
				+ "VALUES (?,?,?,?,?,?,?)";
		try (Connection c = DatabaseUtil.connect(); PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, t.getType());
			ps.setDouble(2, t.getDoses());
			ps.setString(3, t.getmU());
			ps.setString(4, t.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setInt(5, t.getPatientId());
			ps.setInt(6, t.getDoctorId());
			ps.setString(7, t.getDrug());
			ps.executeUpdate();
			
			 try (ResultSet rs = ps.getGeneratedKeys()) {
			        if (rs.next()) return rs.getInt(1);
			        throw new SQLException("No generated key returned");
			 }
		}
	}
	public List<Intake> findByPatient(int patientId) throws SQLException {
        final String sql = "SELECT id, type, drug, doses, measurementUnit, dateTime, patientId, doctorId " +
                           "FROM patientIntake WHERE patientId = ?";
        return DatabaseUtil.queryList(sql, ps -> {
			try {
				ps.setInt(1, patientId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, rs -> {
            return new Intake(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getDouble("doses"),
                rs.getString("measurementUnit"),
                LocalDateTime.parse(rs.getString("dateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                patientId,
                rs.getInt("doctorId"),
                rs.getString("drug")
            );
        });
    }

	// fai query per controllare assunzione corretta

}
