package com.example.medical.controller;

import com.example.medical.model.Appointment;
import com.example.medical.model.AppointmentStatus;
import com.example.medical.model.Cabinet;
import com.example.medical.model.SlotGeneratorForm;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.CabinetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }


    @GetMapping("/cabinets")
    public String listCabinets(Model model) {
        model.addAttribute("cabinets", cabinetRepository.findAll());
        return "admin/cabinets-list";
    }

    @GetMapping("/cabinets/new")
    public String showCreateCabinetForm(Model model) {
        model.addAttribute("cabinet", new Cabinet());
        return "admin/cabinet-form";
    }

    @PostMapping("/cabinets/save")
    public String saveCabinet(@ModelAttribute Cabinet cabinet, RedirectAttributes redirectAttributes) {
        if (cabinet.getName() != null) {
            cabinet.setName(cabinet.getName().trim());
        }
        cabinetRepository.save(cabinet);
        redirectAttributes.addFlashAttribute("successMessage", "Кабинет успешно сохранен.");
        return "redirect:/admin/cabinets";
    }

    @PostMapping("/cabinets/delete/{id}")
    public String deleteCabinet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (cabinetRepository.existsById(id)) {
            cabinetRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Кабинет удален.");
        }
        return "redirect:/admin/cabinets";
    }


    @GetMapping("/generate")
    public String showGenerateForm(
            @RequestParam(required = false) Long cabinetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        model.addAttribute("cabinets", cabinetRepository.findAll());

        SlotGeneratorForm form = new SlotGeneratorForm();
        if (cabinetId != null) {
            form.setCabinetId(cabinetId);
            Cabinet cab = cabinetRepository.findById(cabinetId).orElse(null);
            if (cab != null) {
                form.setIntervalMinutes(cab.getDefaultSlotMinutes());
            }
        }

        if (date != null) {
            form.setStartDate(date.toString());
            form.setEndDate(date.toString());
        }

        if (form.getStartTime() == null) form.setStartTime("08:00");
        if (form.getEndTime() == null) form.setEndTime("17:00");

        model.addAttribute("form", form);
        return "admin/generate-slots";
    }

    @PostMapping("/generate")
    public String generateSlots(@ModelAttribute SlotGeneratorForm form) {
        Cabinet cabinet = cabinetRepository.findById(form.getCabinetId()).orElse(null);
        if (cabinet == null) return "redirect:/admin/generate";

        LocalDate startDate = LocalDate.parse(form.getStartDate());
        LocalDate endDate = (form.getEndDate() != null && !form.getEndDate().isEmpty())
                ? LocalDate.parse(form.getEndDate())
                : startDate;

        LocalTime startTime = LocalTime.parse(form.getStartTime());
        LocalTime endTime = LocalTime.parse(form.getEndTime());

        int interval = (form.getIntervalMinutes() != null && form.getIntervalMinutes() > 0)
                ? form.getIntervalMinutes()
                : cabinet.getDefaultSlotMinutes();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalTime current = startTime;
            while (current.isBefore(endTime)) {
                if (!appointmentRepository.existsByCabinetAndStartTime(cabinet, currentDate.atTime(current))) {
                    Appointment slot = new Appointment();
                    slot.setCabinet(cabinet);
                    slot.setStartTime(currentDate.atTime(current));
                    slot.setEndTime(currentDate.atTime(current.plusMinutes(interval)));
                    appointmentRepository.save(slot);
                }
                current = current.plusMinutes(interval);
            }
            currentDate = currentDate.plusDays(1);
        }

        return "redirect:/cabinet/" + cabinet.getId() + "?date=" + form.getStartDate();
    }


    @GetMapping("/appointments")
    public String listAllAppointments(Model model) {
        List<Appointment> appointments = appointmentRepository.findByPatientIsNotNullOrderByStartTimeDesc();
        model.addAttribute("appointments", appointments);
        return "admin/appointments-list";
    }

    @PostMapping("/appointments/status")
    public String changeStatus(@RequestParam Long id, @RequestParam AppointmentStatus status) {
        Appointment app = appointmentRepository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus(status);
            appointmentRepository.save(app);
        }
        return "redirect:/admin/appointments";
    }
}