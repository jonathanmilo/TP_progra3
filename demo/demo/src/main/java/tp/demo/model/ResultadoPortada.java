package tp.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para estructurar el resultado de la optimización de portada.
 * Representa las publicaciones seleccionadas para la portada después de aplicar
 * el algoritmo Knapsack que maximiza el beneficio (likes + comentarios).
 */
public class ResultadoPortada {
    private List<Publicacion> publicacionesDestacadas;
    private int beneficioTotal; // Suma de (likes + comentarios)
    private int espacioUsado;

    public ResultadoPortada() {
        this.publicacionesDestacadas = new ArrayList<>();
        this.beneficioTotal = 0;
        this.espacioUsado = 0;
    }

    // Getters
    public List<Publicacion> getPublicacionesDestacadas() {
        return publicacionesDestacadas;
    }

    public int getBeneficioTotal() {
        return beneficioTotal;
    }

    public int getEspacioUsado() {
        return espacioUsado;
    }

    /**
     * Agrega una publicación a la portada y actualiza las métricas.
     */
    public void addPublicacion(Publicacion pub) {
        this.publicacionesDestacadas.add(pub);
        this.beneficioTotal += (pub.getLikes() + pub.getComentarios());
        this.espacioUsado += pub.getTamaño();
    }
}

