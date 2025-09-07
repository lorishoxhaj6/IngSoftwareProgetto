package facade;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import dao.InTakeDao;
import dao.MeasurementDao;
import dao.PatientDao;
import dao.PrescriptionDao;
import dao.SymptomDao;
import model.Doctor;
import model.Measurement;
import model.Prescription;
import model.Symptoms;

//service/ClinicFacade.java
public class ClinicFacade {
	
 private final PatientDao patientDao;
 private final MeasurementDao measurementDao;
 private final SymptomDao symptomsDao;
 private final PrescriptionDao prescriptionDao;
 private final InTakeDao intakeDao;

 public ClinicFacade(PatientDao p, MeasurementDao m, SymptomDao s, PrescriptionDao pr, InTakeDao i) {
     this.patientDao = p; this.measurementDao = m; this.symptomsDao = s; this.prescriptionDao = pr; this.intakeDao = i;
 }

 public Doctor loadDoctorInfo(int doctorId) throws SQLException {
     return patientDao.findDoctorById(doctorId);
 }
 public List<Measurement> loadMeasurements(int patientId) throws SQLException {
     return measurementDao.findByPatient(patientId);
 }
 public int addMeasurement(Measurement m) throws SQLException {
     return measurementDao.saveMeasurement(m);
 }
 public int deleteMeasurement(int id) throws SQLException {
     return measurementDao.deleteById(id);
 }
 public List<Symptoms> loadOpenSymptoms(int patientId) throws SQLException {
     return symptomsDao.findOpenByPatient(patientId);
 }
 public int addSymptoms(Symptoms s) throws SQLException {
     return symptomsDao.insert(s);
 }
 public int resolveSymptoms(int symptomId, LocalDateTime end) throws SQLException {
     return symptomsDao.resolve(symptomId, end);
 }
 public List<Prescription> loadPrescriptions(int patientId) throws SQLException {
     return prescriptionDao.findByPatient(patientId);
 }
 public int addIntake(String type, double doses, String unit, LocalDateTime when,
                      int patientId, int doctorId, String drug) throws SQLException {
     return intakeDao.insert(type, doses, unit, when, patientId, doctorId, drug);
 }
}

