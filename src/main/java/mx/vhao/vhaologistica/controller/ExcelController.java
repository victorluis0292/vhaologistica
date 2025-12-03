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
            // Guardar archivo temporal
            Path tempPath = Files.createTempFile("excel_", ".xlsx");
            Files.copy(file.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);
            String rutaArchivo = tempPath.toString();
            System.out.println("[INFO] Archivo guardado temporalmente en: " + rutaArchivo);

            // Obtener columnas del Excel
            List<String> columnasExcel = excelService.getColumnNames(new File(rutaArchivo));
            System.out.println("[INFO] Columnas detectadas en Excel: " + columnasExcel);

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

    // Procesar mapeado y mostrar resultado
    @PostMapping("/process")
    public String procesarMappingFinal(@RequestParam Map<String, String> mapping,
                                       @RequestParam("rutaArchivo") String rutaArchivo,
                                       HttpSession session,
                                       Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);

        try {
            // Limpiar mapping y hacer trim
            Map<String, String> mappingLimpio = new LinkedHashMap<>();
            for (String col : columnasSistema) {
                String keyReal = "mapping[" + col + "]";
                String valor = mapping.getOrDefault(keyReal, "").trim();
                mappingLimpio.put(col.trim(), valor);
            }

            System.out.println("[INFO] Mapeo limpio FINAL: " + mappingLimpio);

            // Procesar archivo (ExcelService pone "-" si no hay mapeo)
            File file = new File(rutaArchivo);
            List<Map<String, String>> datosProcesados = excelService.processExcel(file, mappingLimpio);

            // --- FILTRAR POR ESTADO si el usuario es coordinador ---
            if ("coordinador".equalsIgnoreCase(usuario.getRol()) && usuario.getEstado() != null) {
                String estadoCoord = usuario.getEstado().trim();
                datosProcesados.removeIf(fila -> {
                    String estadoFila = fila.getOrDefault("Estado", "").trim();
                    return !estadoCoord.equalsIgnoreCase(estadoFila);
                });
            }

            System.out.println("[INFO] Total filas procesadas tras filtro: " + datosProcesados.size());

            model.addAttribute("resultado", datosProcesados);
            model.addAttribute("columnasSistema", this.columnasSistema);
            model.addAttribute("contenido", "result");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error procesando datos.");
            model.addAttribute("contenido", "map-columns");
        }

        return "dashboard";
    }

      // --- Logout ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("[INFO] Usuario cerró sesión correctamente.");
        return "redirect:/"; // redirige a página principal o login
    }
}
