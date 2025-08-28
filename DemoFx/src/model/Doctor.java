package model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User{
	
	private List<Paziente> pazienti = new ArrayList<>();
	
	public Doctor(String user, String pw, int medicoId, Paziente paziente) {
		super(user,pw,medicoId);
		this.pazienti.add(paziente);
		super.role = Role.Medico;
	}
	
	public Doctor(String user, String pw, int medicoId, Paziente... paziente) {
		super(user,pw,medicoId);
		for(Paziente p:paziente) {
			this.pazienti.add(p);
		}
		super.role = Role.Medico;
	}
	
	
	
	
}
