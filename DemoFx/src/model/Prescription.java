package model;

public class Prescription {
	
    private int idPrescription; 
    private String doses; // quantità del farmaco per assunzione
    private int quantity; // numero di volte che bisogna assumere il farmaco
    private String indications;
    private int patientId;
    private int doctorId;
    private String drug;
	
    public Prescription(int idPres, String doses, int quantity, String indications, 
                         int patientId, int doctorId,  String drug) {
        this.idPrescription = idPres;
        this.doses = doses;
        this.quantity = quantity;
        this.indications = indications;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.drug = drug;
    }

    public int getIdPrescription() {
        return idPrescription;
    }

    public String getDoses() {
        return doses;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getIndications() {
        return indications;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDrug() {
        return drug;
    }
}
