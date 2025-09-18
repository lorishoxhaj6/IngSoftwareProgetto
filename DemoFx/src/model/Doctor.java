package model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User{
	
	private List<Patient> pazienti = new ArrayList<>();
	
	private String email;
	
	public Doctor(String user, String pw, int medicoId, Patient paziente, String email) {
		super(user,pw,medicoId);
		this.email = email;
		pazienti.add(paziente);
	}
	
	public Doctor(String user, String pw, int medicoId, List<Patient> paziente,String email) {
		super(user,pw,medicoId);
		this.email = email;
		this.pazienti.addAll(paziente);
	}
	
	
	public List<Patient> getPatients(){return this.pazienti;}
	public void setPatients(List<Patient> l) {this.pazienti = l;};
	public String getEmail() {return this.email;};
	public String toString(){ return this.username; }
	
	
}
