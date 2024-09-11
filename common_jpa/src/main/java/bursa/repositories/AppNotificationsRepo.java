package bursa.repositories;

import bursa.entities.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppNotificationsRepo extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByAppUserId(Long id);
    void deleteByNotifyTimeBefore(LocalDateTime time);
}
