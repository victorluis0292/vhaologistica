package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request, HttpSession session) {
        String correo = request.get("correo");
        String pass = request.get("pass");

        Map<String, Object> response = new HashMap<>();

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "El usuario no existe");
            return response;
        }

        if (!usuario.getPass().equals(pass)) {
            response.put("success", false);
            response.put("message", "Contraseña incorrecta");
            return response;
        }

        // Guardar usuario en sesión
        session.setAttribute("usuario", usuario);

        response.put("success", true);
        response.put("message", "Login correcto");
        response.put("usuario", usuario);

        return response;
    }
}
