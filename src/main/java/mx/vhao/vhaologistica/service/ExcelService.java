package mx.vhao.vhaologistica.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.DataFormatter;
@Service
public class ExcelService {

    // =========================================================
    // 1️⃣ Obtener columnas del Excel desde MultipartFile
    // =========================================================
    public List<String> getColumnNames(MultipartFile file) throws Exception {
        return getColumnNames(file.getInputStream());
    }

    public List<String> getColumnNames(File file) throws Exception {
        return getColumnNames(new FileInputStream(file));
    }

    // =========================================================
    // 2️⃣ Método central para obtener nombres de columnas (CON TRIM)
    // =========================================================
    private List<String> getColumnNames(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        List<String> columnas = new ArrayList<>();

        for (Cell celda : headerRow) {
            if (celda != null) {
                String nombre = celda.getStringCellValue();
                if (nombre != null) nombre = nombre.trim();
                columnas.add(nombre);
                System.out.println(">> Columna RAW NORMALIZADA: [" + nombre + "]");
            }
        }

        workbook.close();
        return columnas;
    }

    // =========================================================
    // 3️⃣ Procesar Excel desde archivo / Multipart
    // =========================================================
    public List<Map<String, String>> processExcel(MultipartFile file,
                                                  Map<String, String> mapping) throws Exception {
        return processExcel(file.getInputStream(), mapping);
    }

    public List<Map<String, String>> processExcel(File file,
                                                  Map<String, String> mapping) throws Exception {
        return processExcel(new FileInputStream(file), mapping);
    }

    // =========================================================
    // 4️⃣ Procesar Excel (CON NORMALIZACIÓN DE CLAVES + LOGS)
    // =========================================================
    private List<Map<String, String>> processExcel(InputStream inputStream,
                                                   Map<String, String> mapping) throws Exception {

        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Leer cabeceras normalizadas
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> indexExcel = new HashMap<>();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell celda = headerRow.getCell(i);
            if (celda == null) continue;
            String colName = celda.getStringCellValue();
            if (colName != null) colName = colName.trim();
            indexExcel.put(colName, i);
        }

        System.out.println("[INFO] Columnas detectadas en Excel: " + indexExcel.keySet());

        // Validar mapeo y reemplazar null por "-"
        Map<String, String> mappingLimpio = new LinkedHashMap<>();
        for (String colSistema : mapping.keySet()) {
            String colExcel = mapping.get(colSistema);
            mappingLimpio.put(colSistema, colExcel != null ? colExcel.trim() : "");
        }

        System.out.println("[INFO] Mapeo limpio FINAL: " + mappingLimpio);

        List<Map<String, String>> resultado = new ArrayList<>();

        // Procesar filas
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            Map<String, String> fila = new LinkedHashMap<>();

            for (String colSistema : mappingLimpio.keySet()) {
                String colExcel = mappingLimpio.get(colSistema);
                String value = "-";
                if (colExcel != null && !colExcel.isEmpty()) {
                    Integer index = indexExcel.get(colExcel);
                    if (index != null) {
                        value = getCellValue(row.getCell(index));
                    }
                }
                fila.put(colSistema, value);
            }

            resultado.add(fila);

            if (r <= 5) { // Mostrar solo primeras filas
                System.out.println("[DEBUG] Fila " + r + ": " + fila);
            }
        }

        workbook.close();
        System.out.println("[INFO] Total filas procesadas: " + resultado.size());

        return resultado;
    }

    // =========================================================
    // 5️⃣ Convertir una celda a String
    // =========================================================
    private String getCellValue(Cell celda) {
        if (celda == null) return "-";

        return switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue().trim();
          case NUMERIC -> {
    if (DateUtil.isCellDateFormatted(celda)) {
        yield celda.getLocalDateTimeCellValue().toLocalDate().toString();
    }
    // CORRECCIÓN: Usar DataFormatter para evitar notación científica (3.4E7)
    DataFormatter dataFormatter = new DataFormatter();
    yield dataFormatter.formatCellValue(celda);
}
            case BOOLEAN -> String.valueOf(celda.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield celda.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(celda.getNumericCellValue());
                }
            }
            default -> "-";
        };
    }
}
