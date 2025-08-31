package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Symptoms {
	private int symptomId;
	private int patientId;
	private int medicoId;
	private String symptoms;
	private LocalDateTime dateTime;
	private String notes;
	
	// Formatter per UI
    private static final DateTimeFormatter OUT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ITALY);

	
	public Symptoms(int symptomId,int medicoId, int patientId, String symptoms, LocalDateTime dateTime, String notes) {
		this.symptomId = symptomId;
		this.medicoId = medicoId;
		this.patientId = patientId;
		this.symptoms = symptoms;
		this.dateTime = dateTime;
		this.notes = notes;	
	}
	
    
    public String getDateTimeFormatted() {return (dateTime == null) ? "" : dateTime.format(OUT_FMT);}
	public int getSymptomId() { return this.symptomId; }
    public int getMedicoId() { return this.medicoId; }
	public int getPatientId() { return this.patientId; }
	public String getSymptoms() { return this.symptoms; }
	public LocalDateTime getDateTime() { return dateTime;}
	public String getNotes() { return this.notes;}
	public String toString() {return String.format("%s | %s | %s",dateTime.format(OUT_FMT),symptoms,notes == null ? "Nessuna nota" : notes);}
	
}
