package tp.demo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para estructurar el resultado de la optimización de portada.
 * Representa las publicaciones seleccionadas para la portada después de aplicar
 * el algoritmo Knapsack que maximiza el beneficio (likes + comentarios).
 * 
 * Actualizado para trabajar con el árbol binario de publicaciones.
 */
public class ResultadoPortada {
    private List<Publicacion> publicacionesDestacadas;
    private int beneficioTotal; // Suma de (likes + comentarios)
    private int espacioUsado;
    private int espacioDisponible; // Espacio total disponible
    private float eficiencia; // Beneficio por unidad de espacio
    private int likesTotales;
    private int comentariosTotales;
    private float relevanciaPromedio;
    private List<String> idsPublicaciones; // Para referencia rápida
    private int cantidadPublicaciones;

    public ResultadoPortada() {
        this.publicacionesDestacadas = new ArrayList<>();
        this.beneficioTotal = 0;
        this.espacioUsado = 0;
        this.espacioDisponible = 0;
        this.eficiencia = 0;
        this.likesTotales = 0;
        this.comentariosTotales = 0;
        this.relevanciaPromedio = 0;
        this.idsPublicaciones = new ArrayList<>();
        this.cantidadPublicaciones = 0;
    }

    public ResultadoPortada(int espacioDisponible) {
        this();
        this.espacioDisponible = espacioDisponible;
    }

    // Getters originales
    public List<Publicacion> getPublicacionesDestacadas() {
        return publicacionesDestacadas;
    }

    public int getBeneficioTotal() {
        return beneficioTotal;
    }

    public int getEspacioUsado() {
        return espacioUsado;
    }

    // Nuevos getters
    public int getEspacioDisponible() {
        return espacioDisponible;
    }

    public float getEficiencia() {
        return eficiencia;
    }

    public int getLikesTotales() {
        return likesTotales;
    }

    public int getComentariosTotales() {
        return comentariosTotales;
    }

    public float getRelevanciaPromedio() {
        return relevanciaPromedio;
    }

    public List<String> getIdsPublicaciones() {
        return idsPublicaciones;
    }

    public int getCantidadPublicaciones() {
        return cantidadPublicaciones;
    }

    public int getEspacioLibre() {
        return espacioDisponible - espacioUsado;
    }

    public float getPorcentajeUso() {
        if (espacioDisponible == 0) return 0;
        return (float) espacioUsado / espacioDisponible * 100;
    }

    /**
     * Agrega una publicación a la portada y actualiza las métricas.
     */
    public void addPublicacion(Publicacion pub) {
        if (pub == null) return;

        this.publicacionesDestacadas.add(pub);
        this.idsPublicaciones.add(pub.getId());
        
        int likes = pub.getLikes();
        int comentarios = pub.getComentarios();
        int beneficioPub = likes + comentarios;
        int tamañoPub = pub.getTamaño();
        
        this.beneficioTotal += beneficioPub;
        this.espacioUsado += tamañoPub;
        this.likesTotales += likes;
        this.comentariosTotales += comentarios;
        
        // Actualizar cantidad y promedio
        this.cantidadPublicaciones = this.publicacionesDestacadas.size();
        this.relevanciaPromedio = (this.relevanciaPromedio * (this.cantidadPublicaciones - 1) + 
                                   pub.getRelevancia()) / this.cantidadPublicaciones;
        
        // Recalcular eficiencia
        if (this.espacioUsado > 0) {
            this.eficiencia = (float) this.beneficioTotal / this.espacioUsado;
        }
    }

    /**
     * Agrega múltiples publicaciones a la vez
     */
    public void addPublicaciones(List<Publicacion> publicaciones) {
        for (Publicacion pub : publicaciones) {
            addPublicacion(pub);
        }
    }

    /**
     * Verifica si una publicación específica está en la portada
     */
    public boolean contienePublicacion(String idPublicacion) {
        return idsPublicaciones.contains(idPublicacion);
    }

    /**
     * Verifica si una publicación específica está en la portada por objeto
     */
    public boolean contienePublicacion(Publicacion publicacion) {
        return publicacion != null && idsPublicaciones.contains(publicacion.getId());
    }

    /**
     * Obtiene la publicación con mayor relevancia en la portada
     */
    public Publicacion getPublicacionMasRelevante() {
        return publicacionesDestacadas.stream()
                .max((p1, p2) -> Float.compare(p1.getRelevancia(), p2.getRelevancia()))
                .orElse(null);
    }

    /**
     * Obtiene la publicación con más interacciones en la portada
     */
    public Publicacion getPublicacionConMasInteracciones() {
        return publicacionesDestacadas.stream()
                .max((p1, p2) -> Integer.compare(
                    p1.getLikes() + p1.getComentarios(),
                    p2.getLikes() + p2.getComentarios()))
                .orElse(null);
    }

    /**
     * Obtiene la publicación más grande (por tamaño) en la portada
     */
    public Publicacion getPublicacionMasGrande() {
        return publicacionesDestacadas.stream()
                .max((p1, p2) -> Integer.compare(p1.getTamaño(), p2.getTamaño()))
                .orElse(null);
    }

    /**
     * Obtiene la publicación más pequeña en la portada
     */
    public Publicacion getPublicacionMasPequena() {
        return publicacionesDestacadas.stream()
                .min((p1, p2) -> Integer.compare(p1.getTamaño(), p2.getTamaño()))
                .orElse(null);
    }

    /**
     * Limpia todos los resultados
     */
    public void limpiar() {
        this.publicacionesDestacadas.clear();
        this.idsPublicaciones.clear();
        this.beneficioTotal = 0;
        this.espacioUsado = 0;
        this.likesTotales = 0;
        this.comentariosTotales = 0;
        this.relevanciaPromedio = 0;
        this.eficiencia = 0;
        this.cantidadPublicaciones = 0;
    }

    /**
     * Establece el espacio disponible (útil para reinicializar)
     */
    public void setEspacioDisponible(int espacioDisponible) {
        this.espacioDisponible = espacioDisponible;
    }

    /**
     * Crea un resumen del resultado como String
     */
    public String generarResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESULTADO OPTIMIZACIÓN PORTADA ===\n");
        sb.append(String.format("Espacio: %d/%d usado (%.1f%%)\n", 
                espacioUsado, espacioDisponible, getPorcentajeUso()));
        sb.append(String.format("Beneficio total: %d (likes: %d, comentarios: %d)\n", 
                beneficioTotal, likesTotales, comentariosTotales));
        sb.append(String.format("Eficiencia: %.2f beneficio/unidad\n", eficiencia));
        sb.append(String.format("Publicaciones seleccionadas: %d\n", cantidadPublicaciones));
        sb.append(String.format("Relevancia promedio: %.2f\n", relevanciaPromedio));
        sb.append("----------------------------------------\n");
        
        for (int i = 0; i < publicacionesDestacadas.size(); i++) {
            Publicacion p = publicacionesDestacadas.get(i);
            sb.append(String.format("%d. [%s] %s (tamaño: %d, relevancia: %.2f, likes: %d, coment: %d)\n",
                    i + 1, p.getId(), p.getContenido(), p.getTamaño(), 
                    p.getRelevancia(), p.getLikes(), p.getComentarios()));
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("ResultadoPortada{publicaciones=%d, beneficio=%d, espacio=%d/%d, eficiencia=%.2f}",
                cantidadPublicaciones, beneficioTotal, espacioUsado, espacioDisponible, eficiencia);
    }

    // Métodos para facilitar la integración con Thymeleaf
    public boolean hayResultados() {
        return !publicacionesDestacadas.isEmpty();
    }

    public boolean espacioSuficiente() {
        return espacioUsado <= espacioDisponible;
    }

    /**
     * Clase interna para estadísticas detalladas (útil para la vista)
     */
    public static class EstadisticasDetalladas {
        private final int totalLikes;
        private final int totalComentarios;
        private final float relevanciaPromedio;
        private final float eficiencia;
        private final int espacioLibre;
        private final float porcentajeUso;

        public EstadisticasDetalladas(ResultadoPortada resultado) {
            this.totalLikes = resultado.likesTotales;
            this.totalComentarios = resultado.comentariosTotales;
            this.relevanciaPromedio = resultado.relevanciaPromedio;
            this.eficiencia = resultado.eficiencia;
            this.espacioLibre = resultado.getEspacioLibre();
            this.porcentajeUso = resultado.getPorcentajeUso();
        }

        // Getters
        public int getTotalLikes() { return totalLikes; }
        public int getTotalComentarios() { return totalComentarios; }
        public float getRelevanciaPromedio() { return relevanciaPromedio; }
        public float getEficiencia() { return eficiencia; }
        public int getEspacioLibre() { return espacioLibre; }
        public float getPorcentajeUso() { return porcentajeUso; }
    }

    /**
     * Obtiene estadísticas detalladas del resultado
     */
    public EstadisticasDetalladas getEstadisticas() {
        return new EstadisticasDetalladas(this);
    }
}