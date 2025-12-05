package mx.vhao.vhaologistica.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin("*")
public class CatalogoEstadosMexicoController {

    @GetMapping("/estados-mexico")
    public List<String> obtenerEstadosDeMexico() {
        return List.of(
                "Aguascalientes", "Baja California", "Baja California Sur", "Campeche",
                "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima",
                "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo",
                "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León",
                "Oaxaca", "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí",
                "Sinaloa", "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala",
                "Veracruz", "Yucatán", "Zacatecas"
        );
    }
}
