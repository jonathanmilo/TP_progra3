package tp.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tp.demo.model.Publicacion;
import tp.demo.model.ResultadoAsignacion;
import tp.demo.model.ResultadoPortada;


import tp.demo.service.PublicacionService;
import java.util.List;



@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;

    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
        this.publicacionService.inicializarArbol(); // Asegura que el árbol se inicialice al crear el controller
    }

 

    @PostMapping("/crear")
    public Publicacion crear(@RequestBody Publicacion publicacion) {
        return publicacionService.nuevaPublicacion(publicacion);
    }

    @GetMapping
    public List<Publicacion> todos() {
        return publicacionService.listarTodas();
    }

    @GetMapping("/{id}")
    public Publicacion obtener(@PathVariable String id) {
        return publicacionService.getPublicacion(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Publicacion> porUsuario(@PathVariable String usuarioId) {
        return publicacionService.listarTodas().stream()
                .filter(p -> p.getIdCreador().equals(usuarioId))
                .toList();
    }

    @PostMapping("/{id}/reaccionar")
    public boolean reaccionar(@PathVariable String id, @RequestParam String idUsuario) {
        return publicacionService.reaccionar(id, idUsuario);
    }

    @PostMapping("/{id}/like")
    public boolean agregarLike(@PathVariable String id, @RequestParam String idUsuario) {
        return publicacionService.agregarLike(id, idUsuario);
    }

    @PostMapping("/{id}/comentario")
    public boolean agregarComentarios(
            @PathVariable String id,
            @RequestParam String idUsuario,
            @RequestParam(defaultValue = "1") int cantidad,
            @RequestParam(required = false) String textoComentario) {
        return publicacionService.agregarComentarios(id, idUsuario, cantidad, textoComentario);
    }

    @PostMapping("/{id}/interactuar")
    public boolean interactuar(
            @PathVariable String id,
            @RequestParam String idUsuario,
            @RequestParam(required = false) Boolean like,
            @RequestParam(defaultValue = "0") int comentarios,
            @RequestParam(required = false) String textoComentario) {
        return publicacionService.agregarReaccionCompleta(id, idUsuario, like, comentarios, textoComentario);
    }

    // ========== NUEVOS ENDPOINTS USANDO EL ÁRBOL BINARIO ==========

    /**
     * Obtiene todas las publicaciones ordenadas por relevancia (ascendente)
     */
    @GetMapping("/ordenadas")
    @Operation(summary = "Obtiene publicaciones ordenadas por relevancia (usando árbol binario)")
    public List<Publicacion> obtenerOrdenadas() {
        return publicacionService.obtenerPublicacionesOrdenadas();
    }

    /**
     * Obtiene las top K publicaciones más relevantes
     */
    @GetMapping("/top")
    @Operation(summary = "Obtiene las publicaciones más relevantes")
    public List<Publicacion> obtenerTopPublicaciones(
            @RequestParam(defaultValue = "10") int k) {
        return publicacionService.obtenerTopPublicaciones(k);
    }

    /**
     * Obtiene publicaciones con relevancia en un rango específico
     */
    @GetMapping("/rango")
    @Operation(summary = "Obtiene publicaciones por rango de relevancia")
    public List<Publicacion> obtenerPorRango(
            @RequestParam float min,
            @RequestParam float max) {
        return publicacionService.obtenerPublicacionesPorRango(min, max);
    }

    /**
     * Recalcula la relevancia de todas las publicaciones y reconstruye el árbol
     */
    @PostMapping("/recalcular-todo")
    @Operation(summary = "Recalcula relevancia de todas las publicaciones")
    public ResponseEntity<String> recalcularTodas() {
        publicacionService.recalcularTodasLasRelevancias();
        return ResponseEntity.ok("Relevancia recalculada y árbol reconstruido exitosamente");
    }

    /**
     * Obtiene estadísticas del árbol binario
     */
    @GetMapping("/arbol/estadisticas")
    @Operation(summary = "Obtiene estadísticas del árbol binario")
    public ResponseEntity<ArbolEstadisticas> obtenerEstadisticasArbol() {
        var arbol = Publicacion.getArbolGlobal();
        ArbolEstadisticas stats = new ArbolEstadisticas(
            arbol.getTamaño(),
            arbol.getAltura(),
            arbol.estaVacio()
        );
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint para inicializar manualmente el árbol (útil para testing)
     */
    @PostMapping("/arbol/inicializar")
    @Operation(summary = "Inicializa manualmente el árbol binario")
    public ResponseEntity<String> inicializarArbol() {
        publicacionService.inicializarArbol();
        return ResponseEntity.ok("Árbol binario inicializado manualmente");
    }

    // ========== ENDPOINTS LEGACY (mantenidos por compatibilidad) ==========

    /**
     * @deprecated Usar /top en su lugar
     */
    @Deprecated
    @GetMapping("/relevantes")
    public ResponseEntity<List<Publicacion>> publicacionesRelevantesLegacy() {
        List<Publicacion> top10 = publicacionService.obtenerTopPublicaciones(10);
        return ResponseEntity.ok(top10);
    }

    /**
     * @deprecated Usar /recalcular-todo en su lugar
     */
    @Deprecated
    @PostMapping("/relevantes/calcular")
    public ResponseEntity<String> calcularRelevanciaLegacy() {
        publicacionService.recalcularTodasLasRelevancias();
        int count = publicacionService.obtenerTopPublicaciones(1000).size();
        return ResponseEntity.ok("Relevancia calculada exitosamente. Total de publicaciones: " + count);
    }

    /**
     * @deprecated Ahora se maneja automáticamente con el árbol
     */
    @Deprecated
    @PostMapping("/relevantes/actualizarK")
    public ResponseEntity<String> actualizarKLegacy(@RequestParam int nuevoK) {
        return ResponseEntity.ok("Operación obsoleta. Use /top?k=" + nuevoK + " para obtener las top publicaciones");
    }

    @GetMapping("/optimizar-publicidad")
    public List<ResultadoAsignacion> optimizarPublicidad(@RequestParam int presupuestoEmpresa) {
        return publicacionService.generarCampaña(presupuestoEmpresa);
    }
    
    @GetMapping("/optimizar-portada")
    public ResultadoPortada optimizarPortada(
            @RequestParam int espacioDisponible) {
        return publicacionService.optimizarPortada(espacioDisponible);
    }

    @DeleteMapping
    public void eliminarTodas() {
        publicacionService.eliminarTodas();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar publicación por ID")
    public void eliminar(@PathVariable String id) {
        publicacionService.eliminar(id);
    }

    /**
     * Clase interna para estadísticas del árbol
     */
    public static class ArbolEstadisticas {
        private final int tamaño;
        private final int altura;
        private final boolean vacio;

        public ArbolEstadisticas(int tamaño, int altura, boolean vacio) {
            this.tamaño = tamaño;
            this.altura = altura;
            this.vacio = vacio;
        }

        public int getTamaño() { return tamaño; }
        public int getAltura() { return altura; }
        public boolean isVacio() { return vacio; }
    }
}