package model;

public class Patient extends User{
	
	private int patientId;
	
	public Patient(String user, String pw,int pazienteId, int medicoId) {
		super(user,pw,medicoId);
		this.patientId = pazienteId;
	}
	
	public int getPatientId() {return this.patientId;}
	
}
