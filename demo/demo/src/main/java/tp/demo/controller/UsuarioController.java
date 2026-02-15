package tp.demo.controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import tp.demo.model.Usuario;
import tp.demo.service.UsuarioService;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.nuevoUsuario(usuario);
    }

    @GetMapping
    public List<Usuario> todos() {
        return usuarioService.listar();
    }

    @DeleteMapping
    public void eliminarTodos() {
        usuarioService.eliminarTodos();
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        usuarioService.eliminar(id);
    }
}