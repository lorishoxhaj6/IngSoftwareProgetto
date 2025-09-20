package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;

import dao.jdbc.JdbcPatientDao;
import model.DatabaseUtil;   // usa lo stesso DatabaseUtil del DAO
import model.Patient;

/**
 * Test d'integrazione NON distruttivi:
 * - creano la tabella se serve
 * - inseriscono solo righe con username univoco
 * - rimuovono solo le righe inserite (per username) in @AfterEach
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcPatientDaoTest {

    private final JdbcPatientDao dao = new JdbcPatientDao();

    // terrò qui gli username inseriti in ciascun test per poi cancellarli
    private final List<String> insertedUsernames = new ArrayList<>();

    @BeforeAll
    void ensureSchema() throws Exception {
        try (Connection con = DatabaseUtil.connect()) {
            con.createStatement().executeUpdate(
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
    }

    @BeforeEach
    void resetTracking() {
        insertedUsernames.clear();
    }

    @AfterEach
    void deleteOnlyInsertedRows() throws Exception {
        if (insertedUsernames.isEmpty()) return;

        // elimina solo gli utenti che abbiamo inserito nel test
        String placeholders = insertedUsernames.stream().map(u -> "?").collect(Collectors.joining(","));
        String sql = "DELETE FROM patients WHERE username IN (" + placeholders + ")";
        try (Connection con = DatabaseUtil.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            for (String u : insertedUsernames) ps.setString(i++, u);
            ps.executeUpdate();
        }
    }

    // Helper per generare username univoci (evita collisioni con dati reali)
    private static String uniq(String base) {
        return base + "_test_" + Instant.now().toEpochMilli() + "_" + Math.round(Math.random() * 100000);
    }

    private void insertPatient(String username, String password, int doctorId,
                               String name, String surname, String informations, Integer lastModifiedBy) throws SQLException {
        String sql = "INSERT INTO patients (username, password, doctor_id, name, surname, informations, lastModifiedBy) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DatabaseUtil.connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, username);
            ps.setString(idx++, password);
            ps.setInt(idx++, doctorId);
            ps.setString(idx++, name);
            ps.setString(idx++, surname);
            ps.setString(idx++, informations);
            if (lastModifiedBy == null) {
                ps.setNull(idx++, java.sql.Types.INTEGER);
            } else {
                ps.setInt(idx++, lastModifiedBy);
            }
            ps.executeUpdate();
        }
        insertedUsernames.add(username);
    }

    @Test
    void testFindAll_containsInsertedPatients() throws Exception {
        // Arrange: inserisco 3 pazienti nuovi (ID auto-increment: non faccio assunzioni sugli ID)
        String u1 = uniq("alice");
        String u2 = uniq("bob");
        String u3 = uniq("carl");
        insertPatient(u1, "pwdA", 101, "Alice", "Rossi", null, 999);
        insertPatient(u2, "pwdB", 101, "Bob",   "Bianchi", null, 999);
        insertPatient(u3, "pwdC", 202, "Carl",  "Verdi",   null, 888);

        // Act
        List<Patient> all = dao.findAll();

        // Assert (non controllo la size totale: potrebbero esserci altre righe preesistenti)
        assertNotNull(all, "La lista non deve essere null");
        Set<String> usernames = all.stream().map(Patient::getUsername).collect(Collectors.toSet());
        assertTrue(usernames.containsAll(Set.of(u1, u2, u3)),
                "findAll() dovrebbe contenere gli username inseriti nel test");
        // opzionale: controlla che i PatientId siano > 0 per i nuovi (quindi creati davvero)
        for (Patient p : all) {
            if (Set.of(u1, u2, u3).contains(p.getUsername())) {
                assertTrue(p.getPatientId() > 0, "patientId deve essere valorizzato per " + p.getUsername());
            }
        }
    }

    @Test
    void testFindAllPatient_containsOnlyOurInsertedForThoseDoctors() throws Exception {
        // Arrange: inserisco 2 pazienti per doctor 101 e 1 per 202 (non elimino nulla di preesistente)
        String u1 = uniq("alice");
        String u2 = uniq("bob");
        String u3 = uniq("carl");
        insertPatient(u1, "pwdA", 101, "Alice", "Rossi",   null, 999);
        insertPatient(u2, "pwdB", 101, "Bob",   "Bianchi", null, 999);
        insertPatient(u3, "pwdC", 202, "Carl",  "Verdi",   null, 888);

        // Act
        List<Patient> d101 = dao.findAllPatient(101);
        List<Patient> d202 = dao.findAllPatient(202);

        // Assert: per 101 e 202 ci devono essere almeno i pazienti appena inseriti
        assertNotNull(d101);
        assertNotNull(d202);

        Set<String> u101 = d101.stream().map(Patient::getUsername).collect(Collectors.toSet());
        Set<String> u202 = d202.stream().map(Patient::getUsername).collect(Collectors.toSet());

        assertTrue(u101.containsAll(Set.of(u1, u2)),
                "findAllPatient(101) dovrebbe contenere almeno gli inseriti: " + u1 + ", " + u2);
        assertTrue(u202.contains(u3),
                "findAllPatient(202) dovrebbe contenere almeno l'inserito: " + u3);

        // opzionale: verifica che i pazienti trovati abbiano un patientId > 0
        for (Patient p : d101) {
            if (Set.of(u1, u2).contains(p.getUsername())) {
                assertTrue(p.getPatientId() > 0, "patientId deve essere valorizzato per " + p.getUsername());
            }
        }
        for (Patient p : d202) {
            if (u3.equals(p.getUsername())) {
                assertTrue(p.getPatientId() > 0, "patientId deve essere valorizzato per " + p.getUsername());
            }
        }
    }
}
