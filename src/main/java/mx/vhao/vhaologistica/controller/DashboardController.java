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

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", contenido); // fragment a mostrar
        return "dashboard";
    }

    // Rutas directas para cada fragmento (opciones del sidebar)
    @GetMapping("/dashboard/opcion1")
    public String opcion1(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "opcion1"); // fragmento opcion1
        return "dashboard";
    }

    @GetMapping("/dashboard/opcion2")
    public String opcion2(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "opcion2"); // fragmento opcion2
        return "dashboard";
    }

    @GetMapping("/dashboard/opcion3")
    public String opcion3(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "opcion3"); // fragmento opcion3
        return "dashboard";
    }
}
