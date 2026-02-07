package tp.demo.Publicaciones.Service;
import org.springframework.stereotype.Service;
import tp.demo.Publicaciones.Entidad.Publicacion;
import tp.demo.Publicaciones.Repository.PublicacionRepository;
import java.util.List;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;

    public PublicacionService(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public Publicacion nuevaPublicacion(Publicacion publicacion) {
        return publicacionRepository.save(publicacion);
    }

    public List<Publicacion> listar() {
        return publicacionRepository.findAll();
    }

    public Publicacion getPublicacion(String id) {
        return publicacionRepository.findById(id).orElse(null);
    }   
}