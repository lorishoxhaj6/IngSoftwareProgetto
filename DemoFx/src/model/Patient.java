package model;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class Patient extends User{
	
	private int patientId;
	private String name;
	private String surname;
	
	public Patient(String user, String pw,int pazienteId, int medicoId, String name, String surname) {
		super(user,pw,medicoId);
		System.out.println("pazienteId: "+pazienteId);
		this.patientId = pazienteId;
		this.name = name;
		this.surname = surname;
	}
	
	public int getPatientId() {return this.patientId;}
	public String toString() {return String.format("%s %s", name, surname);}
	public List<Measurement> getMeasurementBloodSugar(Patient p) throws SQLException{
		
		String sql = "SELECT id,dateTime,moment,value FROM measurements WHERE patientId = ?";
		System.out.println("nome del paziente selezionato: "+this.username);
		System.out.println("id del paziente selezionato: "+this.patientId);
		System.out.println("pw del paziente selezionato: "+this.getPassword());
		System.out.println("surname del paziente selezionato: "+this.surname);
		ObservableList<Measurement> obsList = DatabaseUtil.queryList(sql, ps -> {
			try {ps.setInt(1, this.getPatientId());} catch (SQLException e) {e.printStackTrace();}
		}, rs -> {
			String raw = rs.getString("dateTime");
			LocalDateTime date = LocalDateTime.parse(raw,DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			return new Measurement(rs.getInt("id"),this.getPatientId(),rs.getString("moment"),date,rs.getDouble("value"));
		});
		
		List<Measurement> l = new ArrayList<>(obsList);
		
		return l;
	}
	
}
