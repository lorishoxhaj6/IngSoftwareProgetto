package model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User{
	
	//private List<Patient> pazienti = new ArrayList<>();
	private String email;
	
	public Doctor(String user, String pw,int id, int medicoId, /*Paziente paziente*/String email) {
		super(user,pw,id, medicoId);
		this.email = email;
		//pazineti.add(paziente);
	}
	
	/*public Doctor(String user, String pw,int id, int medicoId, List<Patient> paziente) {
		super(user,pw,id,medicoId);
		this.pazienti.addAll(paziente);
	}*/
	
	
	
	
}
