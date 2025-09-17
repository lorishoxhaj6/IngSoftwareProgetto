package model;

import java.time.LocalDateTime;

public class Intake {
    // campi
    private LocalDateTime dateTime;
    private int patientId;
    private int intakeId; // id dell'assunzione



    // costruttore: salva id del paziente e la data in cui ha completato l'assunzione
    public Intake(int intakeId,LocalDateTime dateTime,
                  int patientId) {
        this.intakeId = intakeId;
        this.dateTime = dateTime;
        this.patientId = patientId;
    }

    // getter e setter
    public int getId() {
    	return this.intakeId;
    }
    
    public void setId(int id) {
    	this.intakeId = id;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

}
