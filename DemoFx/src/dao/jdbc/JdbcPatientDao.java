package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dao.PatientDao;
import model.AppUtils;
import model.DatabaseUtil;
import model.Doctor;
import model.Patient;

public class JdbcPatientDao implements PatientDao{

	@Override
	public Doctor findDoctorById(int doctorId) throws SQLException {
		final String sql = "SELECT username, email,password FROM doctors WHERE id = ?";
		String doctorUser = null;
		String doctorEmail = null;
		String docpw = null;
		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, doctorId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					doctorUser = rs.getString("username");
					doctorEmail = rs.getString("email");
					docpw = rs.getString("password");
				}else {
					return null;
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		List<Patient> pazienti = AppUtils.findAllPatient(doctorId);
		
		return new Doctor(doctorUser,docpw,doctorId,pazienti,doctorEmail);
		
	}

	@Override
	 public List<Patient> findAll() throws SQLException  {
        String sql = "SELECT id, username,password, doctor_id, name, surname FROM patients";
     
			return DatabaseUtil.queryList(sql, null, rs ->
			    new Patient(
			        rs.getString("username"),
			        rs.getString("password"),
			        rs.getInt("id"),
			        rs.getInt("doctor_id"),
			        rs.getString("name"),
			        rs.getString("surname")
			    )
			);
    }
}
