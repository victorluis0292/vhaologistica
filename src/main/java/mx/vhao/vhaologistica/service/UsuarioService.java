package mx.vhao.vhaologistica.service;

import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Listar todos los usuarios
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // Guardar o actualizar usuario
    public Usuario guardar(Usuario u) {
        return usuarioRepository.save(u);
    }

    // Buscar por ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Eliminar por ID
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Validar correo existente
    public boolean correoExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    // Buscar usuarios con filtrado opcional por estado
    public List<Usuario> buscarPorEstado(String nombre, String correo, String rol, String estado) {
        if (estado != null && !estado.isBlank()) {
            return usuarioRepository.buscarConEstado(nombre, correo, rol, estado);
        }
        return usuarioRepository.buscar(nombre, correo, rol);
    }
}
