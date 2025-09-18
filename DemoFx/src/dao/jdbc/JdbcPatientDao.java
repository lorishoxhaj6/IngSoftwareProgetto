package dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.PatientDao;
import model.DatabaseUtil;
import model.Patient;

public class JdbcPatientDao implements PatientDao {

	@Override
	public List<Patient> findAll() throws SQLException {
		String sql = "SELECT id, username,password, doctor_id, name, surname,lastModifiedBy FROM patients";

		return DatabaseUtil.queryList(sql, null,
				rs -> new Patient(rs.getString("username"), rs.getString("password"), rs.getInt("id"),
						rs.getInt("doctor_id"), rs.getString("name"), rs.getString("surname"),
						rs.getInt("lastModifiedBy")));
	}

	public String getInfo(int patientId) throws SQLException {
		String sql = "SELECT informations FROM patients WHERE id = ?";
		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, patientId);
			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next())
					return rs.getString("informations");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int updateInfo(int patientId, String text, int medicoId) throws SQLException {
		String sql = "UPDATE patients SET informations = ?, lastModifiedBy =? WHERE id = ?";

		return DatabaseUtil.executeUpdate(sql, ps -> {
			ps.setString(1, text);
			ps.setInt(2, medicoId);
			ps.setInt(3, patientId);
		});
	}

	@Override
	public List<Patient> findAllPatient(int doctorId) throws SQLException {

		List<Patient> pazienti = new ArrayList<>();
		String sqlPazienti = "SELECT * FROM patients WHERE doctor_id = ?";

		try (Connection con = DatabaseUtil.connect(); PreparedStatement ps2 = con.prepareStatement(sqlPazienti)) {
			ps2.setInt(1, doctorId);

			try (ResultSet rs2 = ps2.executeQuery()) {
				while (rs2.next()) {
					pazienti.add(new Patient(rs2.getString("username"), rs2.getString("password"), rs2.getInt("id"),
							rs2.getInt("doctor_id"), rs2.getString("name"), rs2.getString("surname"),
							rs2.getInt("lastModifiedBy")));
				}
			}
		}

		// 3) Creo oggetto dottore con la lista pazienti
		return pazienti;
	}

}
