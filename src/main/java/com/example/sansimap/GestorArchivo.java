package com.example.sansimap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivo {

    public static List<DatoAula> leerAulas() {
        List<DatoAula> lista = new ArrayList<>();
        InputStream is = GestorArchivo.class.getResourceAsStream("/aulas.txt");

        if (is == null) {
            System.err.println("❌ No se encontró aulas.txt en resources.");
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String linea;
            DatoAula actual = null;
            while ((linea = br.readLine()) != null) {
                linea = linea.replace("\uFEFF", "").trim();
                if (linea.isEmpty()) continue;
                if (linea.equals("#AULA")) {
                    actual = new DatoAula();
                } else if (linea.equals("#FIN") && actual != null) {
                    lista.add(actual);
                    actual = null;
                } else if (actual != null && linea.contains(":")) {
                    int sep = linea.indexOf(":");
                    String clave = linea.substring(0, sep).trim().toLowerCase();
                    String valor = linea.substring(sep + 1).trim();
                    switch (clave) {
                        case "codigo": actual.codigo = valor; break;
                        case "ubicacion": actual.ubicacion = valor; break;
                        case "piso": actual.piso = valor; break;
                        case "edificio": actual.edificio = valor.isEmpty() ? null : valor;break;
                        case "fotos": actual.fotos = valor.isEmpty() ? new String[0] : valor.split(","); break;
                        case "ubicacionfoto": actual.ubicacionfoto = valor; break;
                        case "horarios": actual.horarios = valor.split(","); break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static DatoAula buscarAula(String codigo) {
        for (DatoAula aula : leerAulas()) {
            if (aula.codigo.equalsIgnoreCase(codigo)) return aula;
        }
        return null;
    }

    // EL MÉTODO QUE FALTABA
    public static void crearArchivoSiNoExiste() {
        // Intentamos crearlo en la ruta de recursos de desarrollo
        File archivo = new File("src/main/resources/aulas.txt");
        if (!archivo.exists()) {
            try {
                // Crear carpetas si no existen
                archivo.getParentFile().mkdirs();
                if (archivo.createNewFile()) {
                    System.out.println("✅ Archivo aulas.txt creado en: " + archivo.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("❌ No se pudo crear el archivo: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ El archivo aulas.txt ya existe.");
        }
    }
}