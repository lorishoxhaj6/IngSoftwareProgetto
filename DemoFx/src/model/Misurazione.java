package model;

import java.time.LocalDate;

public class Misurazione {
	private int id;
	private int patientId;
	private String moment;
	private LocalDate date;
	
	public Misurazione(int id, int patientId, String moment, LocalDate date) {
		this.id = id;
		this.patientId = patientId;
		this.moment = moment;
		this.date = date;
	}
	
	public int getId() { return this.id; }
	public int getPatientId() { return this.patientId; }
	public String getMoment() { return this.moment; }
	public LocalDate getdate() { return this.date; }
}
