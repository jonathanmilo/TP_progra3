package tp.demo.Usuarios.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;
import tp.demo.Usuarios.Entidad.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findById(String id);
}