package tp.demo.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import tp.demo.model.Publicacion;
import tp.demo.model.PublicacionRelevante;
import tp.demo.service.PublicacionService;
import tp.demo.utils.KnapsackOptimizador;

import java.util.List;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;

    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    @PostMapping ("/crear")
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
             String idUsuario,
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

        // Usar el método que maneja ambos en una sola transacción
        return publicacionService.agregarReaccionCompleta(id, idUsuario, like, comentarios, textoComentario);
    }
    @GetMapping("/relevantes")
    public List<PublicacionRelevante> publicacionesRelevantes() {
        return publicacionService.obtenerPublicacionesRelevantes();
    }

    @PostMapping("/relevantes/actualizarK")
    public List<PublicacionRelevante> actualizarK(@RequestParam int nuevoK) {
        return publicacionService.actualizarK(nuevoK);
    }

    @GetMapping("/optimizar-publicidad")
    public List<KnapsackOptimizador.ResultadoAsignacion> optimizarPublicidad(@RequestParam int presupuestoEmpresa) {
        return publicacionService.generarCampaña(presupuestoEmpresa);
    }
    
    @GetMapping("/optimizar-portada")
    public KnapsackOptimizador.ResultadoPortada optimizarPortada(
            @Parameter(description = "Espacio disponible en la portada", example = "20")
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
}