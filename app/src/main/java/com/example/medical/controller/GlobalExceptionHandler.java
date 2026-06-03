package com.example.medical.controller;

import com.example.medical.exception.NotFoundException;
import com.example.medical.service.SessionCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.TypeMismatchException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private SessionCart sessionCart;

    @ModelAttribute("sessionCartSize")
    public int getCartSize() {
        return sessionCart.getCart().size();
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("status", "404");
        model.addAttribute("error", "Объект не найден");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundPage(NoHandlerFoundException ex, Model model) {
        model.addAttribute("status", "404");
        model.addAttribute("error", "Страница не найдена");
        model.addAttribute("message", "Запрашиваемая страница не существует.");
        return "error/404";
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatch(TypeMismatchException ex, Model model) {
        model.addAttribute("status", "404");
        model.addAttribute("error", "Некорректный запрос");
        model.addAttribute("message", "Неверный идентификатор.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("status", "500");
        model.addAttribute("error", "Внутренняя ошибка сервера");
        model.addAttribute("message", "Произошла ошибка.");
        return "error/500";
    }
}