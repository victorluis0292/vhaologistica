package mx.vhao.vhaologistica.controller;

import jakarta.servlet.http.HttpSession;
import mx.vhao.vhaologistica.model.Usuario;
import mx.vhao.vhaologistica.service.ExcelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard/excel")
public class ExcelController {

    private final ExcelService excelService;

    // Columnas predefinidas del sistema
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

    // GET: formulario inicial de subir archivo
    @GetMapping
    public String mostrarUpload(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "upload"); // carga fragmento upload
        return "dashboard";
    }

    // POST: subir archivo Excel
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
            String rutaArchivo = excelService.guardarArchivo(file);
            List<String> columnas = excelService.obtenerColumnas(new FileInputStream(rutaArchivo));

            model.addAttribute("columnas", columnas);
            model.addAttribute("columnasSistema", columnasSistema);
            model.addAttribute("rutaArchivo", rutaArchivo);
            model.addAttribute("contenido", "map-columns");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error procesando el archivo.");
            model.addAttribute("contenido", "upload");
        }

        return "dashboard";
    }

    // POST: procesar mapeo final
    @PostMapping("/process")
    public String procesarMappingFinal(@RequestParam Map<String, String> mapping,
                                       @RequestParam("rutaArchivo") String rutaArchivo,
                                       HttpSession session,
                                       Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/?error=true";

        model.addAttribute("usuario", usuario);

        try {
            List<Map<String, String>> datosProcesados =
                    excelService.procesarDatos(new FileInputStream(rutaArchivo), mapping);

            model.addAttribute("resultado", datosProcesados);
            model.addAttribute("contenido", "result");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error procesando datos del archivo.");
            model.addAttribute("contenido", "upload");
        }

        return "dashboard";
    }
}
