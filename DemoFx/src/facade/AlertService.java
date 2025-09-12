package facade;

import java.sql.SQLException;
import java.util.List;

import model.AppUtils;
import model.Prescription;

public class AlertService {
	private ClinicFacade clinic;
	
	public AlertService(ClinicFacade clinic) {
		this.clinic = clinic;
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
}
