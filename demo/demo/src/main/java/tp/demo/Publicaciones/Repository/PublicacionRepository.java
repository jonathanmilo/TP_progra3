package tp.demo.Publicaciones.Repository;
import tp.demo.Publicaciones.Entidad.Publicacion;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface PublicacionRepository extends MongoRepository<Publicacion, String> {

    Optional<Publicacion> findById(String id);
}