package com.example.sansimap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivo {

    private static final String RUTA = "aulas.txt";

    public static List<DatoAula> leerAulas() {
        List<DatoAula> lista = new ArrayList<>();

        File archivo = new File(RUTA);
        if (!archivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            DatoAula actual = null;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.equals("#AULA")) {
                    actual = new DatoAula();

                } else if (linea.equals("#FIN") && actual != null) {
                    lista.add(actual);
                    actual = null;

                } else if (actual != null && linea.contains(":")) {
                    String clave = linea.substring(0, linea.indexOf(":")).trim();
                    String valor = linea.substring(linea.indexOf(":") + 1).trim();

                    switch (clave) {
                        case "codigo"        -> actual.codigo        = valor;
                        case "ubicacion"     -> actual.ubicacion     = valor;
                        case "piso"          -> actual.piso          = valor;
                        case "edificio"      -> actual.edificio      = valor.isEmpty() ? null : valor;
                        case "fotos"         -> actual.fotos         = valor.isEmpty() ? new String[0] : valor.split(",");
                        case "ubicacionfoto" -> actual.ubicacionfoto = valor.isEmpty() ? null : valor; // ← nuevo
                        case "horarios"      -> actual.horarios      = valor.isEmpty() ? new String[0] : valor.split(",");
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

            if (aula.codigo.toLowerCase().startsWith(codigo.toLowerCase())) return aula;
        }
        return null;
    }

    public static void crearArchivoSiNoExiste() {
        File archivo = new File(RUTA);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
                System.out.println("Archivo aulas.txt creado en: " + archivo.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
