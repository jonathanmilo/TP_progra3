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
        try{ 
        System.out.println("Listando publicaciones..." + publicacionRepository.findAll().size() + " publicaciones encontradas.");
        return publicacionRepository.findAll();



        }
        catch(Exception e){
            System.out.println("Error al listar publicaciones: " + e.getMessage());
        }
        return null;
    }

    public Publicacion getPublicacion(String id) {
        return publicacionRepository.findById(id).orElse(null);
    }   
    public boolean reaccionar(String idPublicacion, String idUsuario) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion).orElse(null);
        if (publicacion != null) {
            try{
                boolean resultado = publicacion.agregarReaccion(idUsuario);
                if (resultado) {
                    publicacionRepository.save(publicacion);
                    System.out.println("Reacción agregada a la publicación con ID: " + idPublicacion);
                    return true;
                } else {
                    System.out.println("No se pudo agregar la reacción a la publicación con ID: " + idPublicacion);
                }
            }
            catch(Exception e){
                System.out.println("Error al agregar reacción: " + e.getMessage());
            }
            return false;
        }
        return false;
    }
}