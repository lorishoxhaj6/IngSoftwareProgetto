package model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.*;
import java.util.concurrent.*;
// prova codice chatGpt

public class ResetTask {
    public static void scheduleDailyReset() {
    	
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // ora corrente
        LocalDateTime now = LocalDateTime.now();

        // prossima mezzanotte
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long initialDelay = Duration.between(now, nextMidnight).toMillis();

        scheduler.scheduleAtFixedRate(() -> {
            resetPrescriptionsInDB();
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private static void resetPrescriptionsInDB() {
        String sql = "UPDATE prescriptions SET taken = ?"; // resetta tutte le prescription
        try (Connection con = DatabaseUtil.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "No");
        	ps.executeUpdate();
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

