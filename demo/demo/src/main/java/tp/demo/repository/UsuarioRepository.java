package tp.demo.repository;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;
import tp.demo.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findById(String id);
}