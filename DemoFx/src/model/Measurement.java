package model;

import java.time.LocalDate;

public class Measurement {
	private int id;
	private int patientId;
	private String moment;
	private LocalDate date;
	private double value;
	
	public Measurement(int id, int patientId, String moment, LocalDate date, double value) {
		this.id = id;
		this.patientId = patientId;
		this.moment = moment;
		this.date = date;
		this.value = value;
	}
	
	public int getId() { return this.id; }
	public int getPatientId() { return this.patientId; }
	public String getMoment() { return this.moment; }
	public LocalDate getDate() { return this.date; }
	public double getValue() { return this.value;}
	
}
