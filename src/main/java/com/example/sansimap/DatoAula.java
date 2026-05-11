package com.example.sansimap; // Paquete que organiza las clases del proyecto SansiMap

// Clase que almacena los datos e información de un aula
public class DatoAula {

    public String codigo = ""; // Código o nombre del aula

    public String ubicacion = ""; // Ubicación general del aula

    public String piso = ""; // Piso donde se encuentra el aula

    public String edificio = null; // Nombre del edificio

    public String[] fotos = new String[0]; // Arreglo de fotos del aula

    public String ubicacionfoto = null; // Imagen o mapa de ubicación del aula

    public String[] horarios = new String[0]; // Horarios del aula
}