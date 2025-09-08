package model;

import java.time.LocalDateTime;

public class Intake {
    // campi
    private String type;
    private double doses;
    private String mU; // measurementUnit
    private LocalDateTime dateTime;
    private int patientId;
    private int doctorId;
    private int intakeId;
    private String drug;


    // costruttore completo
    public Intake(int intakeId,String type, double doses, String mU, LocalDateTime dateTime,
                  int patientId, int doctorId, String drug) {
        this.intakeId = intakeId;
    	this.type = type;
        this.doses = doses;
        this.mU = mU;
        this.dateTime = dateTime;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.drug = drug;
    }

    // getter e setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public int getId() {
    	return this.intakeId;
    }
    
    public void setId(int id) {
    	this.intakeId = id;
    }
    public double getDoses() {
        return doses;
    }

    public void setDoses(double doses) {
        this.doses = doses;
    }

    public String getmU() {
        return mU;
    }

    public void setmU(String mU) {
        this.mU = mU;
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

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDrug() {
        return drug;
    }

    public void setDrug(String drug) {
        this.drug = drug;
    }
}
