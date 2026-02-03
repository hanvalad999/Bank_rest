# 🚀 Система Управления Банковскими Картами

Backend-приложение на Java Spring Boot для управления банковскими картами, пользователями и переводами.

## 📋 Описание

Система предоставляет REST API для:
- Управления банковскими картами (создание, блокировка, активация)
- Просмотра карт с маскированием номеров
- Переводов между картами
- Управления пользователями (для администраторов)

## 🛠 Технологии

- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Security** + **JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase** (миграции БД)
- **Docker Compose**
- **Swagger/OpenAPI** (документация API)
- **Lombok**

## 📦 Требования

- Java 21+
- Maven 3.6+
- Docker и Docker Compose

## 🚀 Быстрый старт

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd bank_rest-main
```

### 2. Настройка переменных окружения

Создайте файл `.env` в корне проекта (опционально, можно использовать значения по умолчанию):

```bash
# JWT Secret (минимум 32 символа)
JWT_SECRET=your-256-bit-secret-key-for-jwt-token-generation-minimum-32-characters

# Ключ шифрования номеров карт (Base64 encoded 32-byte key)
APP_CARD_ENCRYPTION_KEY=MTIzNDU2Nzg5MGFiY2RlZjEyMzQ1Njc4OTBhYmNkZWY=
```

Или установите переменные окружения в системе.

### 3. Запуск базы данных

```bash
docker-compose up -d postgres
```

Это запустит PostgreSQL на порту 5432.

### 4. Запуск приложения

```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

### 5. Доступ к документации API

После запуска приложения откройте в браузере:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📚 API Endpoints

### Аутентификация

- `POST /api/auth/register` - Регистрация нового пользователя
- `POST /api/auth/login` - Вход в систему (получение JWT токена)

### Карты (для пользователей)

- `GET /api/cards/my` - Получить свои карты (с пагинацией и фильтрацией по статусу)
- `GET /api/cards/{id}` - Получить карту по ID
- `POST /api/cards/{id}/block` - Запросить блокировку карты
- `PUT /api/cards/{id}/status` - Обновить статус карты

### Переводы (для пользователей)

- `POST /api/transfers` - Создать перевод между своими картами
- `GET /api/transfers/my` - Получить свои переводы (с пагинацией)
- `GET /api/transfers/{id}` - Получить перевод по ID

### Администратор

- `GET /api/admin/users` - Получить всех пользователей
- `GET /api/admin/users/{id}` - Получить пользователя по ID
- `POST /api/admin/users` - Создать пользователя
- `DELETE /api/admin/users/{id}` - Удалить пользователя
- `GET /api/admin/cards` - Получить все карты
- `POST /api/admin/cards` - Создать карту
- `DELETE /api/admin/cards/{id}` - Удалить карту
- `GET /api/admin/transfers` - Получить все переводы

## 🔐 Аутентификация

Все endpoints (кроме `/api/auth/**`) требуют JWT токен в заголовке:

```
Authorization: Bearer <your-jwt-token>
```

### Пример регистрации и входа

```bash
# Регистрация
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123"
  }'

# Вход
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123"
  }'
```

Ответ содержит JWT токен:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user1",
  "role": "USER"
}
```

## 👥 Роли

### USER (Пользователь)
- Просмотр своих карт
- Запрос блокировки своих карт
- Переводы между своими картами
- Просмотр своих переводов

### ADMIN (Администратор)
- Все возможности USER
- Создание, блокировка, активация, удаление карт
- Управление пользователями
- Просмотр всех карт и переводов

## 💳 Особенности

### Шифрование номеров карт
Номера карт шифруются в базе данных с использованием AES-GCM. При отображении они маскируются: `**** **** **** 1234`

### Автоматическая проверка истекших карт
Задача выполняется каждый день в полночь и автоматически помечает карты с истекшим сроком действия как `EXPIRED`.

### Оптимистичная блокировка
При переводах используется оптимистичная блокировка для предотвращения race conditions.

## 🧪 Тестирование

Запуск тестов:

```bash
mvn test
```

Тесты покрывают:
- Создание и управление картами
- Переводы между картами
- Проверку прав доступа
- Валидацию данных

## 📁 Структура проекта

```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── config/          # Конфигурация (Security, OpenAPI)
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA сущности
│   │   ├── exception/       # Обработка исключений
│   │   ├── repository/      # JPA репозитории
│   │   ├── security/        # JWT и Security компоненты
│   │   ├── service/         # Бизнес-логика
│   │   └── util/            # Утилиты (маскирование)
│   └── resources/
│       ├── application.yml  # Конфигурация приложения
│       └── db/migration/    # Liquibase миграции
└── test/                    # Тесты
```

## 🔧 Конфигурация

Основные настройки в `application.yml`:

- **Порт приложения**: 8080
- **База данных**: PostgreSQL (localhost:5432)
- **JWT срок действия**: 24 часа
- **Liquibase**: автоматическое применение миграций

## 🐳 Docker

### Запуск только базы данных

```bash
docker-compose up -d postgres
```

### Остановка

```bash
docker-compose down
```

### Очистка данных

```bash
docker-compose down -v
```

## 📝 Миграции базы данных

Миграции выполняются автоматически при запуске приложения через Liquibase.

Файлы миграций находятся в `src/main/resources/db/migration/`:
- `001-create-users.yaml` - Создание таблицы users
- `002-create-cards.yaml` - Создание таблицы cards
- `003-create-transfers.yaml` - Создание таблицы transfers

## 🚨 Обработка ошибок

API возвращает стандартизированные ответы об ошибках:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation errors",
  "errors": {
    "field": "Error message"
  }
}
```

## 📖 Дополнительная документация

- **OpenAPI спецификация**: `docs/openapi.yaml`
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## 🔒 Безопасность

- Пароли хешируются с использованием BCrypt
- Номера карт шифруются в БД (AES-GCM)
- JWT токены для аутентификации
- Ролевой доступ (ADMIN/USER)
- Валидация входных данных
- Защита от SQL инъекций (JPA)

## 📄 Лицензия

Этот проект создан в образовательных целях.

## 👤 Автор

Разработано в рамках учебного проекта.
