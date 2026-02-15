package tp.demo.repository;
import tp.demo.model.Publicacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PublicacionRepository extends MongoRepository<Publicacion, String> {

    Optional<Publicacion> findById(String id);

    List<Publicacion> findAll();
}