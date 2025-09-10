package facade;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import dao.IntakeDao;
import dao.MeasurementDao;
import dao.PatientDao;
import dao.PrescriptionDao;
import dao.SymptomDao;
import model.Doctor;
import model.Intake;
import model.Measurement;
import model.Patient;
import model.Prescription;
import model.Symptoms;

/**
 * Facade per le operazioni di clinica: nasconde i dettagli dei DAO
 * e offre un'API semplice a controller e UI.
 */
public class ClinicFacade {
	
 private final PatientDao patientDao; 
 private final MeasurementDao measurementDao;
 private final SymptomDao symptomsDao;
 private final PrescriptionDao prescriptionDao;
 private final IntakeDao intakeDao;

 public ClinicFacade(PatientDao p, MeasurementDao m, SymptomDao s, PrescriptionDao pr, IntakeDao i) {
     this.patientDao = p; this.measurementDao = m; this.symptomsDao = s; this.prescriptionDao = pr; this.intakeDao = i;
 }

 /**
  * Restituisce le informazioni del dottore con l'identificativo specificato.
  *
  * @param doctorId identificativo del dottore
  * @return istanza di {@link Doctor}
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public Doctor loadDoctorInfo(int doctorId) throws SQLException {
     return patientDao.findDoctorById(doctorId);
 }
/**
 * Resitituisce la lista di tutti i pazineti nel db
 * @return List<Patient>
 * @throws SQLException
 */
 public List<Patient> loadAllPatients() throws SQLException {
     return patientDao.findAll();
 }
 /**
  * Restituisce le misurazioni del paziente con id indicato.
  *
  * @param patientId identificativo del paziente
  * @return lista di {@link Measurement} del paziente (lista vuota se nessuna)
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public List<Measurement> loadMeasurements(int patientId) throws SQLException {
     return measurementDao.findByPatient(patientId);
 }

 /**
  * Aggiunge una nuova misurazione.
  *
  * @param m misurazione da inserire
  * @return id generato per la misurazione inserita
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public int addMeasurement(Measurement m) throws SQLException {
     return measurementDao.insert(m);
 }

 /**
  * Elimina una misurazione passando il suo identificativo.
  *
  * @param id identificativo della misurazione
  * @return numero di righe eliminate (0 se non esiste)
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public int deleteMeasurement(int id) throws SQLException {
     return measurementDao.deleteById(id);
 }

 /**
  * Restituisce i sintomi ancora aperti (non risolti) del paziente indicato.
  *
  * @param patientId identificativo del paziente
  * @return lista di sintomi aperti (lista vuota se nessuno)
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public List<Symptoms> loadOpenSymptoms(int patientId) throws SQLException {
     return symptomsDao.findOpenByPatient(patientId);
 }

 /**
  * Registra un nuovo sintomo.
  *
  * @param s sintomo da inserire
  * @return id generato per il sintomo inserito
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public int addSymptoms(Symptoms s) throws SQLException {
     return symptomsDao.insert(s);
 }

 /**
  * Segna come risolto un sintomo, registrando la data/ora di risoluzione.
  *
  * @param symptomId identificativo del sintomo
  * @param end data e ora di risoluzione
  * @return numero di righe aggiornate (0 se l'id non esiste)
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public int resolveSymptoms(int symptomId, LocalDateTime end) throws SQLException {
     return symptomsDao.resolve(symptomId, end);
 }

 /**
  * Restituisce tutte le terapie (prescrizioni) associate al paziente indicato.
  *
  * @param patientId identificativo del paziente
  * @return lista di {@link Prescription} (lista vuota se nessuna)
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public List<Prescription> loadPrescriptions(int patientId) throws SQLException {
     return prescriptionDao.findByPatient(patientId);
 }

 /**
  * Registra un'assunzione di farmaco da parte del paziente.
  *
  * @param type tipo di assunzione (es. "orale")
  * @param doses quantità assunta (numero)
  * @param unit unità della dose (es. "mg", "ml")
  * @param when data e ora dell'assunzione
  * @param patientId identificativo del paziente
  * @param doctorId identificativo del medico che ha prescritto/validato
  * @param drug nome del farmaco
  * @return id generato per l'assunzione inserita
  * @throws SQLException se si verifica un errore di accesso al database
  */
 public int addIntake(Intake t) throws SQLException {
     return intakeDao.insert(t);
 }
 /**
  * Restituisce le informazioni legate alla storia clinica del paziente
  * @param patientId 
  * @return String (stringa contenente info)
  * @throws SQLException
  */
 public String getInformation(int patientId) throws SQLException {
	 return patientDao.getInfo(patientId);
 }
 /**
  * aggiorna le informazioni legate all'id del paziente, possono farlo 
  * tutti i medici, e la modifica di un medico aggiorna le viste di tutti i medici
  * @param patientId
  * @param newText
  * @return numero di righe aggiornate (0 se l'id non esiste)
  * @throws SQLException
  */
 public int updateInformation(int patientId,String newText) throws SQLException {
	 return patientDao.updateInfo(patientId,newText);
 }
/**
 * Elimina la terapia tramite id
 * @param idPrescription
 * @return numero di righe eliminate (0 se l'id non esiste)
 * @throws SQLException
 */
 public int deletePrescription(int idPrescription) throws SQLException {
	return prescriptionDao.deletePrescription(idPrescription);
 }
 
 public int updatePrescriptionPreso(String preso, int idPrescription) throws SQLException {
		return prescriptionDao.updatePreso(preso, idPrescription);
	 }
/**
 * Inserimento della terapia 
 * @param p (terapia presa in input)
 * @return id generato per la terapia inserita
 * @throws SQLException
 */
 public int insertPrescription(Prescription p) throws SQLException {
	 return prescriptionDao.insert(p);
 }
}
