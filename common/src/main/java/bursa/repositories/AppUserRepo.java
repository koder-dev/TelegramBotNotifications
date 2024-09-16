package bursa.repositories;

import bursa.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(long telegramUserId);
    Optional<AppUser> findByEmail(String email);
}
