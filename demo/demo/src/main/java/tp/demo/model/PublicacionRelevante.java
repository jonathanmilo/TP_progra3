package tp.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * Representa una publicación en la colección de "top K más relevantes".
 * Esta colección se mantiene separada para optimizar consultas de publicaciones relevantes.
 *
 * Extiende de Publicacion para heredar todos sus campos y comportamiento.
 * En lugar de ordenar millones de publicaciones cada vez, mantenemos solo las K más relevantes
 * pre-calculadas y actualizadas incrementalmente.
 */
@Document(collection = "publicaciones_relevantes")
public class PublicacionRelevante extends Publicacion {

    public PublicacionRelevante() {
        super();
    }

    public PublicacionRelevante(Publicacion publicacion) {
        // Copiar campos básicos
        this.setId(publicacion.getId());
        this.contenido = publicacion.contenido;
        this.idCreador = publicacion.idCreador;
        this.fechaCreacion = publicacion.fechaCreacion;
        this.reacciones = publicacion.reacciones;
        this.relevancia = publicacion.relevancia;
        this.alcancePotencial = publicacion.alcancePotencial;
        this.costo = publicacion.costo;
        this.duracion = publicacion.duracion;
        this.tamaño = publicacion.tamaño;
        this.fechaActualizacionRelevancia = new Date();
    }
}

