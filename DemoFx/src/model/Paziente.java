package model;

public class Paziente {
	private String user;
	private String pw;
	private String medico;
	
	public Paziente(String user, String pw, String medico) {
		this.user= user;
		this.pw = pw;
		this.medico = medico;
	}
	
	public String getUsername() {
		return this.user;
	}
	public String getPassword() {
		return this.pw;
	}
	public String getMedico() {
		return this.medico;
	}
}
