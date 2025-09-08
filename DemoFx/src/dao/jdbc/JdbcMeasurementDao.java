package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.MeasurementDao;
import model.DatabaseUtil;
import model.Measurement;

public class JdbcMeasurementDao implements MeasurementDao {
    
    public List<Measurement> findByPatient(int patientId) throws SQLException {
        final String sql = "SELECT id, dateTime, moment, value FROM measurements WHERE patientId = ?";
        return DatabaseUtil.queryList(sql, ps -> {
			try {
				ps.setInt(1, patientId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, rs -> {
            int id = rs.getInt("id");
            LocalDateTime dt = LocalDateTime.parse(rs.getString("dateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return new Measurement(id, patientId, rs.getString("moment"), dt, rs.getDouble("value"));
        });
    }

    @Override
    public int insert(Measurement m) throws SQLException {
        final String sql = "INSERT INTO measurements (patientId, moment, dateTime, value) VALUES (?,?,?,?)";
        try (Connection c = DatabaseUtil.connect();
             PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getPatientId());
            ps.setString(2, m.getMoment());
            ps.setString(3, m.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setDouble(4, m.getValue());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public int deleteById(int id) throws SQLException {
        final String sql = "DELETE FROM measurements WHERE id = ?";
        return DatabaseUtil.executeUpdate(sql, ps -> ps.setInt(1, id));
    }
}
