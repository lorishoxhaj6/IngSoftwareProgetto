package dao;

import java.sql.SQLException;
import java.util.List;

import model.Doctor;
import model.Patient;

/***
 * Input:
 * int doctorId
 * return:
 * Doctor
 */
public interface PatientDao {
	Doctor findDoctorById(int doctorId) throws SQLException;
	List<Patient> findAll() throws SQLException;
	String getInfo(int patientId) throws SQLException;
	int updateInfo(int patientId,String text) throws SQLException;
}
