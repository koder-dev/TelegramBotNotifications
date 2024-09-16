package bursa.repositories;

import bursa.entities.AppPhoto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppPhotoRepo extends JpaRepository<AppPhoto, Long> {
    List<AppPhoto> findByAppUserId(long userId, Pageable pageable);
    Long countByAppUserId(Long userId);
}
