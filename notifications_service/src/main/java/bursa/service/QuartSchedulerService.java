package bursa.service;

import bursa.entities.AppNotification;

import java.time.LocalDateTime;

public interface QuartSchedulerService {
    void scheduleNotification(LocalDateTime startTime, Long chatId, AppNotification appNotification);
    void cancelNotification(Long notificationId);
}
