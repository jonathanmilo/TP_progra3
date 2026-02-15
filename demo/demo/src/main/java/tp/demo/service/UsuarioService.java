package tp.demo.service;

import tp.demo.repository.UsuarioRepository;
import tp.demo.model.Usuario;
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

    public void eliminarTodos() {
        usuarioRepository.deleteAll();
    }

    public void eliminar(String id) {
        usuarioRepository.deleteById(id);
    }
}