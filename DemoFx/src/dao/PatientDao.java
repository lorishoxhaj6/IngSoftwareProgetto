package dao;

import java.sql.SQLException;
import java.util.List;

import model.Patient;


public interface PatientDao {
	List<Patient> findAll() throws SQLException;
	String getInfo(int patientId) throws SQLException;
	int updateInfo(int patientId,String text,int medicoId) throws SQLException;
	List<Patient> findAllPatient(int doctorId) throws SQLException;
}
