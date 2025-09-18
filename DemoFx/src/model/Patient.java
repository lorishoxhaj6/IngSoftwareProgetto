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
	private int lastModifiedBy;
	
	public Patient(String user, String pw,int pazienteId, int medicoId, String name, String surname,int lastmodifiedby) {
		super(user,pw,medicoId);
		this.patientId = pazienteId;
		this.name = name;
		this.surname = surname;
		this.lastModifiedBy = lastmodifiedby;
	}
	
	public int getPatientId() {return this.patientId;}
	public int getLastModifiedBy() { return lastModifiedBy; }
	public void setLastModifiedBy(int lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
	public String toString() {return String.format("%s %s", name, surname);}
	public List<Measurement> getMeasurementBloodSugar(Patient p) throws SQLException{
		
		String sql = "SELECT id,dateTime,moment,value FROM measurements WHERE patientId = ?";
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
