package tp.demo.model;

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

    public String contenido;
    public String idCreador;
    public Date fechaCreacion;
    public List<Reaccion> reacciones; // Lista de reacciones (likes + comentarios)
    public float relevancia; // para solución de problema 1
    public int alcancePotencial; // Alcance potencial del anuncio (cantidad de personas que pueden visualizarlo)
    public int costo;    // Dinero que cuesta este anuncio
    public int duracion; // Tiempo que dura el anuncio (en segundos)
    public int tamaño; // Espacio que ocupa en la portada
    public TipoPublicacion tipo;
    public Date fechaActualizacionRelevancia; // Última vez que se calculó relevancia

    public Publicacion() {
        this.reacciones = new ArrayList<>();
    }

    public Publicacion(String contenido, String idCreador, Date fechaCreacion, List<Reaccion> reacciones, int costo, int duracion, TipoPublicacion tipo) {
        this.contenido = contenido;
        this.idCreador = idCreador;
        this.fechaCreacion = fechaCreacion;
        this.reacciones = reacciones != null ? reacciones : new ArrayList<>();
        this.costo = costo;
        this.duracion = duracion;
        this.tipo = tipo;
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
    
    public List<Reaccion> getReacciones() {
        return reacciones;
    }
    
    public void setReacciones(List<Reaccion> reacciones) {
        this.reacciones = reacciones;
    }

    public int getLikes() {
        if (reacciones == null) return 0;
        return (int) reacciones.stream().filter(Reaccion::isLike).count();
    }

    public int getComentarios() {
        if (reacciones == null) return 0;
        return reacciones.stream().mapToInt(Reaccion::getComentarios).sum();
    }

    public int getCosto() { return costo; }
    public void setCosto(int costo) { this.costo = costo; }

    public int getAlcancePotencial() {
        if (alcancePotencial > 0) {
            return alcancePotencial;
        }

        // Si no está definido, calcular un valor por defecto razonable
        // Basado en: costo * 5 (asumiendo que anuncios más caros tienen más alcance)
        // o en reacciones actuales * 10 si existen
        int reacciones = getCantidadReacciones();
        if (reacciones > 0) {
            return reacciones * 10;
        }

        // Última opción: estimar basado en el costo
        return costo > 0 ? costo * 5 : 100; // Mínimo 100 de alcance por defecto
    }

    public void setAlcancePotencial(int alcancePotencial) {
        this.alcancePotencial = alcancePotencial;
    }

    public int getDuracion() {
        if (duracion > 0) {
            return duracion;
        }
        return tipo.getDuracionBase();
    }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    
    public int getTamaño() {
        if (tamaño > 0) {
            return tamaño;
        }

        // El tamaño es dependiendo del tipo de publicación
        // no debe cambiar con las reacciones - el tamaño físico es fijo

        return tipo.getFactorTamaño();
    }

    public void setTamaño(int tamaño) { this.tamaño = tamaño; }

    public void setTipo(TipoPublicacion tipo) {
        this.tipo = tipo;
    }

    public int getCantidadReacciones() {
        return getLikes() + getComentarios();
    }

    public boolean agregarLike(String idUsuario) {
        if (this.reacciones == null) {
            this.reacciones = new ArrayList<>();
        }

        Reaccion reaccionExistente = buscarReaccionPorUsuario(idUsuario);

        if (reaccionExistente != null) {
            reaccionExistente.setLike(true);
        } else {
            Reaccion nuevaReaccion = new Reaccion(idUsuario, true, 0);
            this.reacciones.add(nuevaReaccion);
        }

        return true;
    }

    public boolean agregarComentarios(String idUsuario, int cantidad) {
        return agregarComentarios(idUsuario, cantidad, null);
    }

    public boolean agregarComentarios(String idUsuario, int cantidad, String textoComentario) {
        if (this.reacciones == null) {
            this.reacciones = new ArrayList<>();
        }

        Reaccion reaccionExistente = buscarReaccionPorUsuario(idUsuario);

        if (reaccionExistente != null) {
            if (textoComentario != null && !textoComentario.isEmpty()) {
                reaccionExistente.incrementarComentarios(cantidad, textoComentario);
            } else {
                reaccionExistente.incrementarComentarios(cantidad);
            }
        } else {
            Reaccion nuevaReaccion = new Reaccion(idUsuario, null, cantidad, textoComentario);
            this.reacciones.add(nuevaReaccion);
        }

        return true;
    }

    public boolean agregarReaccion(String idUsuario, Boolean like, int comentarios) {
        return agregarReaccion(idUsuario, like, comentarios, null);
    }

    public boolean agregarReaccion(String idUsuario, Boolean like, int comentarios, String textoComentario) {
        if (this.reacciones == null) {
            this.reacciones = new ArrayList<>();
        }

        Reaccion reaccionExistente = buscarReaccionPorUsuario(idUsuario);

        if (reaccionExistente != null) {
            if (like != null && like) {
                reaccionExistente.setLike(true);
            }
            if (comentarios > 0) {
                if (textoComentario != null && !textoComentario.isEmpty()) {
                    reaccionExistente.incrementarComentarios(comentarios, textoComentario);
                } else {
                    reaccionExistente.incrementarComentarios(comentarios);
                }
            }
        } else {
            Reaccion nuevaReaccion = new Reaccion(idUsuario, like, comentarios, textoComentario);
            this.reacciones.add(nuevaReaccion);
        }

        return true;
    }
    
    public boolean agregarReaccion(String idUsuario) {
        return agregarLike(idUsuario); // Por defecto, se considera un like
    }

    private Reaccion buscarReaccionPorUsuario(String idUsuario) {
        if (this.reacciones == null) return null;
        return this.reacciones.stream()
                .filter(r -> r.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElse(null);
    }

    public int getRelevancia() {
        int R;
        int numReacciones = this.getCantidadReacciones();
        int edadEnDias = (int) ((new Date().getTime() - this.fechaCreacion.getTime()) / (1000 * 60 * 60 * 24));
        if (edadEnDias == 0) {
            edadEnDias = 1; // Evitar división por cero
        }
        R =  numReacciones / edadEnDias;
        this.relevancia = R;
        this.fechaActualizacionRelevancia = new Date();

        return (int) relevancia;
    }

    public Date getFechaActualizacionRelevancia() {
        return fechaActualizacionRelevancia;
    }

    public void setFechaActualizacionRelevancia(Date fecha) {
        this.fechaActualizacionRelevancia = fecha;
    }
}