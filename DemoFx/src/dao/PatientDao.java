package dao;

import java.sql.SQLException;

import model.Doctor;

/***
 * Input:
 * int doctorId
 * return:
 * Doctor
 */
public interface PatientDao {
	Doctor findDoctorById(int doctorId) throws SQLException;
}
