package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true"; // no logueado

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "inicio");  // mensaje inicial
        return "dashboard";
    }

    @GetMapping("/dashboard/opcion1")
    public String opcion1(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "opcion1"); // fragmento opcion1
        return "dashboard";
    }
}
