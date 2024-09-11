package bursa.repositories;

import bursa.entities.AppDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppDocumentRepo extends JpaRepository<AppDocument, Long> {
    List<AppDocument> findByAppUserId(long userId, Pageable pageable);

}
