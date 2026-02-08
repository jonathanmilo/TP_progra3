package tp.demo.Publicaciones.Entidad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "publicaciones_relevantes")
public class PublicacionRelevante {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;  // ← MISMO ID que la Publicacion original
    
    private String contenido;
    private String idCreador;
    private Date fechaCreacion;
    private List<UsuarioReaccion> reacciones;
    private float relevancia;
    private Date fechaActualizacionRelevancia;
    
    // Constructor vacío (requerido)
    public PublicacionRelevante() {
    }
    
    // Constructor desde Publicacion
    public PublicacionRelevante(Publicacion publicacion) {
        // ¡MISMO ID!
        this.id = publicacion.getId();
        this.contenido = publicacion.getContenido();
        this.idCreador = publicacion.getIdCreador();
        this.fechaCreacion = publicacion.getFechaCreacion();
        this.reacciones = publicacion.getReacciones() != null ? 
            new ArrayList<>(publicacion.getReacciones()) : new ArrayList<>();
        this.relevancia = publicacion.getRelevancia();
        this.fechaActualizacionRelevancia = new Date();
    }
    
    // Método para actualizar desde Publicacion
    public void actualizarDesdePublicacion(Publicacion publicacion) {
        if (!this.id.equals(publicacion.getId())) {
            throw new IllegalArgumentException("No se puede actualizar con publicación de diferente ID");
        }
        
        this.contenido = publicacion.getContenido();
        this.reacciones = publicacion.getReacciones() != null ? 
            new ArrayList<>(publicacion.getReacciones()) : new ArrayList<>();
        this.relevancia = publicacion.getRelevancia();
        this.fechaActualizacionRelevancia = new Date();
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    
    public String getIdCreador() { return idCreador; }
    public void setIdCreador(String idCreador) { this.idCreador = idCreador; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public List<UsuarioReaccion> getReacciones() { return reacciones; }
    public void setReacciones(List<UsuarioReaccion> reacciones) { this.reacciones = reacciones; }
    
    public float getRelevancia() { return relevancia; }
    public void setRelevancia(float relevancia) { 
        this.relevancia = relevancia;
        this.fechaActualizacionRelevancia = new Date();
    }
    
    public Date getFechaActualizacionRelevancia() { return fechaActualizacionRelevancia; }
    
    // Métodos útiles
    public boolean correspondeAPublicacion(Publicacion publicacion) {
        return this.id.equals(publicacion.getId());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PublicacionRelevante that = (PublicacionRelevante) obj;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}