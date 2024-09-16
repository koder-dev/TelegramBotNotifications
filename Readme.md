
# HelperBot

HelperBot is a Telegram bot designed for managing user notifications and saving files to its database.

## Features

- **User Registration**: Users can register using their email address.
- **Flexible Notification Scheduling**: Users can schedule notifications with flexible date inputs.
- **File Storage**: Users can send files, which will be saved and available for download.
- **Email Notifications**: The bot can send notifications via email.
- **Multi-format Date Input**: Supports various date formats such as `18:00`, `30.09 17:59`, `завтра о 20:15`, and more.
- **Supported File Types**: Documents, Photos, Audio, Video.
- **Data Storage**: Uses MySQL to store user data and notifications.
- **Task Scheduling**: Notification tasks are scheduled and processed using Quartz or other schedulers.
- **Message Queue**: RabbitMQ is used for asynchronous task processing.

## Technologies

- **Java 17**
- **Spring Boot**
- **MySQL**
- **RabbitMQ**
- **Telegram Bot API**
- **Quartz Scheduler**

## Environment

- **Java 17+**
- **Docker**
- **Maven**
- **Telegram Bot Token** (can be obtained from [@BotFather](https://t.me/BotFather))

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/koder-dev/TelegramBotNotifications.git
   cd TelegramBotNotifications
   ```

2. Create the configuration file `application.properties` in every module's directory where `application.properties.example` is present.

3. Fill in the `.env` file based on `.env.example`.

4. **Docker Compose**: Run the following command to start the module containers:

   ```bash
   docker-compose up -d
   ```

5. The bot should now be running and available via Telegram.

## Workflow

1. The user sends a message to the bot.
2. The message is received by the `dispatcher` module, where the bot instance `TelegramBot` is running.
3. `TelegramBot` forwards the message (or `Update`) to the `UpdateController`, where the type of `Update` is determined.
4. Depending on the type of `Update`, it is passed to `UpdateProducer`, which uses the RabbitMQ message broker to place it in the appropriate message queue.
5. Each module has `Consumers` that capture messages from RabbitMQ.
6. Each module handles a specific function:
    - **node**: Processes Telegram commands and message types and forwards them to other modules.
    - **rest_module**: Handles file uploads from the database and email registration confirmation.
    - **mail_service**: Sends confirmation emails.
    - **notification_service**: Manages all notification logic.
    - **dispatcher**: Initializes the bot and controls `Update`.
    - **common**: Contains shared functionality used across all modules.
7. The full message flow is:  
   `dispatcher.TelegramBot` -> `UpdateController` -> `UpdateProducer` -> `RabbitQueue` -> `RabbitListener` -> `ModuleConsumer` -> `MainService` -> `ModuleProducer` -> `RabbitAnswerQueue` -> `RabbitAnswerListener` -> `dispatcher.AnswerConsumer` -> `UpdateController` -> `TelegramBot.sendAnswer`.

For any questions, contact `t.me/koderdev`.


---

# HelperBot

HelperBot — це Телеграм-бот, розроблений для керування користувацькими сповіщеннями та збереження файлів у базі даних бота.

## Можливості

- **Реєстрація користувачів**: Користувачі можуть реєструватися за допомогою електронної пошти.
- **Гнучке планування сповіщень**: Користувачі можуть планувати сповіщення з гнучкими параметрами введення дати.
- **Збереження файлів**: Користувачі можуть надсилати файли, які будуть збережені та доступні для завантаження.
- **Електронні сповіщення**: Бот може відправляти сповіщення через електронну пошту.
- **Багатоформатне введення дати**: Підтримуються різні формати дат, такі як `18:00`, `30.09 17:59`, `завтра о 20:15` та інші.
- **Підтримувані типи файлів**: Документи, Фото, Аудіо, Відео.
- **Збереження даних**: Для збереження даних користувачів та сповіщень використовується MySQL.
- **Планування завдань**: Завдання зі сповіщеннями плануються та обробляються за допомогою Quartz або інших планувальників.
- **Черга повідомлень**: RabbitMQ використовується для асинхронної обробки завдань.

## Технології

- **Java 17**
- **Spring Boot**
- **MySQL**
- **RabbitMQ**
- **Telegram Bot API**
- **Quartz Scheduler**

## Середовище

- **Java 17+**
- **Docker** 
- **Maven** 
- **Telegram Bot Token** (отримати в [@BotFather](https://t.me/BotFather))

## Початок роботи

1. Клонування репозиторію:

   ```bash
   git clone https://github.com/koder-dev/TelegramBotNotifications.git
   cd TelegramBotNotifications
   ```

2. Створіть файл конфігурації `application.properties` у кожному модулі, де присутній файл `application.properties.example`.

3. Заповніть файл `.env` за зразком `.env.example`.

4. **Docker Compose**: Виконайте команду, щоб запустити контейнери модулів:

   ```bash
   docker-compose up -d
   ```

5. Бот тепер працює та доступний через Telegram.

## Принцип роботи

1. Користувач відправляє повідомлення боту.
2. Повідомлення надходить у модуль `dispatcher`, де працює екземпляр бота `TelegramBot`.
3. `TelegramBot` передає повідомлення (далі — `Update`) до `UpdateController`, який визначає тип отриманого `Update`.
4. Залежно від типу `Update`, повідомлення передається до `UpdateProducer`, який через RabbitMQ додає його до відповідної черги повідомлень.
5. У кожному модулі реалізовано класи `Consumers`, які перехоплюють повідомлення з RabbitMQ.
6. Кожен модуль відповідає за свою частину функціоналу:
    - **node**: Відповідає за обробку команд та типів повідомлень Телеграму і передачу їх до інших модулів.
    - **rest_module**: Відповідає за завантаження файлів з бази даних і підтвердження реєстрації електронної пошти.
    - **mail_service**: Відповідає за надсилання листів для підтвердження електронної пошти.
    - **notification_service**: Відповідає за логіку роботи з нагадуваннями.
    - **dispatcher**: Ініціалізація бота та обробка `Update`.
    - **common**: Спільний функціонал, який використовується у кожному модулі.
7. Повний шлях повідомлення такий:  
   `dispatcher.TelegramBot` -> `UpdateController` -> `UpdateProducer` -> `RabbitQueue` -> `RabbitListener` -> `ModuleConsumer` -> `MainService` -> `ModuleProducer` -> `RabbitAnswerQueue` -> `RabbitAnswerListener` -> `dispatcher.AnswerConsumer` -> `UpdateController` -> `TelegramBot.sendAnswer`.

З будь-якими питаннями звертайтеся до `t.me/koderdev`.

---