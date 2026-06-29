-- Создание логических схем
CREATE SCHEMA IF NOT EXISTS hospital_core; -- Зона Spring Boot
CREATE SCHEMA IF NOT EXISTS bot_state;     -- Зона Python бота

-- Используем одного админа для простоты, но разделяем данные по схемам
COMMENT ON SCHEMA hospital_core IS 'Main application data: slots, appointments, users';
COMMENT ON SCHEMA bot_state IS 'Telegram bot internals: conversation states, temporary cache';

-- Начальная таблица для проверки связи
CREATE TABLE IF NOT EXISTS hospital_core.slots (
    id SERIAL PRIMARY KEY,
    doctor_name VARCHAR(100) NOT NULL,
    slot_time TIMESTAMP NOT NULL,
    is_reserved BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 0 -- Для Optimistic Locking (защита от гонки)
);
