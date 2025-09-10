package dao;

import java.sql.SQLException;
import java.util.List;

import model.DatabaseUtil;
import model.Prescription;

public interface PrescriptionDao {
    List<Prescription> findByPatient(int patientId) throws SQLException;
    int deletePrescription(int idPrescription) throws SQLException;
	int insert(Prescription p) throws SQLException;
	public int updatePreso (String taken, int id) throws SQLException;
}
