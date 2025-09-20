package facade;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import dao.jdbc.JdbcIntakeDao;
import model.AppUtils;
import model.Doctor;
import model.Measurement;
import model.Patient;
import model.Prescription;

public class AlertService {
	private ClinicFacade clinic;
	private JdbcIntakeDao IntakeDao;
	
	public AlertService(ClinicFacade clinic, JdbcIntakeDao d) {
		this.clinic = clinic;
		this.IntakeDao = d;
	}
	
	public void checkPendingPrescriptions(int patientId) {
        try {
            List<Prescription> list = clinic.loadPrescriptions(patientId);
            boolean hasPending = list.stream().anyMatch(p -> !"Yes".equals(p.getTaken()));
            if (hasPending) {
                AppUtils.showError("Attenzione", "Ci sono farmaci non assunti",
                                   "Alcune terapie non risultano ancora completate!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public void checkHighMeasurements(Patient p) {
        try {
            List<Measurement> list = clinic.loadMeasurements(p.getPatientId());
            int grave = 0;
            LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
            
            // filtro le misurazioni dell'ultima settimana
            List<Measurement> lastWeekMeasurements = list.stream()
                .filter(m -> m.getDateTime().isAfter(oneWeekAgo)) // getDateTime() ritorna LocalDateTime
                .collect(Collectors.toList());
            
            
            for(Measurement m : lastWeekMeasurements) {
            	if(m.getValue() > 180 && grave < 2)
            		grave = 2;
            	if(m.getValue() > 130 && grave < 1)
            		grave = 1;
            }
            
            
            if (grave == 1) {
                AppUtils.showError("Attenzione", "misurazione glicemiche alte ultimi 7gg",
                                   "il paziente ha alcune misurazioni glicemiche oltre 130 negli ultimi 7gg");
            }
            if (grave == 2) {
                AppUtils.showError("Attenzione", "misurazione glicemiche molto alte",
                                   "il paziente ha alcune misurazioni glicemiche oltre 180 negli ultimi 7gg");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	

	
	public StringBuilder checkLastFreeDaysIntake(Doctor user) throws SQLException {
		StringBuilder error = new StringBuilder();
        for (Patient p : user.getPatients()) {
            try {
                if (IntakeDao.fetchLastThreeDaysIntakes(p.getPatientId())) {
                    error.append("• Il paziente ")
                         .append(p.getUsername())
                         .append(" non ha assunto i farmaci negli ultimi 3 giorni.\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return error;
        
    }
}
	
	
	
	

