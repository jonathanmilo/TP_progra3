package tp.demo.Usuarios.Service;

import tp.demo.Usuarios.Repository.UsuarioRepository;
import tp.demo.Usuarios.Entidad.Usuario;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario nuevoUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuario(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }   
}