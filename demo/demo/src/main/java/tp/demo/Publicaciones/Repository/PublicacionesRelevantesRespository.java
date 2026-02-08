package tp.demo.Publicaciones.Repository;

import tp.demo.Publicaciones.Entidad.PublicacionRelevante;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PublicacionesRelevantesRespository extends MongoRepository<PublicacionRelevante, String> {

    Optional<PublicacionRelevante> findById(String id);
    void deleteById(String id);

    List<PublicacionRelevante> findAll();
}