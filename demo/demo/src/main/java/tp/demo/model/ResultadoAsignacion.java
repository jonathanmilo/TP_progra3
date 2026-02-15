package tp.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para estructurar el resultado de la optimización de publicidad por usuario.
 * Representa la asignación de anuncios a un usuario específico después de aplicar
 * el algoritmo Knapsack.
 */
public class ResultadoAsignacion {
    private String usuarioNombre;
    private List<Publicacion> anunciosAsignados;
    private int alcanceTotal;
    private int tiempoTotalUsado;
    private int costoEconomicoTotal;

    public ResultadoAsignacion(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
        this.anunciosAsignados = new ArrayList<>();
        this.alcanceTotal = 0;
        this.tiempoTotalUsado = 0;
        this.costoEconomicoTotal = 0;
    }

    // Getters
    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public List<Publicacion> getAnunciosAsignados() {
        return anunciosAsignados;
    }

    public int getAlcanceTotal() {
        return alcanceTotal;
    }

    public int getTiempoTotalUsado() {
        return tiempoTotalUsado;
    }

    public int getCostoEconomicoTotal() {
        return costoEconomicoTotal;
    }

    /**
     * Agrega un anuncio a la asignación del usuario y actualiza las métricas.
     */
    public void addAnuncio(Publicacion anuncio) {
        this.anunciosAsignados.add(anuncio);
        this.alcanceTotal += anuncio.getAlcancePotencial();
        this.tiempoTotalUsado += anuncio.getDuracion();
        this.costoEconomicoTotal += anuncio.getCosto();
    }
}

