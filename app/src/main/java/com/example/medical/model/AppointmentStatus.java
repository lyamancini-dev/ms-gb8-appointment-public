package com.example.medical.model;

public enum AppointmentStatus {
    SCHEDULED("Запланировано"),
    COMPLETED("Завершено"),
    CANCELLED("Отменено"),
    NO_SHOW("Не явился");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}