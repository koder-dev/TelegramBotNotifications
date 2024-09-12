package bursa.repositories;

import bursa.entities.AppAudio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppAudioRepo extends JpaRepository<AppAudio, Long> {
    List<AppAudio> findByAppUserId(long userId, Pageable pageable);
    Long countByAppUserId(Long userId);
}
