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

    // LISTAR TODOS
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // GUARDAR GENERAL
    public Usuario guardar(Usuario u) {
        return usuarioRepository.save(u);
    }

    // BUSCAR POR ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // ELIMINAR POR ID
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // VALIDAR SI EXISTE CORREO
    public boolean correoExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    // BUSQUEDA GENERAL
    public List<Usuario> buscarGeneral(String q, String estadoFiltro) {
        if (q == null) q = "";
        return usuarioRepository.buscarGeneral(q, estadoFiltro);
    }

    // -------------------------
    //   SECCIÓN DE TÉCNICOS
    // -------------------------

    // ✔ GUARDAR TÉCNICO
    public Usuario guardarTecnico(Usuario u) {
        u.setRol("TECNICO");

        // Si no envían contraseña, asignar "1234"
        if (u.getPass() == null || u.getPass().isBlank()) {
            u.setPass("1234");
        }

        return usuarioRepository.save(u);
    }

    // LISTAR TÉCNICOS
    public List<Usuario> listarTecnicos() {
        return usuarioRepository.findTecnicos();
    }

    // BUSCAR TÉCNICOS POR TEXTO
    public List<Usuario> buscarTecnicos(String q) {
        return usuarioRepository.buscarTecnicos(q);
    }

    // LISTAR COORDINADORES
    public List<Usuario> listarCoordinadores() {
        return usuarioRepository.listarCoordinadores();
    }
}
