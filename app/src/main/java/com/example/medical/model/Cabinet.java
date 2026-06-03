package com.example.medical.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cabinets")
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String roomNumber;
    private Integer defaultSlotMinutes = 15;

    // ИСПРАВЛЕНО: при удалении кабинета все его записи удаляются автоматически
    @OneToMany(mappedBy = "cabinet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    public Cabinet() {
    }

    public Cabinet(String name, String roomNumber) {
        this.name = name;
        this.roomNumber = roomNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public Integer getDefaultSlotMinutes() { return defaultSlotMinutes; }
    public void setDefaultSlotMinutes(Integer defaultSlotMinutes) { this.defaultSlotMinutes = defaultSlotMinutes; }
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
}