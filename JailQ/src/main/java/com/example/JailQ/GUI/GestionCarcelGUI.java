package com.example.JailQ.GUI;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Interfaz Gráfica de Usuario (GUI) para la gestión y creación de nuevas cárceles dentro del sistema JailQ.
 *
 * <p>Esta ventana permite:
 * <ul>
 *     <li>Introducir los datos básicos de una cárcel (nombre, descripción, localidad y capacidad).</li>
 *     <li>Enviar dichos datos al backend mediante una petición HTTP POST.</li>
 *     <li>Visualizar la respuesta del servidor en una consola integrada.</li>
 * </ul>
 *
 * <p>La interfaz está diseñada con Swing y utiliza {@link HttpClient} para la comunicación con el servidor.
 *
 */
public class GestionCarcelGUI extends JFrame {

    private JTextField txtNombre, txtLocalidad, txtCapacidad;
    private JTextArea txtDescripcion;
    private JTextArea txtConsola;
    private final HttpClient httpClient;

    /**
     * Constructor principal de la clase.
     *
     * <p>Configura:
     * <ul>
     *     <li>El cliente HTTP para las peticiones al backend.</li>
     *     <li>Los componentes visuales del formulario.</li>
     *     <li>La consola inferior donde se muestran los resultados.</li>
     * </ul>
     *
     * <p>La ventana se inicializa con un diseño basado en {@link BorderLayout}.
     */
    public GestionCarcelGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Gestión de Cárceles");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel del formulario principal
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Añadir Nueva Cárcel"));

        // Campo: Nombre
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        // Campo: Descripción
        panelFormulario.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextArea(3, 20);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelFormulario.add(scrollDesc);

        // Campo: Localidad
        panelFormulario.add(new JLabel("Localidad:"));
        txtLocalidad = new JTextField();
        panelFormulario.add(txtLocalidad);

        // Campo: Capacidad
        panelFormulario.add(new JLabel("Capacidad:"));
        txtCapacidad = new JTextField();
        panelFormulario.add(txtCapacidad);

        // Botón de envío
        JButton btnAnadir = new JButton("Añadir Cárcel");
        btnAnadir.addActionListener(e -> anadirCarcel());
        panelFormulario.add(new JLabel("")); // celda vacía para alineación
        panelFormulario.add(btnAnadir);

        // Consola inferior
        txtConsola = new JTextArea(10, 30);
        txtConsola.setEditable(false);
        txtConsola.setLineWrap(true);
        txtConsola.setWrapStyleWord(true);

        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Resultado del Servidor"));

        add(panelFormulario, BorderLayout.NORTH);
        add(scrollConsola, BorderLayout.CENTER);
    }

    /**
     * Envía una petición HTTP POST al backend para registrar una nueva cárcel.
     *
     * <p>El proceso incluye:
     * <ol>
     *     <li>Construcción del cuerpo JSON con los datos del formulario.</li>
     *     <li>Envío de la petición a {@code http://localhost:8080/carcel/crear}.</li>
     *     <li>Interpretación del código de estado devuelto por el servidor.</li>
     *     <li>Actualización de la consola con el resultado.</li>
     * </ol>
     *
     * <p>En caso de error, se muestra un mensaje indicando la posible causa.
     */
    private void anadirCarcel() {
        try {
            String jsonBody = String.format(
                    "{\"nombre\":\"%s\", \"descripcion\":\"%s\", \"localidad\":\"%s\", \"capacidad\":%s}",
                    txtNombre.getText(),
                    txtDescripcion.getText(),
                    txtLocalidad.getText(),
                    txtCapacidad.getText()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/carcel/crear"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                txtConsola.setText("ÉXITO (201): Cárcel creada.\n" + response.body());
                limpiarFormulario();
            } else {
                txtConsola.setText("ERROR (" + response.statusCode() + "):\n" + response.body());
            }

        } catch (Exception ex) {
            txtConsola.setText("Excepción: " + ex.getMessage() +
                    "\n¿Está encendido el servidor Spring Boot?");
        }
    }

    /**
     * Restablece todos los campos del formulario a su estado inicial.
     *
     * <p>Se utiliza tras un registro exitoso para permitir introducir una nueva cárcel sin residuos de datos previos.
     */
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtLocalidad.setText("");
        txtCapacidad.setText("");
    }

    /**
     * Punto de entrada principal de la aplicación.
     *
     * <p>Inicia la interfaz en el hilo de eventos de Swing para garantizar la seguridad en la manipulación de componentes gráficos.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionCarcelGUI().setVisible(true));
    }
}