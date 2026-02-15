package tp.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tp.demo.model.PublicacionRelevante;

public interface PublicacionesRelevantesRepository extends MongoRepository<PublicacionRelevante, String> {
}

