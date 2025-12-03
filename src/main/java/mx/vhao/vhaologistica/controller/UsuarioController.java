package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.repository.UsuarioRepository;
import mx.vhao.vhaologistica.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    // ============================================================
    // 1️⃣ OBTENER USUARIO EN SESIÓN
    // ============================================================
    @GetMapping("/sesion")
    public Map<String, Object> obtenerUsuarioSesion(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "No hay usuario en sesión");
            return response;
        }

        response.put("success", true);
        response.put("usuario", usuario);
        return response;
    }

    // ============================================================
    // 2️⃣ CERRAR SESIÓN
    // ============================================================
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        session.invalidate();
        response.put("success", true);
        response.put("message", "Sesión cerrada correctamente");
        return response;
    }

    // ============================================================
    // 3️⃣ REGISTRAR USUARIO (OPCIONAL)
    // ============================================================
    @PostMapping("/registrar")
    public Map<String, Object> registrar(@RequestBody Usuario nuevoUsuario) {
        Map<String, Object> response = new HashMap<>();

        if (usuarioRepository.findByCorreo(nuevoUsuario.getCorreo()).isPresent()) {
            response.put("success", false);
            response.put("message", "El correo ya está registrado");
            return response;
        }

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        response.put("success", true);
        response.put("message", "Usuario registrado correctamente");
        response.put("usuario", guardado);
        return response;
    }

    // ============================================================
    // 4️⃣ LISTAR TODOS LOS USUARIOS
    // ============================================================
    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    // ============================================================
    // 5️⃣ BUSCAR USUARIOS (nombre, correo, rol)
    // ============================================================
    @GetMapping("/buscar")
    public List<Usuario> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String rol,
            HttpSession session) {

        if (nombre != null && nombre.isBlank()) nombre = null;
        if (correo != null && correo.isBlank()) correo = null;
        if (rol != null && rol.isBlank()) rol = null;

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        String estadoFiltro = null;
        // Si el usuario en sesión es coordinador, filtramos por su estado
        if (usuarioSesion != null && "coordinador".equalsIgnoreCase(usuarioSesion.getRol())) {
            estadoFiltro = usuarioSesion.getEstado();
        }

        return usuarioService.buscarPorEstado(nombre, correo, rol, estadoFiltro);
    }

    // ============================================================
    // 6️⃣ GUARDAR NUEVO USUARIO
    // ============================================================
    @PostMapping
    public Usuario guardar(@RequestBody Usuario u) {
        return usuarioService.guardar(u);
    }

    // ============================================================
    // 7️⃣ ACTUALIZAR EXISTENTE
    // ============================================================
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario u) {
        u.setId(id);
        return usuarioService.guardar(u);
    }

    // ============================================================
    // 8️⃣ ELIMINAR USUARIO
    // ============================================================
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
