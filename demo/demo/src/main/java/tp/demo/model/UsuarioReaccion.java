
package tp.demo.model;
import java.util.Date; 

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
