package bursa.repositories;

import bursa.entities.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentRepo extends JpaRepository<AppDocument, Long> {
}
