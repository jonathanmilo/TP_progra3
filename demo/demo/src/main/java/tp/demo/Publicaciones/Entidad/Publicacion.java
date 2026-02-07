package tp.demo.Publicaciones.Entidad;
import tp.demo.Publicaciones.Entidad.UsuarioReaccion;
import java.sql.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "publicaciones")
public class Publicacion {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)

    private String id;

    private String contenido;
    private String idCreador;
    private Date fechaCreacion;
    private List<UsuarioReaccion> reacciones;

    public Publicacion( String contenido, String idCreador, Date fechaCreacion, List<UsuarioReaccion> reacciones) {
        
        this.contenido = contenido;
        this.idCreador = idCreador;
        this.fechaCreacion = fechaCreacion;
        this.reacciones = reacciones;
    }

    public String getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public String getIdCreador() {
        return idCreador;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    public List<UsuarioReaccion> getReacciones() {
        return reacciones;
    }
    public void agregarReaccion(UsuarioReaccion reaccion) {
        reacciones.add(reaccion);
    }
}
