package com.example.sansimap; // Paquete principal del proyecto SansiMap

import java.time.LocalDateTime; // Manejo de fechas y horas
import java.time.temporal.ChronoUnit; // Permite calcular tiempo transcurrido
import java.util.Properties; // Configuración SMTP
import java.util.Random; // Generador de números aleatorios

import javax.mail.*; // Librerías de correo
import javax.mail.internet.*; // Manejo de correos y direcciones

// Clase encargada de enviar y validar códigos por correo
public class ServicioCorreo {

    // Correo oficial del sistema
    private static final String MI_CORREO =
            "sansimap.umss@gmail.com";

    // Contraseña de aplicación de Gmail
    private static final String MYPASSWORD =
            "zphu ittv zepc uewd";

    // Código generado actualmente
    private static String codigoActual = "";

    // Define si el código es para registro
    private static boolean codigoCorreo = false;

    // Define si el código es para recuperación de contraseña
    private static boolean codigoPassword = false;

    // Guarda la hora exacta de generación del código
    public static LocalDateTime tiempoGeneracion;

    // Tiempo máximo permitido del código en minutos
    private static final int MINUTOS_VIGENCIA = 5;

    // Genera un código aleatorio de 6 dígitos
    public static String generarCodigo() {

        Random rnd = new Random();

        // CAMBIO IMPORTANTE:
        // 1000000 permite generar hasta 999999 correctamente
        int numero = rnd.nextInt(1000000);

        return String.format("%06d", numero);
    }

    // Envía un correo de verificación
    public static boolean enviarCorreoVerificacion(
            String correoDestino
    ) {

        // Genera nuevo código
        codigoActual = generarCodigo();

        // Guarda hora actual
        tiempoGeneracion = LocalDateTime.now();

        String motivoCorreo;

        String contenido;

        // Configuración SMTP de Gmail
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.host", "smtp.gmail.com");

        props.put("mail.smtp.port", "587");

        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Crea sesión autenticada
        Session session = Session.getInstance(
                props,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                MI_CORREO,
                                MYPASSWORD
                        );
                    }
                }
        );

        // Configura correo de registro
        if (codigoCorreo) {

            motivoCorreo =
                    "Código de Verificación - SANSI MAP";

            contenido =
                    "<h2>Bienvenido a Sansi Map</h2>"
                            + "<p>Tu código de verificación es:</p>"
                            + "<h1 style='color:#e3000f;'>"
                            + codigoActual
                            + "</h1>"
                            + "<p>Este código expira en 5 minutos.</p>";

        }

        // Configura correo de recuperación
        else if (codigoPassword) {

            motivoCorreo =
                    "Código de recuperación - SANSI MAP";

            contenido =
                    "<h2>Recuperación de contraseña</h2>"
                            + "<p>Tu código es:</p>"
                            + "<h1 style='color:#e3000f;'>"
                            + codigoActual
                            + "</h1>"
                            + "<p>Este código expira en 5 minutos.</p>";

        }

        // Si no hay tipo definido
        else {

            return false;
        }

        try {

            // Crea mensaje
            Message message = new MimeMessage(session);

            // Define remitente
            message.setFrom(
                    new InternetAddress(MI_CORREO)
            );

            // Define destinatario
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(correoDestino)
            );

            // Define asunto
            message.setSubject(motivoCorreo);

            // Define contenido HTML
            message.setContent(
                    contenido,
                    "text/html; charset=utf-8"
            );

            System.out.println(
                    "Enviando correo a "
                            + correoDestino
                            + "..."
            );

            // Envía correo
            Transport.send(message);

            System.out.println(
                    "¡Correo enviado con éxito!"
            );

            return true;

        } catch (MessagingException e) {

            // Muestra error en consola
            System.err.println(
                    "Error al enviar correo: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    // Valida si el código ingresado es correcto
    // 1 = correcto
    // 0 = incorrecto
    // -1 = expirado
    public static int validarCodigo(
            String codigoIngresado
    ) {

        // Evita errores si no existe código generado
        if (codigoActual == null
                || codigoActual.isEmpty()) {

            return 0;
        }

        // Verifica coincidencia
        if (!codigoActual.equals(codigoIngresado)) {

            return 0;
        }

        // Calcula tiempo transcurrido
        long minutosTranscurridos =
                ChronoUnit.MINUTES.between(
                        tiempoGeneracion,
                        LocalDateTime.now()
                );

        // Verifica expiración
        if (minutosTranscurridos >= MINUTOS_VIGENCIA) {

            codigoActual = "";

            return -1;
        }

        return 1;
    }

    // Devuelve el código actual
    public static String getCodigoActual() {

        return codigoActual;
    }

    // Activa o desactiva modo registro
    public static void setCodigoCorreo(
            boolean cambioDeEstado
    ) {

        codigoCorreo = cambioDeEstado;
    }

    // Activa o desactiva modo recuperación
    public static void setCodigoPassword(
            boolean cambioDeEstado
    ) {

        codigoPassword = cambioDeEstado;
    }
}