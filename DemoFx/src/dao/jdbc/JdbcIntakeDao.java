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
import javafx.collections.ObservableList;
import model.DatabaseUtil;
import model.Intake;

public class JdbcIntakeDao implements IntakeDao {

	public int insert(Intake t) throws SQLException {
		final String sql = "INSERT INTO patientIntake (dateTime,patientId) "
				+ "VALUES (?,?)";
		try (Connection c = DatabaseUtil.connect(); PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, t.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			ps.setInt(2, t.getPatientId());
			ps.executeUpdate();
			
			 try (ResultSet rs = ps.getGeneratedKeys()) {
			        if (rs.next()) return rs.getInt(1);
			        throw new SQLException("No generated key returned");
			 }
		}
	}
	
	public int delete(int intakeId) throws SQLException {
	    final String sql = "DELETE FROM patientIntake WHERE id = ?";
	    try (Connection c = DatabaseUtil.connect();
	         PreparedStatement ps = c.prepareStatement(sql)) {
	        ps.setInt(1, intakeId);
	        return ps.executeUpdate(); // 0 o 1
	    }
	}
	
	public List<Intake> findByPatient(int patientId) throws SQLException {
        final String sql = "SELECT id, dateTime, patientId " +
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
                LocalDateTime.parse(rs.getString("dateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                patientId
            );
        });
    }
	
	public boolean fetchLastThreeDaysIntakes(int patientId) throws SQLException {
	    String from = LocalDateTime.now().minusDays(3).withNano(0).toString(); // es. 2025-09-14T09:17:03
	    String to   = LocalDateTime.now().withNano(0).toString();              // es. 2025-09-17T09:17:03

	    final String sql = """
	        SELECT id, dateTime
	        FROM patientIntake
	        WHERE patientId = ?
	          AND dateTime BETWEEN ? AND ?
	        """;

	    ObservableList<Intake> list = DatabaseUtil.queryList(sql, ps -> {
	        try {
				ps.setInt(1, patientId);
				ps.setString(2, from);
		        ps.setString(3, to);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }, rs -> new Intake(
	            rs.getInt("id"),
	            LocalDateTime.parse(rs.getString("dateTime")), 
	            patientId
	    ));

	    return list.size() >= 3;
	}


	

}
