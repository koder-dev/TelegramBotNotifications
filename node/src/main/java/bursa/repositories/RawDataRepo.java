package bursa.repositories;

import bursa.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepo extends JpaRepository<RawData, Long> {
}
