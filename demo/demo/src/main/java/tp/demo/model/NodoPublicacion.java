package tp.demo.model;

public class NodoPublicacion {
    private Publicacion publicacion;
    private NodoPublicacion izquierdo;
    private NodoPublicacion derecho;
    private float peso; // Relevancia de la publicación

    public NodoPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
        this.peso = publicacion.getRelevancia();
        this.izquierdo = null;
        this.derecho = null;
    }

    // Getters y Setters
    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
        this.peso = publicacion.getRelevancia();
    }

    public NodoPublicacion getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoPublicacion izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoPublicacion getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoPublicacion derecho) {
        this.derecho = derecho;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
}