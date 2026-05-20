package com.example.sansimap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivo {

    /**
     * Lee el archivo "aulas.txt" y convierte su contenido en una lista de objetos DatoAula.
     * Utiliza lógica secuencial básica para analizar el texto línea por línea.
     */
    public static List<DatoAula> leerAulas() {
        // Creamos una lista vacía para almacenar los objetos DatoAula que vayamos construyendo.
        List<DatoAula> lista = new ArrayList<>();

        // Obtenemos el archivo de texto desde la carpeta de recursos (resources).
        // Se usa getResourceAsStream porque es la forma correcta de leer archivos integrados
        // cuando el proyecto JavaFX se empaqueta en un .jar.
        InputStream is = GestorArchivo.class.getResourceAsStream("/aulas.txt");

        // Verificamos si el archivo se cargó correctamente. Si no, mostramos error y devolvemos la lista vacía.
        if (is == null) {
            System.err.println("❌ No se encontró aulas.txt en resources.");
            return lista;
        }

        // Usamos un BufferedReader para leer el archivo de forma eficiente (línea a línea).
        // Se especifica la codificación "UTF-8" para no tener problemas con tildes o caracteres especiales.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String linea;
            DatoAula actual = null; // Variable temporal para ir armando el objeto del aula actual

            // Leemos el archivo hasta que el resultado sea null (fin del archivo)
            while ((linea = br.readLine()) != null) {

                // Limpieza de la línea:
                // replace("\uFEFF", "") elimina el caracter BOM (marca de orden de bytes) que a veces Windows añade al inicio de los txt.
                // trim() quita los espacios en blanco al principio y al final.
                linea = linea.replace("\uFEFF", "").trim();

                // Si la línea quedó vacía después de limpiarla, la ignoramos y pasamos a la siguiente.
                if (linea.isEmpty()) continue;

                // Identificamos el inicio de un bloque de datos para una nueva aula.
                if (linea.equals("#AULA")) {
                    actual = new DatoAula(); // Instanciamos el objeto para empezar a llenarlo.
                }
                // Identificamos el final del bloque. Si el objeto fue instanciado, lo guardamos en la lista.
                else if (linea.equals("#FIN") && actual != null) {
                    lista.add(actual);
                    actual = null; // Reiniciamos la variable para que esté lista para el próximo bloque #AULA.
                }
                // Si estamos dentro de un bloque de aula (actual != null) y la línea contiene ":"
                else if (actual != null && linea.contains(":")) {

                    int sep = linea.indexOf(":"); // Buscamos en qué posición están los dos puntos.

                    // Extraemos la "clave" (lado izquierdo de los :), le quitamos espacios y la pasamos a minúsculas.
                    String clave = linea.substring(0, sep).trim().toLowerCase();
                    // Extraemos el "valor" (lado derecho de los :), le quitamos espacios.
                    String valor = linea.substring(sep + 1).trim();

                    // Usamos un switch para asignar el valor extraído al atributo correcto del objeto DatoAula.
                    switch (clave) {
                        case "codigo": actual.codigo = valor; break;
                        case "ubicacion": actual.ubicacion = valor; break;
                        case "piso": actual.piso = valor; break;

                        // Operador ternario: si el valor está vacío, guarda null; si no, guarda el valor.
                        case "edificio": actual.edificio = valor.isEmpty() ? null : valor; break;

                        // split(",") divide el texto en un arreglo de Strings usando la coma como separador.
                        // Si está vacío, crea un arreglo de tamaño cero para evitar errores NullPointerException.
                        case "fotos": actual.fotos = valor.isEmpty() ? new String[0] : valor.split(","); break;

                        case "ubicacionfoto": actual.ubicacionfoto = valor; break;
                        case "horarios": actual.horarios = valor.split(","); break;
                    }
                }
            }
        } catch (Exception e) {
            // Si ocurre cualquier error de entrada/salida (ej. disco lleno o archivo corrupto), se imprime la traza.
            e.printStackTrace();
        }

        // Retornamos la colección completa con todos los datos extraídos.
        return lista;
    }

    /**
     * Busca un aula en específico dentro de la lista de aulas leyendo el código.
     * * @param codigo El identificador del aula a buscar (ej. "691B").
     * @return El objeto DatoAula correspondiente si lo encuentra, o null si no existe.
     */
    public static DatoAula buscarAula(String codigo) {
        // Llama a leerAulas() para obtener la lista e itera sobre cada aula (búsqueda lineal).
        for (DatoAula aula : leerAulas()) {
            // equalsIgnoreCase compara los textos sin importar si el usuario escribió en mayúsculas o minúsculas.
            if (aula.codigo.equalsIgnoreCase(codigo)) return aula;
        }
        // Si el ciclo termina y no se encontró el código, retorna null indicando que no existe.
        return null;
    }

    /**
     * Verifica si el archivo base "aulas.txt" existe en el entorno de desarrollo.
     * Si no existe, crea las carpetas necesarias y un archivo en blanco.
     */
    public static void crearArchivoSiNoExiste() {
        // Crea un objeto File apuntando a la ruta física dentro de la estructura del proyecto.
        File archivo = new File("src/main/resources/aulas.txt");

        // Si el archivo físico no existe en el disco duro de la computadora...
        if (!archivo.exists()) {
            try {
                // getParentFile().mkdirs() se asegura de crear todas las subcarpetas
                // en la ruta ("src", "main", "resources") si es que no estuvieran creadas.
                archivo.getParentFile().mkdirs();

                // createNewFile() intenta crear el documento .txt vacío.
                if (archivo.createNewFile()) {
                    System.out.println("✅ Archivo aulas.txt creado en: " + archivo.getAbsolutePath());
                }
            } catch (IOException e) {
                // Captura excepciones si no hay permisos de escritura en la carpeta del proyecto.
                System.err.println("❌ No se pudo crear el archivo: " + e.getMessage());
            }
        } else {
            // Si el archivo ya estaba allí, solo informa que todo está en orden.
            System.out.println("ℹ️ El archivo aulas.txt ya existe.");
        }
    }
}