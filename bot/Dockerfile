FROM python:3.11-slim

WORKDIR /app

# Установка системных зависимостей для работы с Postgres
RUN apt-get update && apt-get install -y libpq-dev gcc && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Бот работает через Long Polling, порты открывать не нужно
ENTRYPOINT ["python", "main.py"]