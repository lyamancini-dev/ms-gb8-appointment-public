package com.example.medical.service;

import com.example.medical.model.Appointment;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Service
// Каждый пользователь получает свою сессию.
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionCart {

    // Теперь нам не нужен HttpSession, мы просто храним список прямо здесь!
    private List<Appointment> cart = new ArrayList<>();

    public List<Appointment> getCart() {
        return cart;
    }

    public void addSlot(Appointment slot) {
        if (cart.stream().noneMatch(s -> s.getId().equals(slot.getId()))) {
            cart.add(slot);
        }
    }

    public void removeSlot(Long slotId) {
        cart.removeIf(s -> s.getId().equals(slotId));
    }

    public void clearCart() {
        cart.clear();
    }
}