package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.PrescriptionDao;
import model.DatabaseUtil;
import model.Prescription;

public class JdbcPrescriptionDao implements PrescriptionDao {

	public List<Prescription> findByPatient(int patientId) throws SQLException {
		String sql = "SELECT * "
				+ "FROM prescriptions WHERE patientId = ?";
		return DatabaseUtil.queryList(sql, ps -> {
			try {
				ps.setInt(1, patientId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, rs -> {
			return new Prescription(rs.getInt("id"), rs.getDouble("doses"), rs.getString("measurementUnit"),
					rs.getInt("quantity"), rs.getString("indications"), patientId, rs.getInt("doctorId"),
					rs.getString("drug"), rs.getString("taken"), rs.getString("lastModifiedBy"));
		});
	}

	public boolean prescriptionTaken(int patientId) throws SQLException {
		final String sql = "SELECT taken FROM prescriptions WHERE patientId = ?";

		List<String> list = DatabaseUtil.queryList(sql, ps -> {
			try {
				ps.setInt(1, patientId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, rs -> rs.getString("taken"));

		// true se tutte le righe hanno "Yes"
		return list.stream().allMatch("Yes"::equals);
	}

	public List<Prescription> findAll() throws SQLException {
		final String sql = "SELECT * "
				+ "FROM prescriptions";
		return DatabaseUtil.queryList(sql, ps -> {
		}, rs -> {
			return new Prescription(rs.getInt("id"), rs.getDouble("doses"), rs.getString("measurementUnit"),
					rs.getInt("quantity"), rs.getString("indications"), rs.getInt("patientId"), rs.getInt("doctorId"),
					rs.getString("drug"), rs.getString("taken"),rs.getString("lastModifiedBy"));
		});
	}

	public int deletePrescription(int idPrescription) throws SQLException {
		String sql = "DELETE FROM prescriptions WHERE id = ?";
		return DatabaseUtil.executeUpdate(sql, ps -> ps.setInt(1, idPrescription));
	}

	public int updatePreso(String taken, int id) {
		String sql = "UPDATE prescriptions SET taken = ? WHERE id = ?";
		int rows = DatabaseUtil.executeUpdate(sql, ps -> {
			ps.setString(1, taken);
			ps.setInt(2, id);
		});
		return rows;
	}

	public int insert(Prescription p) throws SQLException {
		final String sql = "INSERT INTO prescriptions (doses, measurementUnit, quantity, indications,patientId,doctorId,drug,taken,lastModifiedBy) VALUES (?,?,?,?,?,?,?,?,?)";
		try (Connection c = DatabaseUtil.connect();
				PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
			ps.setDouble(1, p.getDoses());
			ps.setString(2, p.getMeasurementUnit());
			ps.setInt(3, p.getQuantity());
			ps.setString(4, p.getIndications());
			ps.setInt(5, p.getPatientId());
			ps.setInt(6, p.getDoctorId());
			ps.setString(7, p.getDrug());
			ps.setString(8,p.getTaken());
			ps.setString(9,p.getLastModifiedBy());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : -1;
			}
		}
	}

	@Override
	 public int updatePrescriptionReset() throws SQLException {
        final String resetSql = "UPDATE prescriptions SET taken = ?";
        return DatabaseUtil.executeUpdate(resetSql, ps -> ps.setString(1, "No"));
    }
}
