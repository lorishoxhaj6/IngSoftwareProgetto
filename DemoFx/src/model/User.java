package model;

public abstract class User {
	protected String username;
	protected String pw;
	protected int id;
	protected int doctorId;
	protected Role role;
	
	public User(String username,String pw, int id, int medicoId) {
		this.username = username;
		this.pw = pw;
		this.id = id;
		this.doctorId = medicoId;
	}
	
	public String getUsername() {
		return this.username;
	}
	

	public String getPassword() {
		return this.pw;
	}
	
	public int getId() {
		return this.id;
	}
	
	//paziente: usa per sapere a che medico è associato
	//dottore: usa per accedere al suo campo medicoId
	public int getMedicoId() { 
		return this.doctorId;
	}
}
