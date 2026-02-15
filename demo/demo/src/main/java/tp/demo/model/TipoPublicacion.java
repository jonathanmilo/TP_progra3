package tp.demo.model;

/**
 * Enum que define los diferentes tipos de publicaciones disponibles.
 * Cada tipo tiene características específicas que afectan:
 * - Tamaño en portada
 * - Costo de producción
 * - Alcance potencial
 * - Duración de visualización en segundos
 */
public enum TipoPublicacion {

    /**
     * Publicación de texto simple.
     * - Tamaño: Pequeño (compacto)
     * - Duración: Rápida de consumir
     * - Engagement: Moderado
     */
    TEXTO("Texto", 1, 5),

    /**
     * Publicación con imagen o foto.
     * - Tamaño: Medio
     * - Duración: Media
     * - Engagement: Alto (las imágenes atraen más)
     */
    IMAGEN("Imagen", 2, 10),

    /**
     * Video estándar
     * - Tamaño: Grande
     * - Duración: Larga
     * - Engagement: Muy alto
     */
    VIDEO("Video", 4, 20),

    /**
     * Video corto estilo Reels/TikTok
     * - Tamaño: Medio-Grande
     * - Duración: Corta pero impactante
     * - Engagement: Muy alto (formato viral)
     */
    REEL("Reel", 3, 15),

    /**
     * Encuesta o poll interactivo.
     * - Tamaño: Medio
     * - Duración: Media
     * - Engagement: Alto
     */
    ENCUESTA("Encuesta", 2, 12),

    /**
     * Artículo extenso o blog post.
     * - Tamaño: Muy grande
     * - Duración: Muy larga
     * - Engagement: Moderado
     */
    ARTICULO("Artículo", 5, 25);

    private final String nombre;
    private final int factorTamaño;
    private final int duracionBase;

    TipoPublicacion(String nombre, int factorTamaño, int duracionBase) {
        this.nombre = nombre;
        this.factorTamaño = factorTamaño;
        this.duracionBase = duracionBase;
    }

    public String getNombre() {
        return nombre;
    }

    public int getFactorTamaño() {
        return factorTamaño;
    }

    public int getDuracionBase() {
        return duracionBase;
    }

    /**
     * Detecta automáticamente el tipo basándose en el contenido.
     * Útil cuando se crea una publicación sin especificar tipo explícitamente.
     */
    public static TipoPublicacion detectarPorContenido(String contenido) {
        if (contenido == null || contenido.isEmpty()) {
            return TEXTO;
        }

        String contenidoLower = contenido.toLowerCase();

        // Detectar por palabras clave y emojis
        if (contenidoLower.contains("reel") || contenidoLower.contains("🎬")) {
            return REEL;
        }
        if (contenidoLower.contains("video") || contenidoLower.contains("🎥")) {
            return VIDEO;
        }
        if (contenidoLower.contains("imagen") || contenidoLower.contains("foto") ||
            contenidoLower.contains("📸") || contenidoLower.contains("🖼️")) {
            return IMAGEN;
        }
        if (contenidoLower.contains("encuesta") || contenidoLower.contains("poll") ||
            contenidoLower.contains("📊") || contenidoLower.contains("votación")) {
            return ENCUESTA;
        }
        if (contenidoLower.contains("artículo") || contenidoLower.contains("blog") ||
            contenido.length() > 300) {
            return ARTICULO;
        }

        // Por defecto, texto simple
        return TEXTO;
    }
}

