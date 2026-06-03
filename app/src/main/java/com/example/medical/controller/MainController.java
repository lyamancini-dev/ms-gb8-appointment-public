package com.example.medical.controller;

import com.example.medical.exception.NotFoundException;
import com.example.medical.model.CheckoutForm;
import com.example.medical.repository.PatientRepository;
import com.example.medical.model.Patient;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.medical.model.Appointment;
import com.example.medical.model.Cabinet;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.CabinetRepository;
import com.example.medical.service.SessionCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.medical.model.SlotGeneratorForm;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private SessionCart sessionCart;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Главная страница
    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("cabinets", cabinetRepository.findAll());
        return "index";
    }

    // Расписание кабинета
    @GetMapping("/cabinet/{id}")
    public String cabinetSchedule(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Кабинет не найден"));

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository
                .findByCabinetAndStartTimeBetweenOrderByStartTimeAsc(cabinet, startOfDay, endOfDay);

        model.addAttribute("cabinet", cabinet);
        model.addAttribute("selectedDate", date);
        model.addAttribute("appointments", appointments);

        return "schedule";
    }

    // --- КОРЗИНА ---

    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id) {
        Appointment slot = appointmentRepository.findById(id).orElse(null);
        if (slot != null && slot.getPatient() == null) {
            sessionCart.addSlot(slot);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cart", sessionCart.getCart());
        return "cart";
    }

    @GetMapping("/cart/clear")
    public String clearCart() {
        sessionCart.clearCart();
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        sessionCart.removeSlot(id);
        return "redirect:/cart";
    }

    // --- ОФОРМЛЕНИЕ ---

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (sessionCart.getCart().isEmpty()) return "redirect:/";
        model.addAttribute("checkoutForm", new CheckoutForm());
        model.addAttribute("cart", sessionCart.getCart());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(
            @Valid @ModelAttribute("checkoutForm") CheckoutForm form,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes)
    {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", sessionCart.getCart());
            return "checkout";
        }

        Patient patient = patientRepository.findByFullName(form.getFullName());
        if (patient == null) {
            patient = new Patient();
            patient.setFullName(form.getFullName());
            patient.setPhone(form.getPhone());
            patientRepository.save(patient);

            patient.setEmail(form.getEmail());
            patientRepository.save(patient);
        }

        for (Appointment slot : sessionCart.getCart()) {
            slot.setPatient(patient);
            String studyArea = request.getParameter("studyArea_" + slot.getId());
            slot.setStudyArea(studyArea);
            slot.setReferringSource(form.getReferringDoctor());
            slot.setNote(form.getNotes());
            slot.setQuota("бесплатно".equalsIgnoreCase(form.getPaymentType()));
            appointmentRepository.save(slot);
        }

        sessionCart.clearCart();
        redirectAttributes.addFlashAttribute("successMessage", "Запись успешно оформлена!");
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String successPage() { return "success"; }

    // --- ПОИСК ---

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Appointment> results = appointmentRepository.findByPatientFullNameContainingIgnoreCase(query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-results";
    }

    // --- УПРАВЛЕНИЕ ---

    @GetMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Appointment slot = appointmentRepository.findById(id).orElse(null);
        if (slot != null) {
            slot.setPatient(null);
            slot.setStudyArea(null);
            slot.setReferringSource(null);
            slot.setNote(null);
            slot.setQuota(false);
            appointmentRepository.save(slot);
            redirectAttributes.addFlashAttribute("successMessage", "Запись отменена.");
            return "redirect:/cabinet/" + slot.getCabinet().getId();
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Appointment slot = appointmentRepository.findById(id).orElse(null);
        if (slot == null) return "redirect:/";

        CheckoutForm form = new CheckoutForm();
        if (slot.getPatient() != null) {
            form.setFullName(slot.getPatient().getFullName());
            form.setPhone(slot.getPatient().getPhone());
        }
        form.setPaymentType(slot.isQuota() ? "бесплатно" : "платно");
        form.setReferringDoctor(slot.getReferringSource());
        form.setNotes(slot.getNote());
        form.setStudyArea(slot.getStudyArea());

        model.addAttribute("slot", slot);
        model.addAttribute("checkoutForm", form);
        return "edit-appointment";
    }

    @PostMapping("/edit/{id}")
    public String updateAppointment(@PathVariable Long id,
                                    @Valid @ModelAttribute("checkoutForm") CheckoutForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        Appointment slot = appointmentRepository.findById(id).orElse(null);
        if (slot == null) return "redirect:/";

        if (bindingResult.hasErrors()) {
            model.addAttribute("slot", slot);
            return "edit-appointment";
        }

        Patient patient = patientRepository.findByFullName(form.getFullName());
        if (patient == null) {
            patient = new Patient();
            patient.setFullName(form.getFullName().trim());
            patient.setPhone(form.getPhone().trim());
            patientRepository.save(patient);
        }
        patient.setPhone(form.getPhone());
        patientRepository.save(patient);

        slot.setPatient(patient);
        slot.setStudyArea(form.getStudyArea());
        slot.setReferringSource(form.getReferringDoctor());
        slot.setNote(form.getNotes());
        slot.setQuota("бесплатно".equalsIgnoreCase(form.getPaymentType()));
        appointmentRepository.save(slot);

        redirectAttributes.addFlashAttribute("successMessage", "Запись обновлена.");
        return "redirect:/cabinet/" + slot.getCabinet().getId();
    }

    @GetMapping("/slot/delete/{id}")
    public String deleteSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Appointment slot = appointmentRepository.findById(id).orElse(null);
        if (slot != null) {
            Long cabinetId = slot.getCabinet().getId();
            appointmentRepository.delete(slot);
            redirectAttributes.addFlashAttribute("successMessage", "Слот удален.");
            return "redirect:/cabinet/" + cabinetId;
        }
        return "redirect:/";
    }

    @PostMapping("/slot/add")
    public String addSingleSlot(
            @RequestParam Long cabinetId,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam(required = false) Integer duration
    ) {
        Cabinet cabinet = cabinetRepository.findById(cabinetId).orElse(null);
        if (cabinet == null) return "redirect:/";

        int dur = (duration != null && duration > 0) ? duration : cabinet.getDefaultSlotMinutes();
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(time);

        Appointment slot = new Appointment();
        slot.setCabinet(cabinet);
        slot.setStartTime(localDate.atTime(localTime));
        slot.setEndTime(localDate.atTime(localTime.plusMinutes(dur)));
        appointmentRepository.save(slot);

        return "redirect:/cabinet/" + cabinetId + "?date=" + date;
    }

    @GetMapping("/schedule/clear")
    @Transactional
    public String clearSchedule(
            @RequestParam Long cabinetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes
    ) {
        Cabinet cabinet = cabinetRepository.findById(cabinetId).orElse(null);
        if (cabinet != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            appointmentRepository.deleteByCabinetAndStartTimeBetween(cabinet, startOfDay, endOfDay);
            redirectAttributes.addFlashAttribute("successMessage", "Слоты удалены.");
        }
        return "redirect:/cabinet/" + cabinetId + "?date=" + date;
    }
}