package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DoctorDashboardController {

    @FXML
    private TextField allergyField;

    @FXML
    private TextField amount;

    @FXML
    private LineChart<?, ?> bloodSugarGraph;

    @FXML
    private TextField comorbitField;

    @FXML
    private Button delTherapyBut;

    @FXML
    private ListView<?> historyView;

    @FXML
    private TextArea infoPatients;

    @FXML
    private ListView<?> mailBox;

    @FXML
    private TextField medicineField;

    @FXML
    private Button modifyTherapyBut;

    @FXML
    private TextField numberOfIntakes;

    @FXML
    private TextArea otherIndication;

    @FXML
    private TextField pathologiesField;

    @FXML
    private ListView<?> patientsListView;

    @FXML
    private TextField riskField;

    @FXML
    private Button saveButton;

    @FXML
    private ListView<?> symptomsMedicinesView;

    @FXML
    private TableView<?> tableTherapyView;

    @FXML
    private Button visualizeButton;

    @FXML
    void deleteTherapy(ActionEvent event) {

    }

    @FXML
    void insertInMedicalHistory(ActionEvent event) {

    }

    @FXML
    void insertTherapy(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {

    }

    @FXML
    void modifyTherapy(ActionEvent event) {

    }

    @FXML
    void removeFromMedicalHistory(ActionEvent event) {

    }

    @FXML
    void visualize(ActionEvent event) {

    }

}
