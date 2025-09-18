package model;

public class Prescription {
	
    private int idPrescription; 
    private Double doses; // quantità del farmaco per assunzione
    private String measurementUnit; // unità di misura del farmaco
    private int quantity; // numero di volte che bisogna assumere il farmaco
    private String indications;
    private int patientId;
    private int doctorId;
    private String drug;
    private String taken;
    private String lastModifiedBy;
	
    public Prescription(int idPres, Double doses, String unit,int quantity, String indications, 
                         int patientId, int doctorId,  String drug, String taken, String lastmodifiedby) {
        this.idPrescription = idPres;
        this.doses = doses;
        this.measurementUnit = unit;
        this.quantity = quantity;
        this.indications = indications;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.drug = drug;
        this.taken = taken;
        this.lastModifiedBy = lastmodifiedby;
    }
    
    
    public String getLastModifiedBy(){return lastModifiedBy;};
    public void setLastModifiedBy(String lastmodified) {lastModifiedBy = lastmodified;};
    public int getIdPrescription() {return idPrescription;}
    public Double getDoses() {return doses;}
    public String getMeasurementUnit() {return measurementUnit;}
    public int getQuantity() {return quantity;}
    public String getIndications() {return indications;}
    public int getPatientId() {return patientId;}
    public int getDoctorId() {return doctorId;}
    public String getDrug() {return drug;}
    public void setId(int id) { this.idPrescription = id;};
    public void setDoses(Double doses) {this.doses = doses;}
    public void setMeasurementUnit(String m) {this.measurementUnit = m;}
	public void setQuantity(int quantity) {this.quantity = quantity;}
	public void setIndications(String indications) {this.indications = indications;}
	public void setDrug (String drug) { this.drug = drug; }
	public String getTaken() { return this.taken; }
	public void setTaken(String taken) { this.taken = taken; }
	public String toString(){return drug;}
}
