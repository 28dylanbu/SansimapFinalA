package com.example.sansimap;

import java.io.*;
import java.util.Scanner;

public class ManejadorUsuarios {

    private static final String ARCHIVO_USUARIOS = "usuarios_registrados.txt";

    // MÉTODO 1: Guardar un nuevo usuario (Registro)
    public static void guardarUsuario(String nombre, String email, String password) {
        try (FileWriter fw = new FileWriter(ARCHIVO_USUARIOS, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Guardamos el formato: nombre|email|password
            out.println(nombre + "|" + email + "|" + password);
            System.out.println("Usuario guardado con éxito.");

        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    // MÉTODO 2: Buscar y validar (Inicio de Sesión)
    public static boolean validarCredenciales(String identificador, String password) {
        File file = new File(ARCHIVO_USUARIOS);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split("\\|"); // Dividimos por el símbolo |

                if (partes.length == 3) {
                    String nombreTxt = partes[0];
                    String emailTxt = partes[1];
                    String passTxt = partes[2];

                    // Comparamos si el identificador coincide con nombre o email
                    if ((identificador.equals(nombreTxt) || identificador.equals(emailTxt))
                            && password.equals(passTxt)) {
                        return true; // ¡Coincidencia encontrada!
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado.");
        }
        return false; // No se encontró el usuario o la contraseña es incorrecta
    }
    public static boolean usuarioYaExiste(String nombreBuscado, String emailBuscado) {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return false;

        try (Scanner scanner = new Scanner(archivo)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split("\\|");

                if (partes.length == 3) {
                    String nombreTxt = partes[0];
                    String emailTxt = partes[1];

                    // Comprobamos si el nombre O el email ya existen (ignorando mayúsculas)
                    if (nombreTxt.equalsIgnoreCase(nombreBuscado.trim()) ||
                            emailTxt.equalsIgnoreCase(emailBuscado.trim())) {
                        return true; // Se encontró una coincidencia, el usuario no es único
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return false; // No hay coincidencias, el usuario puede registrarse
    }

    public static boolean validarEmail(String email)  {
        File archivo = new File(ARCHIVO_USUARIOS);
        if(!archivo.exists()){
            return false;
        }

        try (Scanner scanner = new Scanner(archivo)){
            while (scanner.hasNextLine()){
                String linea = scanner.nextLine();
                String[] partes = linea.split("\\|");
                if(partes.length>=2){
                    if(partes[1].equals(email)){
                        return true;
                    }

                }

            }
        }catch (FileNotFoundException e){
            System.err.println("Not found file");
        }
        return false;
    }
    public static boolean actualizarPassword(String emailBuscado, String nuevaPassword) {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return false;

        // Usaremos una lista para guardar todas las líneas temporalmente
        java.util.List<String> lineasActualizadas = new java.util.ArrayList<>();
        boolean encontrado = false;

        try (Scanner scanner = new Scanner(archivo)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split("\\|");

                if (partes.length == 3 && partes[1].equalsIgnoreCase(emailBuscado.trim())) {
                    // ¡Encontramos al usuario! Creamos la línea con la nueva contraseña
                    String nombre = partes[0];
                    String email = partes[1];
                    lineasActualizadas.add(nombre + "|" + email + "|" + nuevaPassword);
                    encontrado = true;
                } else {
                    // Si no es el usuario, guardamos la línea tal cual estaba
                    lineasActualizadas.add(linea);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return false;
        }

        // Si lo encontramos, sobreescribimos el archivo con la lista actualizada
        if (encontrado) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo, false))) { // false para sobreescribir
                for (String l : lineasActualizadas) {
                    writer.println(l);
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo: " + e.getMessage());
            }
        }

        return false;
    }
}