
package tp.demo.Publicaciones.Entidad;
import java.util.Date; // Cambiado de java.sql.Date a java.util.Date
import org.springframework.data.annotation.Id;
import io.swagger.v3.oas.annotations.media.Schema;

public class UsuarioReaccion {

    private String idUsuario;
    private String idPublicacion;
    private Date fechaReaccion;

    public UsuarioReaccion(String idPublicacion,String idUsuario, Date fechaReaccion) {
        
        this.idPublicacion = idPublicacion;
        this.idUsuario = idUsuario;
        this.fechaReaccion = fechaReaccion;
        
    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public Date getFechaReaccion() {
        return fechaReaccion;
    }
    public String getIdPublicacion() {
        return idPublicacion;
    }
}
