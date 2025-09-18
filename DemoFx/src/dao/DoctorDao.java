package dao;

import java.sql.SQLException;

import model.Doctor;

public interface DoctorDao {
	Doctor findDoctorById(int doctorId) throws SQLException;
}
