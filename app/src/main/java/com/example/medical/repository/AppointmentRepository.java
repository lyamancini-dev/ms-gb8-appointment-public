package com.example.medical.repository;

import com.example.medical.model.Appointment;
import com.example.medical.model.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIsNotNullOrderByStartTimeDesc();

    List<Appointment> findAllByOrderByStartTimeDesc();

    List<Appointment> findByCabinetAndStartTimeBetweenOrderByStartTimeAsc(
            Cabinet cabinet,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
    // Поиск записей по части ФИО пациента
    List<Appointment> findByPatientFullNameContainingIgnoreCase(String name);

    // Проверка существования записи в конкретном кабинете на указанное время
    boolean existsByCabinetAndStartTime(Cabinet cabinet, LocalDateTime startTime);

    void deleteByCabinetAndStartTimeBetween(Cabinet cabinet, LocalDateTime start, LocalDateTime end);

}
