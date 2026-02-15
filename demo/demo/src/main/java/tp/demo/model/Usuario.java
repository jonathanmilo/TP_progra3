package tp.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public String id;

    public String nombre;

    public int tiempoMaximoExposicion;
    
    /**
    Cantidad de likes + comentarios en posteos publicados por el usuario
     */
    public int relevanciaEnPosteos = 0;

    public Usuario() {
    }
    
    public Usuario(String nombre) {
        this.nombre = nombre;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTiempoMaximoExposicion() { return tiempoMaximoExposicion; }

    public void setTiempoMaximoExposicion(int tiempo) { this.tiempoMaximoExposicion = tiempo; }

    public int getRelevanciaEnPosteos() { return relevanciaEnPosteos; }

    public void setRelevanciaEnPosteos(int relevancia) { this.relevanciaEnPosteos = relevancia; }

    /**
     * Incrementa la relevancia acumulada cuando una publicación del usuario gana engagement
     */
    public void incrementarRelevancia(int incremento) {
        this.relevanciaEnPosteos += incremento;
    }
}