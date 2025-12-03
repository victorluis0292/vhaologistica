package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginViewController {

    private final UsuarioRepository usuarioRepository;

    public LoginViewController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // =========================================================
    // 1️⃣ Mostrar login.html
    // =========================================================
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    // =========================================================
    // 2️⃣ Procesar LOGIN tradicional del form
    // =========================================================
    @PostMapping("/login")
    public String loginForm(@RequestParam String correo,
                            @RequestParam String pass,
                            HttpSession session) {

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElse(null);

        // Validaciones
        if (usuario == null || !usuario.getPass().equals(pass)) {
            return "redirect:/?error=true";
        }

        // Guardar usuario en sesión
        session.setAttribute("usuario", usuario);

        // Redirigir al dashboard
        return "redirect:/dashboard";
    }
}
