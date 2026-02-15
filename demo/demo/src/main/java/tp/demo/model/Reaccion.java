package tp.demo.model;

import java.util.Date;

/**
 * Representa una reacción de un usuario a una publicación.
 * Incluye likes (boolean) y comentarios (cantidad).
 */
public class Reaccion {
    private String idUsuario;
    private Boolean like;
    private int comentarios;
    private String textoComentario;
    private Date fechaReaccion;

    public Reaccion() {
        this.like = null;
        this.comentarios = 0;
        this.textoComentario = null;
        this.fechaReaccion = new Date();
    }

    public Reaccion(String idUsuario, Boolean like, int comentarios, String textoComentario) {
        this.idUsuario = idUsuario;
        this.like = like;
        this.comentarios = comentarios;
        this.textoComentario = textoComentario;
        this.fechaReaccion = new Date();
    }

    public Reaccion(String idUsuario, Boolean like, int comentarios) {
        this(idUsuario, like, comentarios, null);
    }

    public Reaccion(String idUsuario, Boolean like) {
        this(idUsuario, like, 0, null);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Boolean getLike() {
        return like;
    }

    public boolean isLike() {
        return like != null && like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public int getComentarios() {
        return comentarios;
    }

    public void setComentarios(int comentarios) {
        this.comentarios = comentarios;
    }

    public String getTextoComentario() {
        return textoComentario;
    }

    public void setTextoComentario(String textoComentario) {
        this.textoComentario = textoComentario;
    }

    public Date getFechaReaccion() {
        return fechaReaccion;
    }

    public void setFechaReaccion(Date fechaReaccion) {
        this.fechaReaccion = fechaReaccion;
    }

    // Métodos útiles
    public void incrementarComentarios(int cantidad) {
        this.comentarios += cantidad;
    }

    public void incrementarComentarios(int cantidad, String texto) {
        this.comentarios += cantidad;
        // Si ya había texto, agregar el nuevo separado por salto de línea
        if (this.textoComentario != null && !this.textoComentario.isEmpty()) {
            this.textoComentario += "\n---\n" + texto;
        } else {
            this.textoComentario = texto;
        }
    }

}

