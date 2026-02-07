package tp.demo.Usuarios.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import tp.demo.Usuarios.Entidad.Usuario;
import tp.demo.Usuarios.Service.UsuarioService;


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
}