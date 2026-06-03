package com.example.medical.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CheckoutForm {

    @NotBlank(message = "ФИО пациента обязательно для заполнения")
    private String fullName;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    private String paymentType; // бесплатно / платно
    private String referringDoctor;
    private String notes;

    private String studyArea;

    public String getStudyArea() {
        return studyArea;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStudyArea(String studyArea) {
        this.studyArea = studyArea;
    }


    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public String getReferringDoctor() { return referringDoctor; }
    public void setReferringDoctor(String referringDoctor) { this.referringDoctor = referringDoctor; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}