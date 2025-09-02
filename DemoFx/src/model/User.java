package model;

public abstract class User {
	protected String username;
	protected String pw;
	protected int doctorId;

	
	public User(String username,String pw, int medicoId) {
		this.username = username;
		this.pw = pw;
		this.doctorId = medicoId;
	}
	
	public String getUsername() {return this.username;}
	

	public String getPassword() {return this.pw;}
	
	//paziente: usa per sapere a che medico è associato
	//dottore: usa per accedere al suo campo medicoId
	public int getMedicoId() {return this.doctorId;}
}
