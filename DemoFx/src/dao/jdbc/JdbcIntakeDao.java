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

public class JdbcIntakeDao implements IntakeDao {

	public int insert(Intake t) throws SQLException {
		final String sql = "INSERT INTO patientIntake (doses,measurementUnit,dateTime,patientId,doctorId,drug) "
				+ "VALUES (?,?,?,?,?,?)";
		try (Connection c = DatabaseUtil.connect(); PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
			ps.setDouble(1, t.getDoses());
			ps.setString(2, t.getmU());
			ps.setString(3, t.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setInt(4, t.getPatientId());
			ps.setInt(5, t.getDoctorId());
			ps.setString(6, t.getDrug());
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
				e.printStackTrace();
			}
		}, rs -> {
            return new Intake(
                rs.getInt("id"),
                rs.getDouble("doses"),
                rs.getString("measurementUnit"),
                LocalDateTime.parse(rs.getString("dateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                patientId,
                rs.getInt("doctorId"),
                rs.getString("drug")
            );
        });
    }
	
	

	

}
