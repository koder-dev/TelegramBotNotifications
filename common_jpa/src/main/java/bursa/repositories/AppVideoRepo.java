package bursa.repositories;

import bursa.entities.AppVideo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppVideoRepo extends JpaRepository<AppVideo, Long> {
    List<AppVideo> findByAppUserId(long userId, Pageable pageable);
}
