package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.repository.UsuarioRepository;
import mx.vhao.vhaologistica.service.UsuarioService;

import org.springframework.http.ResponseEntity;
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

    public UsuarioController(UsuarioRepository usuarioRepository,
                             UsuarioService usuarioService) {
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
    // 3️⃣ REGISTRAR USUARIO GENERAL
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
    // 5️⃣ BUSCAR USUARIOS (predictivo)
    // ============================================================
    @GetMapping("/buscar")
    public List<Usuario> buscar(
            @RequestParam(required = false) String q,
            HttpSession session) {

        if (q != null && q.isBlank()) {
            q = null;
        }

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        String estadoFiltro = null;
        if (usuarioSesion != null &&
                "coordinador".equalsIgnoreCase(usuarioSesion.getRol())) {
            estadoFiltro = usuarioSesion.getEstado();
        }

        return usuarioService.buscarGeneral(q, estadoFiltro);
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
    // 8️⃣ ELIMINAR USUARIO GENERAL
    // ============================================================
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }

    // ============================================================
    // 👨‍🔧 9️⃣ ENDPOINTS PARA TÉCNICOS
    // ============================================================

    // 🔹 Listar todos los técnicos
    @GetMapping("/tecnicos")
    public List<Usuario> listarTecnicos() {
        return usuarioRepository.findTecnicos();
    }

    // 🔹 Buscar técnicos (predictivo)
    @GetMapping("/tecnicos/buscar")
    public List<Usuario> buscarTecnicos(@RequestParam(required = false) String q) {
        if (q != null && q.isBlank()) q = null;
        return usuarioRepository.buscarTecnicos(q);
    }

    // 🔹 Registrar técnico (forzado rol TECNICO)
   // 🔹 Registrar técnico (forzado rol TECNICO)
@PostMapping("/tecnicos")
public ResponseEntity<?> registrarTecnico(@RequestBody Usuario u) {

    // Validar rol técnico
    u.setRol("TECNICO");

    // VALIDAR que tenga estado obligatorio
    if (u.getEstado() == null || u.getEstado().trim().isEmpty()) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "El campo ESTADO es obligatorio para registrar un técnico.");
        return ResponseEntity.badRequest().body(error);
    }

    // Guardar técnico
    Usuario guardado = usuarioService.guardarTecnico(u);

    Map<String, Object> resp = new HashMap<>();
    resp.put("success", true);
    resp.put("message", "Técnico registrado correctamente.");
    resp.put("usuario", guardado);

    return ResponseEntity.ok(resp);
}


    // 🔹 Actualizar técnico
    @PutMapping("/tecnicos/{id}")
    public Usuario actualizarTecnico(@PathVariable Long id, @RequestBody Usuario u) {
        u.setId(id);
        u.setRol("TECNICO");
        return usuarioService.guardar(u);
    }

    // 🔹 Eliminar técnico
    @DeleteMapping("/tecnicos/{id}")
    public void eliminarTecnico(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }

    // 🔹 Obtener técnico por ID
    @GetMapping("/tecnicos/{id}")
    public Usuario obtenerTecnico(@PathVariable Long id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u != null && "TECNICO".equalsIgnoreCase(u.getRol())) {
            return u;
        }
        return null;
    }

    

    // ============================================================
    // 🔧 Asignar coordinador a un técnico
    // ============================================================
    @PutMapping("/{id}/coordinador")
    public Usuario asignarCoordinador(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String coordinador = body.get("coordinador");

        Usuario u = usuarioService.buscarPorId(id);
        if (u == null) return null;

        u.setCoordinador(coordinador);
        return usuarioService.guardar(u);
    }

    // ============================================================
    // 🔹 Listar solo coordinadores
    // ============================================================
    @GetMapping("/coordinadores")
    public List<Usuario> soloCoordinadores() {
        return usuarioRepository.listarCoordinadores();
    }
}
