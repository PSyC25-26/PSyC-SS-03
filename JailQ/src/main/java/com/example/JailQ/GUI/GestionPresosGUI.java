package com.example.JailQ.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Interfaz Gráfica de Usuario (GUI) para la gestión y registro de presos en el sistema JailQ.
 * <p>
 * Esta clase proporciona un formulario basado en Swing que permite:
 * <ul>
 * <li>Introducir datos personales del preso (nombre, apellidos).</li>
 * <li>Especificar fechas clave mediante formato ISO (AAAA-MM-DD).</li>
 * <li>Definir la condena en años.</li>
 * <li>Enviar la información mediante una petición HTTP POST al backend.</li>
 * </ul>
 * * @author Adrián Baz/Grupo 3
 * @version 1.0
 */
public class GestionPresosGUI extends JFrame {

    private JTextField txtNombre, txtApellidos, txtFechaNacimiento, txtCondena, txtFechaIngreso;
    private JTextArea txtConsola;
    private final HttpClient httpClient;

    /**
     * Constructor de la clase; Inicializa el cliente HTTP y configura todos los componentes
     * visuales de la interfaz, incluyendo:
     * <ul>
     * <li>El panel de formulario con celdas personalizadas.</li>
     * <li>El botón de registro con su lógica de envío.</li>
     * <li>La consola inferior para la visualización de respuestas del servidor.</li>
     * </ul>
     * Nota: la interfaz no está implementada con el backend, lo que causa errores lógicos al intentar añadir cualquier preso.
     */
    public GestionPresosGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Gestión de Presos");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL SUPERIOR: Formulario ---
        JPanel panelFormulario = new JPanel(new GridLayout(5, 1, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Preso"));

        txtNombre = new JTextField();
        txtApellidos = new JTextField();
        txtFechaNacimiento = new JTextField();
        txtCondena = new JTextField();
        txtFechaIngreso = new JTextField();

        panelFormulario.add(crearCelda("Nombre:", txtNombre));
        panelFormulario.add(crearCelda("Apellidos:", txtApellidos));
        panelFormulario.add(crearCelda("Fecha Nacimiento (AAAA-MM-DD):", txtFechaNacimiento));
        panelFormulario.add(crearCelda("Condena (años):", txtCondena));
        panelFormulario.add(crearCelda("Fecha Ingreso (AAAA-MM-DD):", txtFechaIngreso));

        // --- BOTÓN REGISTRAR ---
        JButton btnAnadir = new JButton("Registrar Preso");
        btnAnadir.addActionListener(e -> enviarPreso());

        // --- PANEL INFERIOR: Consola ---
        txtConsola = new JTextArea(12, 30); 
        txtConsola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Estado del Servidor"));

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelFormulario, BorderLayout.CENTER);
        panelCentral.add(btnAnadir, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.NORTH);
        add(scrollConsola, BorderLayout.SOUTH); 
    }

    /**
     * Recopila los datos del formulario y realiza una petición POST asíncrona al servidor.
     * <p>
     * El proceso sigue estos pasos:
     * <ol>
     * <li>Valida el formato de las fechas introducidas.</li>
     * <li>Construye un cuerpo JSON con los datos del preso.</li>
     * <li>Envía la petición a {@code http://localhost:8080/presos/crear}.</li>
     * <li>Muestra el resultado (éxito o error) en la consola de la GUI.</li>
     * </ol>
     * * @throws DateTimeParseException Si el formato de las fechas no es AAAA-MM-DD.
     * @throws Exception Para cualquier otro error durante la comunicación HTTP.
     */
    private void enviarPreso() {
        try {
            String fechaNac = txtFechaNacimiento.getText();
            String fechaIng = txtFechaIngreso.getText();
            
            // Validación previa en cliente
            LocalDate.parse(fechaNac); 
            LocalDate.parse(fechaIng);

            String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"apellidos\":\"%s\", \"fechaNacimiento\":\"%s\", \"condena\":%s, \"fechaIngreso\":\"%s\"}",
                txtNombre.getText(),
                txtApellidos.getText(),
                fechaNac,
                txtCondena.getText().replace(",", "."), 
                fechaIng
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/presos/crear")) 
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                txtConsola.setText("✅ ÉXITO: Preso registrado correctamente.\n" + response.body());
                limpiarFormulario();
            } else {
                txtConsola.setText("❌ ERROR (" + response.statusCode() + "):\n" + response.body());
            }

        } catch (DateTimeParseException ex) {
            txtConsola.setText("⚠️ Formato de fecha incorrecto. Usa AAAA-MM-DD.");
        } catch (Exception ex) {
            txtConsola.setText("🚨 Excepción: " + ex.getMessage());
        }
    }

    /**
     * Restablece todos los campos del formulario a su estado inicial.
     * <p>
     * Limpia los campos de texto y establece la fecha de ingreso a la fecha actual del sistema.
     */
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtFechaNacimiento.setText("");
        txtCondena.setText("");
        txtFechaIngreso.setText(LocalDate.now().toString());
    }

    /**
     * Crea un panel contenedor para un campo de entrada con un diseño estandarizado.
     * <p>
     * La celda consta de una etiqueta sobre un campo de texto, con bordes definidos 
     * para mejorar la legibilidad del formulario.
     * * @param etiqueta El texto descriptivo que aparecerá sobre el campo.
     * @param campo El componente {@link JTextField} donde el usuario escribe.
     * @return Un objeto {@link JPanel} configurado con el diseño de celda.
     */
    private JPanel crearCelda(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel(new GridLayout(2, 1)); 
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
        return panel;
    }

    /**
     * Punto de entrada principal de la aplicación GUI.
     * <p>
     * Inicia la interfaz de usuario en el hilo de despacho de eventos de Swing 
     * para garantizar la seguridad de los hilos.
     * * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionPresosGUI().setVisible(true));
    }
}