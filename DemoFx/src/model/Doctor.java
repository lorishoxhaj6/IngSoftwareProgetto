package model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User{
	
	private List<Patient> pazienti = new ArrayList<>();
	
	public Doctor(String user, String pw, int medicoId, Patient paziente) {
		super(user,pw,medicoId);
		this.pazienti.add(paziente);
		super.role = Role.Medico;
	}
	
	public Doctor(String user, String pw, int medicoId, List<Patient> paziente) {
		super(user,pw,medicoId);
		this.pazienti.addAll(paziente);
		
		super.role = Role.Medico;
	}
	
	
	
	
}
