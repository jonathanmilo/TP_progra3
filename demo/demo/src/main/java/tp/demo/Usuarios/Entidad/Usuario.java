package tp.demo.Usuarios.Entidad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;
    
    private String nombre;
    
    public Usuario() {
    }
    
    // Constructor con argumentos
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
}