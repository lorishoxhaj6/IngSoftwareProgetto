package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.jdbc.JdbcPrescriptionDao;

public class ResetTask {

    public static void checkAndResetIfNeeded() {
        String today = LocalDate.now().toString();
        String lastReset = "";

        String selectSql = "SELECT date FROM lastPrescriptionReset LIMIT 1";
        String updateSql = "UPDATE lastPrescriptionReset SET date = ?"; // aggiorna data reset
        String resetSql = "UPDATE prescriptions SET taken = ?";
        int rows = 0;
        
        try (Connection con = DatabaseUtil.connect();
             PreparedStatement psSelect = con.prepareStatement(selectSql);) {
        	try(ResultSet rs = psSelect.executeQuery()){
        		if (rs.next()) {
                    lastReset = rs.getString("date");
                }
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println(lastReset);
        if (!today.equals(lastReset)) {
        	rows = DatabaseUtil.executeUpdate(resetSql, ps ->{
        		ps.setString(1, "No");
        	});
        	if(rows > 0) {
        	}
        }
        if (!today.equals(lastReset)) { // aggiorno la data dell'ultimo reset
        	rows = DatabaseUtil.executeUpdate(updateSql, ps ->{
        		ps.setString(1, today);
        	});
        	if(rows > 0) {
        		
        	}
        }

    }
}
