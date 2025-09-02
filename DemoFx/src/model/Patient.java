package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends User{
	
	private int patientId;
	private String name;
	private String surname;
	
	public Patient(String user, String pw,int pazienteId, int medicoId, String name, String surname) {
		super(user,pw,medicoId);
		this.patientId = pazienteId;
		this.name = name;
		this.surname = surname;
	}
	
	public int getPatientId() {return this.patientId;}
	public String toString() {
		return String.format("%s %s", name, surname);
	}
	
}
