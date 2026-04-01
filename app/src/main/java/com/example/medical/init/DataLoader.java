package com.example.medical.init;

import com.example.medical.repository.CabinetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final CabinetRepository cabinetRepository;

    public DataLoader(CabinetRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем: если база пустая (первый запуск), можно вывести сообщение.
        // Мы НЕ удаляем данные и НЕ создаем примеры.

        if (cabinetRepository.count() == 0) {
            System.out.println("База данных пуста. Готова к работе.");
        } else {
            System.out.println("База данных загружена. Найдено кабинетов: " + cabinetRepository.count());
        }
    }
}