package com.example.medical.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cabinet_id")
    private Cabinet cabinet;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String studyArea;
    private boolean isQuota;
    private String referringSource;
    private String note;

    public Appointment() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cabinet getCabinet() { return cabinet; }
    public void setCabinet(Cabinet cabinet) { this.cabinet = cabinet; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStudyArea() { return studyArea; }
    public void setStudyArea(String studyArea) { this.studyArea = studyArea; }
    public boolean isQuota() { return isQuota; }
    public void setQuota(boolean quota) { isQuota = quota; }
    public String getReferringSource() { return referringSource; }
    public void setReferringSource(String referringSource) { this.referringSource = referringSource; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }


    @Enumerated(EnumType.STRING) // Чтобы в базе сохранялось слово "SCHEDULED", а не число
    private AppointmentStatus status = AppointmentStatus.SCHEDULED; // Значение по умолчанию

    // Геттер и сеттер
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}