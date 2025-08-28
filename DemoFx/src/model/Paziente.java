package model;

public class Paziente extends User{
	
	private int pazienteId;
	
	public Paziente(String user, String pw, int medicoId,int pazienteId) {
		super(user,pw,medicoId);
		this.pazienteId = pazienteId;
		super.role = Role.Paziente;
	}
	
}
