package tp.demo.Publicaciones.Entidad;

import java.util.ArrayList;
import java.util.Date; 
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
    private float relevancia; // para solución de problema 1

    // ✅ Constructor sin argumentos (OBLIGATORIO para Jackson)
    public Publicacion() {
    }

    public Publicacion(String contenido, String idCreador, Date fechaCreacion, List<UsuarioReaccion> reacciones) {
        this.contenido = contenido;
        this.idCreador = idCreador;
        this.fechaCreacion = fechaCreacion;
        this.reacciones = reacciones;
    }

    public Publicacion(String contenido, String idCreador) {
        this.contenido = contenido;
        this.idCreador = idCreador;
        this.fechaCreacion = new Date();
        this.reacciones = new ArrayList<>();
    }
    // Getters y Setters

    public void setId(String id) {
        this.id = id;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public void setRelevancia(float relevancia) {
        this.relevancia = relevancia;
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
    
    public void setReacciones(List<UsuarioReaccion> reacciones) {
        this.reacciones = reacciones;
    }
    
    // ✅ Método para obtener cantidad de reacciones rápidamente
    public int getCantidadReacciones() {
        return reacciones != null ? reacciones.size() : 0;
    }
    
    public boolean agregarReaccion(String idUsuario) {
        if (this.reacciones == null) {
            this.reacciones = new ArrayList<>();
        }
        UsuarioReaccion nuevaReaccion = new UsuarioReaccion(this.id, idUsuario, new Date());
        this.reacciones.add(nuevaReaccion);
        return true;
    }
    
    public float getRelevancia() {
          float R;
            int numReacciones = this.getCantidadReacciones();
            int edadEnDias = (int) ((new Date().getTime() - this.fechaCreacion.getTime()) / (1000 * 60 * 60 * 24));
            if (edadEnDias == 0) {
                edadEnDias = 1; // Evitar división por cero
            }
            R = (float) numReacciones / edadEnDias;
            this.relevancia = R;
        
        return relevancia;
    }
}