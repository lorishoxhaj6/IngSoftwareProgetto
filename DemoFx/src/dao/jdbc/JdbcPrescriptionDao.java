package dao.jdbc;

import java.sql.SQLException;
import java.util.List;

import dao.PrescriptionDao;
import model.DatabaseUtil;
import model.Prescription;

public class JdbcPrescriptionDao implements PrescriptionDao{

	 public List<Prescription> findByPatient(int patientId) throws SQLException {
	        final String sql = "SELECT id, doses, measurementUnit, quantity, indications, drug, doctorId " +
	                           "FROM prescriptions WHERE patientId = ?";
	        return DatabaseUtil.queryList(sql, ps -> {
				try {
					ps.setInt(1, patientId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}, rs -> {
	            return new Prescription(
	                rs.getInt("id"),
	                rs.getDouble("doses"),
	                rs.getString("measurementUnit"),
	                rs.getInt("quantity"),
	                rs.getString("indications"),
	                patientId,
	                rs.getInt("doctorId"),
	                rs.getString("drug")
	            );
	        });
	    }
}
