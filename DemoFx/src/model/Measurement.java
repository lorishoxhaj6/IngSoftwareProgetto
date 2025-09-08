package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Measurement {
	private int id;
	private int patientId;
	private String moment;
	private LocalDateTime dateTime;
	private double value;
	
	// Formatter per UI
    private static final DateTimeFormatter OUT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ITALY);

	
	public Measurement(int id, int patientId, String moment, LocalDateTime date, double value) {
		this.id = id;
		this.patientId = patientId;
		this.moment = moment;
		this.dateTime = date;
		this.value = value;
	}
	
    // Getter formattato per la TableView
    public String getDateTimeFormatted() {return (dateTime == null) ? "" : dateTime.format(OUT_FMT);}
	public int getId() { return this.id; }
	public int getPatientId() { return this.patientId; }
	public String getMoment() { return this.moment; }
	public LocalDateTime getDateTime() { return dateTime;}
	public double getValue() { return this.value;}
	public void setMoment(String moment) {this.moment = moment;}
	public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}
	public void setValue(double value) {this.value = value;}
	public void setId(int id) {this.id = id;};
	

}
