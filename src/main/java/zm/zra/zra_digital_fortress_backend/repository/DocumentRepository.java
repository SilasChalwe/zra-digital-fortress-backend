package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    //  custom queries as needed
}