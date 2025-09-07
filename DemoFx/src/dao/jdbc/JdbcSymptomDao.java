package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.SymptomDao;
import model.DatabaseUtil;
import model.Symptoms;

public class JdbcSymptomDao implements SymptomDao {
    
	
    public List<Symptoms> findOpenByPatient(int patientId) throws SQLException {
        final String sql = "SELECT id,doctor_id, symptoms, startDateTime, notes FROM symptoms " +
                           "WHERE patient_id = ? AND endDateTime IS NULL";
        return DatabaseUtil.queryList(sql, ps -> {
			try {
				ps.setInt(1, patientId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, rs -> {
            int id = rs.getInt("id");
            LocalDateTime start = LocalDateTime.parse(rs.getString("startDateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return new Symptoms(id, rs.getInt(patientId), patientId, rs.getString("symptoms"), start, rs.getString("notes"));
        });
    }

    
    public int saveSymptom(Symptoms s) throws SQLException {
        final String sql = "INSERT INTO symptoms (patient_id, doctor_id, symptoms, startDateTime, notes) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseUtil.connect();
             PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getPatientId());
            ps.setInt(2, s.getMedicoId());
            ps.setString(3, s.getSymptoms());
            ps.setString(4, s.getDateTimeFormatted());
            ps.setString(5, s.getNotes() == null ? "" : s.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    
    public int resolveSymptom(int id, LocalDateTime end) throws SQLException {
        final String sql = "UPDATE symptoms SET endDateTime = ? WHERE id = ?";
        return DatabaseUtil.executeUpdate(sql, ps -> {
            ps.setString(1, end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setInt(2, id);
        });
    }


	
}

