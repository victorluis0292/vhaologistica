package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.service.ExcelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard/excel")
public class ExcelController {

    private final ExcelService excelService;

    private final List<String> columnasSistema = List.of(
            "Ticket","Afiliación","Fecha inicio","Estatus","IDC DE CAMPO","Servicio",
            "Tipo de TPV","Comercio","Domicilio","Colonia","Ciudad","ASIGNACION",
            "Estado","C.P.","Solicitó","Teléfono contacto 1","Lada tel","Referencia",
            "Horario","Teléfono temporal 1","Comentarios","DAR","Giro","Tipo de servicio",
            "Cantidad Insumos","Almacén","Celular contacto","Teléfono contacto 2","VIM"
    );

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    // Mostrar formulario subir Excel
    @GetMapping
    public String mostrarUpload(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "upload");
        return "dashboard";
    }

    // Subir archivo y mostrar mapeador
    @PostMapping("/upload")
    public String subirArchivo(@RequestParam("file") MultipartFile file,
                               HttpSession session,
                               Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";
        model.addAttribute("usuario", usuario);

        if (file.isEmpty()) {
            model.addAttribute("mensaje", "Debes seleccionar un archivo Excel.");
            model.addAttribute("contenido", "upload");
            return "dashboard";
        }

        try {
            Path tempPath = Files.createTempFile("excel_", ".xlsx");
            Files.copy(file.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);

            String rutaArchivo = tempPath.toString();

            List<String> columnasExcel = excelService.getColumnNames(new File(rutaArchivo));

            model.addAttribute("columnas", columnasExcel);
            model.addAttribute("columnasSistema", columnasSistema);
            model.addAttribute("rutaArchivo", rutaArchivo);
            model.addAttribute("contenido", "map-columns");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error leyendo el archivo.");
            model.addAttribute("contenido", "upload");
        }

        return "dashboard";
    }

    // Procesar mapeo y mostrar resultados
    @PostMapping("/process")
    public String procesarMappingFinal(@RequestParam Map<String, String> mapping,
                                       @RequestParam("rutaArchivo") String rutaArchivo,
                                       HttpSession session,
                                       Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);

        try {
            // Limpiar mapping
            Map<String, String> mappingLimpio = new LinkedHashMap<>();
            for (String col : columnasSistema) {
                String keyReal = "mapping[" + col + "]";
                String valor = mapping.getOrDefault(keyReal, "").trim();
                mappingLimpio.put(col.trim(), valor);
            }

            // Procesar archivo Excel
            File file = new File(rutaArchivo);
            List<Map<String, String>> datosProcesados = excelService.processExcel(file, mappingLimpio);

            // ============================================================
            // ⭐ FILTRO POR ESTADO (Coordinador y Técnico)
            // ============================================================
         // ⭐ FILTRO POR ESTADO (Coordinador y Técnico)
// ⭐ FILTRO POR ESTADO (Coordinador y Técnico)
if (usuario.getEstado() != null && !usuario.getEstado().isEmpty()) {

    String estadoUsuario = usuario.getEstado().trim();
    String rol = usuario.getRol().toLowerCase();

    System.out.println("===== FILTRO POR ESTADO =====");
    System.out.println("ROL USUARIO: " + rol);
    System.out.println("ESTADO USUARIO: [" + estadoUsuario + "]");

    if (rol.equals("coordinador") || rol.equals("tecnico")) {

        datosProcesados.removeIf(fila -> {

            // Buscar la columna EXACTA "Estado"
            String estadoFila = fila.entrySet().stream()
                    .filter(e -> e.getKey().trim().equalsIgnoreCase("Estado"))
                    .map(e -> e.getValue().trim())
                    .findFirst()
                    .orElse("");

            // LOG por fila
            System.out.println("-----------------------------------");
            System.out.println("Fila estado encontrado: [" + estadoFila + "]");
            System.out.println("¿Coincide? " + estadoUsuario.equalsIgnoreCase(estadoFila));

            boolean eliminar = !estadoUsuario.equalsIgnoreCase(estadoFila);

            System.out.println("¿Eliminar fila? " + eliminar);

            return eliminar;
        });
    }
}



            model.addAttribute("resultado", datosProcesados);
            model.addAttribute("columnasSistema", columnasSistema);
            model.addAttribute("contenido", "result");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error procesando datos.");
            model.addAttribute("contenido", "map-columns");
        }

        return "dashboard";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
