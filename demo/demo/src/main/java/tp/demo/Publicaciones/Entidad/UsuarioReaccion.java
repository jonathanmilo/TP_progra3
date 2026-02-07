
package tp.demo.Publicaciones.Entidad;
import java.sql.Date;

public class UsuarioReaccion {
    private String id;
    private String idUsuario;
    private Date fechaReaccion;

    public UsuarioReaccion(String id,String idUsuario, Date fechaReaccion) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.fechaReaccion = fechaReaccion;
        
    }
    public String getId() {
        return id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public Date getFechaReaccion() {
        return fechaReaccion;
    }
}
