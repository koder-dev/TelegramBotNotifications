package bursa.repositories;

import bursa.entities.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppNotificationsRepo extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByAppUserId(Long id);
}
