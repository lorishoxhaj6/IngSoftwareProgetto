package model;

public class Patient extends User{
	
	private int patientId;
	
	public Patient(String user, String pw, int medicoId,int pazienteId) {
		super(user,pw,medicoId);
		this.patientId = pazienteId;
		super.role = Role.Paziente;
	}
	
}
