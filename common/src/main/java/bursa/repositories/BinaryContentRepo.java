package bursa.repositories;

import bursa.entities.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepo extends JpaRepository<BinaryContent, Long> {
}
