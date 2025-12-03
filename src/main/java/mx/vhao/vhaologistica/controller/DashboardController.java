package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    // Dashboard principal con fragment dinámico
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(value = "contenido", required = false, defaultValue = "inicio") String contenido,
            HttpSession session,
            Model model) {

        // Verificar usuario en sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/?error=true";
        }

        // Pasar usuario y fragmento a mostrar
        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", contenido);

        return "dashboard"; // Thymeleaf usará 'contenido' para cargar el fragmento correspondiente
    }
     // --- Logout ---
    @GetMapping("/dashboard/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("[INFO] Usuario cerró sesión correctamente.");
        return "redirect:/"; // redirige al login o página principal
    }
}
