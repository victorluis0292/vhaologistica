package mx.vhao.vhaologistica.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ExcelService {

    private final String carpetaUpload = "C:/VHAOLOGISTICA/uploads/";

    public String guardarArchivo(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IOException("Archivo vacío");

        File directorio = new File(carpetaUpload);
        if (!directorio.exists() && !directorio.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + carpetaUpload);
        }

        String rutaArchivo = carpetaUpload + file.getOriginalFilename();
        File destino = new File(rutaArchivo);
        file.transferTo(destino);

        return rutaArchivo;
    }

    public List<String> obtenerColumnas(InputStream is) throws Exception {
        List<String> columnas = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row filaCabecera = sheet.getRow(0);
            if (filaCabecera == null) throw new Exception("Archivo Excel vacío");

            for (Cell cell : filaCabecera) columnas.add(cell.getStringCellValue());
        }
        return columnas;
    }

    public List<Map<String, String>> procesarDatos(InputStream is, Map<String, String> mapping) throws Exception {
        List<Map<String, String>> resultado = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row filaCabecera = sheet.getRow(0);
            if (filaCabecera == null) return resultado;

            List<String> cabecera = new ArrayList<>();
            for (Cell cell : filaCabecera) cabecera.add(cell.getStringCellValue());

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row fila = sheet.getRow(i);
                if (fila == null) continue;

                Map<String, String> filaMap = new LinkedHashMap<>();
                for (int j = 0; j < cabecera.size(); j++) {
                    String nombreColumna = cabecera.get(j);
                    String asignacion = mapping.get(nombreColumna);

                    Cell cell = fila.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    if(asignacion != null && !asignacion.isEmpty())
                        filaMap.put(asignacion, cell.getStringCellValue());
                }
                resultado.add(filaMap);
            }
        }
        return resultado;
    }
}
