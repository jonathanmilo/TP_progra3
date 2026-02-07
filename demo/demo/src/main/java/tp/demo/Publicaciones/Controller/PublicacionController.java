package tp.demo.Publicaciones.Controller;
import org.springframework.web.bind.annotation.*;
import tp.demo.Publicaciones.Entidad.Publicacion;
import tp.demo.Publicaciones.Service.PublicacionService;

import java.util.List;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;

    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    @PostMapping
    public Publicacion crear(@RequestBody Publicacion publicacion) {
        return publicacionService.nuevaPublicacion(publicacion);
    }

    @GetMapping
    public List<Publicacion> todos() {
        return publicacionService.listar();
    }
    @GetMapping("/{id}")
    public Publicacion obtener(@PathVariable String id) {
        return publicacionService.getPublicacion(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Publicacion> porUsuario(@PathVariable String usuarioId) {
        return publicacionService.listar().stream()
                .filter(p -> p.getIdCreador().equals(usuarioId))
                .toList();
    }
}