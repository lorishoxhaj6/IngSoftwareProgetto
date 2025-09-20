package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.DoctorDao;
import model.DatabaseUtil;
import model.Doctor;
import model.Patient;

public class JdbcDoctorDao implements DoctorDao{

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
			
			
			return new Doctor(doctorUser,docpw,doctorId,(Patient)null,doctorEmail);
	}
	
}
