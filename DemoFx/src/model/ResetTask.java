package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ResetTask {

    public static void checkAndResetIfNeeded() {
        String today = LocalDate.now().toString();
        String lastReset = "";

        String selectSql = "SELECT date FROM lastPrescriptionReset LIMIT 1";
        String updateSql = "UPDATE lastPrescriptionReset SET date = ?"; // aggiorna data reset

        // apriamo UNA sola connessione per tutto
        try (Connection con = DatabaseUtil.connect();
             PreparedStatement psSelect = con.prepareStatement(selectSql);
             ResultSet rs = psSelect.executeQuery()) {

            if (rs.next()) {
                lastReset = rs.getString("date");
            }

            if (!today.equals(lastReset)) {
                // reset delle prescriptions
                String resetSql = "UPDATE prescriptions SET taken = ?";
                try (PreparedStatement psReset = con.prepareStatement(resetSql)) {
                    psReset.setString(1, "No");
                    psReset.executeUpdate();
                }

                // aggiorniamo la data dell'ultimo reset
                try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                    psUpdate.setString(1, today);
                    psUpdate.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
