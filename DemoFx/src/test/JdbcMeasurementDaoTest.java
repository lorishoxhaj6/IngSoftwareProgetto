package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.junit.jupiter.api.*;

import dao.jdbc.JdbcMeasurementDao;
import model.DatabaseUtil;
import model.Measurement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcMeasurementDaoTest {

    private final JdbcMeasurementDao dao = new JdbcMeasurementDao();

    // tracking di ciò che inseriamo per ripulire senza toccare dati pre-esistenti
    private Integer insertedPatientId = null;
    private final List<Integer> insertedMeasurementIds = new ArrayList<>();

    @BeforeAll
    void ensureSchema() throws Exception {
        // patients (serve un paziente valido se ci sono FK)
        try (Connection con = DatabaseUtil.connect();
             Statement st = con.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS patients (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username TEXT NOT NULL UNIQUE," +
                "  password TEXT NOT NULL," +
                "  doctor_id INTEGER NOT NULL," +
                "  name TEXT," +
                "  surname TEXT," +
                "  informations TEXT," +
                "  lastModifiedBy INTEGER" +
                ")"
            );
        }
        // measurements
        try (Connection con = DatabaseUtil.connect();
             Statement st = con.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS measurements (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  patientId INTEGER NOT NULL," +
                "  moment TEXT," +
                "  dateTime TEXT NOT NULL," +
                "  value REAL NOT NULL" +
                ")"
            );
        }
    }

    @BeforeEach
    void resetTracking() {
        insertedMeasurementIds.clear();
        insertedPatientId = null;
    }

    @AfterEach
    void cleanupOnlyWhatWeInserted() throws Exception {
        // elimina SOLO le misurazioni inserite da questo test
        for (Integer mid : insertedMeasurementIds) {
            try {
                dao.deleteById(mid);
            } catch (SQLException ignore) {
                // se già cancellata nel test specifico, va bene
            }
        }
        insertedMeasurementIds.clear();

        // elimina SOLO il paziente creato per il test (se presente)
        if (insertedPatientId != null) {
            try (Connection con = DatabaseUtil.connect();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM patients WHERE id = ?")) {
                ps.setInt(1, insertedPatientId);
                ps.executeUpdate();
            } catch (SQLException ignore) {
            } finally {
                insertedPatientId = null;
            }
        }
    }

    // Helpers ---------------------------------------------------------------

    private static String uniq(String base) {
        return base + "_t" + Instant.now().toEpochMilli() + "_" + (int)(Math.random() * 100000);
    }

    private int insertTestPatient() throws SQLException {
        String username = uniq("meas_user");
        String sql = "INSERT INTO patients (username, password, doctor_id, name, surname, informations, lastModifiedBy) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DatabaseUtil.connect();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setString(i++, username);
            ps.setString(i++, "pwd");
            ps.setInt(i++, 99999);        // doctor_id fittizio
            ps.setString(i++, "Nome");
            ps.setString(i++, "Cognome");
            ps.setString(i++, null);
            ps.setNull(i++, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Impossibile ottenere l'id del paziente di test");
            }
        }
    }

    private int insertMeasurementRaw(int patientId, String moment, LocalDateTime dt, double value) throws SQLException {
        String sql = "INSERT INTO measurements (patientId, moment, dateTime, value) VALUES (?,?,?,?)";
        try (Connection con = DatabaseUtil.connect();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId);
            ps.setString(2, moment);
            ps.setString(3, dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setDouble(4, value);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Impossibile ottenere l'id della measurement di test");
            }
        }
    }

    // Tests -----------------------------------------------------------------

    @Test
    void testInsert_and_findByPatient_containsInsertedOnes() throws Exception {
        // Arrange: creo un paziente fittizio e 2 misurazioni tramite il DAO reale
        insertedPatientId = insertTestPatient();

        Measurement m1 = new Measurement(0, insertedPatientId, "before_breakfast",
                LocalDateTime.now().withNano(0), 110.5);
        Measurement m2 = new Measurement(0, insertedPatientId, "after_lunch",
                LocalDateTime.now().plusMinutes(1).withNano(0), 145.0);

        int id1 = dao.insert(m1);
        int id2 = dao.insert(m2);
        insertedMeasurementIds.add(id1);
        insertedMeasurementIds.add(id2);

        // Act
        List<Measurement> out = dao.findByPatient(insertedPatientId);

        // Assert: tra i risultati DEVONO esserci le due appena inserite (per id)
        assertNotNull(out);
        Set<Integer> ids = new HashSet<>();
        for (Measurement m : out) {
            // assumendo i classici getter in Measurement
            ids.add(m.getId());
        }
        assertTrue(ids.contains(id1), "findByPatient() dovrebbe contenere la misura id=" + id1);
        assertTrue(ids.contains(id2), "findByPatient() dovrebbe contenere la misura id=" + id2);
    }

    @Test
    void testDeleteById_removesOnlyTargetRow() throws Exception {
        // Arrange: paziente fittizio con 2 misurazioni create "raw" (per testare esplicitamente deleteById)
        insertedPatientId = insertTestPatient();

        int idA = insertMeasurementRaw(insertedPatientId, "before_dinner",
                LocalDateTime.now().withNano(0), 120.0);
        int idB = insertMeasurementRaw(insertedPatientId, "after_dinner",
                LocalDateTime.now().plusMinutes(1).withNano(0), 155.0);
        insertedMeasurementIds.add(idA);
        insertedMeasurementIds.add(idB);

        // Act: cancello solo idA
        int delCount = dao.deleteById(idA);
        assertEquals(1, delCount, "deleteById deve eliminare esattamente una riga");

        // Assert: findByPatient non deve restituire più idA, ma deve ancora restituire idB
        List<Measurement> out = dao.findByPatient(insertedPatientId);
        Set<Integer> ids = new HashSet<>();
        for (Measurement m : out) ids.add(m.getId());

        assertFalse(ids.contains(idA), "La misura id=" + idA + " non dovrebbe più esistere");
        assertTrue(ids.contains(idB), "La misura id=" + idB + " deve ancora esistere");
    }
}
