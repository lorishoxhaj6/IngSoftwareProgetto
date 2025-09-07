package dao;

import java.sql.SQLException;
import java.util.List;

import model.Prescription;

public interface PrescriptionDao {
    List<Prescription> findByPatient(int patientId) throws SQLException;
}
