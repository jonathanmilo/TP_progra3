package tp.demo.Publicaciones.Controller;
import org.springframework.web.bind.annotation.*;
import tp.demo.Publicaciones.Entidad.Publicacion;
import tp.demo.Publicaciones.Entidad.PublicacionRelevante;
import tp.demo.Publicaciones.Service.PublicacionService;
import tp.demo.recursos.KnapsackOptimizador;

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
    
}