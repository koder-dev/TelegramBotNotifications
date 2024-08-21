package bursa.repositories;

import bursa.entities.AppVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVideoRepo extends JpaRepository<AppVideo, Long> {
}
