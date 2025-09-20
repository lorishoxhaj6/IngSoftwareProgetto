package facade;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import dao.DoctorDao;
import dao.IntakeDao;
import dao.LastPrescriptionDao;
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
 * Facade per le operazioni di clinica: nasconde i dettagli dei DAO e offre
 * un'API semplice a controller e UI.
 */
public class ClinicFacade {

	private final PatientDao patientDao;
	private final MeasurementDao measurementDao;
	private final SymptomDao symptomsDao;
	private final PrescriptionDao prescriptionDao;
	private final IntakeDao intakeDao;
	private final LastPrescriptionDao lastPrescriptionDao;
	private final DoctorDao doctorDao;

	public ClinicFacade(PatientDao p, MeasurementDao m, SymptomDao s, PrescriptionDao pr, IntakeDao i,
			LastPrescriptionDao l, DoctorDao d) {
		this.patientDao = p;
		this.measurementDao = m;
		this.symptomsDao = s;
		this.prescriptionDao = pr;
		this.intakeDao = i;
		this.lastPrescriptionDao = l;
		this.doctorDao = d;
	}

	
	
	/**
	 * Resitituisce la lista di tutti i pazineti nel db
	 * 
	 * @return List<Patient>
	 * @throws SQLException
	 */
	public List<Patient> loadAllPatients() throws SQLException {
		return patientDao.findAll();
	}
	
	/**
	 * 
	 * @param doctorId
	 * @return Lista dei pazienti assocciati al dottore con id = ?
	 * @throws SQLException
	 */
	public List<Patient> findAllPatientsByDoctorId(int doctorId) throws SQLException {
		return patientDao.findAllPatient(doctorId);
	}
	
	/**
	 * Restituisce un oggetto Doctor tramite id per accedere alle sue info.
	 *
	 * @param doctorId identificativo del dottore
	 * @return istanza di {@link Doctor}
	 * @throws SQLException se si verifica un errore di accesso al database
	 */
	public Doctor findDoctorById(int doctorId) throws SQLException {
		Doctor d = doctorDao.findDoctorById(doctorId);
		d.setPatients(patientDao.findAllPatient(doctorId));
		return d;
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
	 * @param end       data e ora di risoluzione
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
	 * 
	 * @param doses     quantità assunta (numero)
	 * @param unit      unità della dose (es. "mg", "ml")
	 * @param when      data e ora dell'assunzione
	 * @param patientId identificativo del paziente
	 * @param doctorId  identificativo del medico che ha prescritto/validato
	 * @param drug      nome del farmaco
	 * @return id generato per l'assunzione inserita
	 * @throws SQLException se si verifica un errore di accesso al database
	 */
	public int addIntake(Intake t) throws SQLException {
		return intakeDao.insert(t);
	}

	/**
	 * Restituisce le informazioni legate alla storia clinica del paziente
	 * 
	 * @param patientId
	 * @return String (stringa contenente info)
	 * @throws SQLException
	 */
	public String getInformation(int patientId) throws SQLException {
		return patientDao.getInfo(patientId);
	}

	/**
	 * aggiorna le informazioni legate all'id del paziente, possono farlo tutti i
	 * medici, e la modifica di un medico aggiorna le viste di tutti i medici
	 * 
	 * @param patientId
	 * @param newText
	 * @param docId
	 * @return numero di righe aggiornate (0 se l'id non esiste)
	 * @throws SQLException
	 */
	public int updateInformation(int patientId, String newText, int medicoId) throws SQLException {
		return patientDao.updateInfo(patientId, newText, medicoId);
	}

	/**
	 * Elimina la terapia tramite id
	 * 
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
	 * 
	 * @param p (terapia presa in input)
	 * @return id generato per la terapia inserita
	 * @throws SQLException
	 */
	public int insertPrescription(Prescription p) throws SQLException {
		return prescriptionDao.insert(p);
	}

	/**
	 * verifica e resetta se necessario le prescrizioni nel caso in cui siano state
	 * prese
	 * 
	 * @throws SQLException
	 */
	public void checkAndResetIfNeeded(Patient pTrigger) throws SQLException {
	    final String today = java.time.LocalDate.now().toString();

	    try (var con = model.DatabaseUtil.connect()) {
	        con.setAutoCommit(false);
	        try {
	            String last = "";
	            try (var ps = con.prepareStatement("SELECT date FROM lastPrescriptionReset LIMIT 1");
	                 var rs = ps.executeQuery()) {
	                if (rs.next()) last = rs.getString(1);
	            }

	            if (!today.equals(last)) {
	                // === 1) carica TUTTI i pazienti ===
	                var allPatients = patientDao.findAll(); // hai già il DAO e il metodo nella Facade

	                // === 2) per ciascun paziente: check mancata + reset taken ===
	                try (var psSel = con.prepareStatement("SELECT taken FROM prescriptions WHERE patientId = ?");
	                     var psIns = con.prepareStatement("INSERT INTO patientIntake (dateTime, patientId) VALUES (?, ?)");
	                     var psUpd = con.prepareStatement("UPDATE prescriptions SET taken='No' WHERE patientId=?")) {

	                    final var endOfYesterday = java.time.LocalDate.now()
	                            .minusDays(1).atTime(23, 59, 59).toString();

	                    for (var pt : allPatients) {
	                        int total = 0, notOk = 0;

	                        psSel.setInt(1, pt.getPatientId());
	                        try (var rs = psSel.executeQuery()) {
	                            while (rs.next()) {
	                                total++;
	                                if (!"Yes".equals(rs.getString("taken"))) notOk++;
	                            }
	                        }

	                        boolean allTaken = (total > 0 && notOk == 0);

	                        if (!allTaken) {
	                            psIns.setString(1, endOfYesterday);
	                            psIns.setInt(2, pt.getPatientId());
	                            psIns.executeUpdate();
	                        }

	                        psUpd.setInt(1, pt.getPatientId());
	                        psUpd.executeUpdate();
	                    }
	                }

	                // === 3) aggiorna UNA VOLTA la data globale ===
	                int rows;
	                try (var ps = con.prepareStatement("UPDATE lastPrescriptionReset SET date = ?")) {
	                    ps.setString(1, today);
	                    rows = ps.executeUpdate();
	                }
	                if (rows == 0) {
	                    try (var ps = con.prepareStatement("INSERT INTO lastPrescriptionReset(date) VALUES (?)")) {
	                        ps.setString(1, today);
	                        ps.executeUpdate();
	                    }
	                }
	            }

	            con.commit();
	        } catch (SQLException ex) {
	            con.rollback();
	            throw ex;
	        } finally {
	            con.setAutoCommit(true);
	        }
	    }
	}


}
