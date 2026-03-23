package com.example.JailQ.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Interfaz gráfica que permite visualizar y eliminar cuentas de tipo POLICIA.
 * 
 * Esta clase realiza peticiones HTTP al backend (Spring Boot) para:
 * <ul>
 *   <li>Obtener la lista de cuentas de policía</li>
 *   <li>Eliminar una cuenta seleccionada</li>
 * </ul>
 * 
 * Utiliza Swing para la interfaz y HttpClient para la comunicación con el servidor.
 */
public class EliminarPoliciaGUI extends JFrame {

    /** Cliente HTTP para comunicarse con el backend */
    private final HttpClient httpClient;

    /** Modelo de datos de la lista de policías */
    private DefaultListModel<String> modeloLista;

    /** Lista visual de policías */
    private JList<String> listaPolicias;

    /** Etiqueta para mostrar estado o mensajes */
    private JLabel lblEstado;

    /**
     * Constructor que inicializa la interfaz gráfica y carga los datos.
     */
    public EliminarPoliciaGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Eliminar cuentas de policía");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Listado de cuentas POLICIA");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaPolicias = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaPolicias);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Selecciona un policía"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnRecargar = new JButton("Recargar");
        btnRecargar.addActionListener(e -> cargarPolicias());

        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.addActionListener(e -> eliminarPoliciaSeleccionado());

        panelBotones.add(btnRecargar);
        panelBotones.add(btnEliminar);

        panelInferior.add(panelBotones, BorderLayout.NORTH);

        lblEstado = new JLabel(" ");
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        panelInferior.add(lblEstado, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        cargarPolicias();
    }

    /**
     * Realiza una petición GET al backend para obtener las cuentas de tipo POLICIA
     * y las muestra en la lista.
     */
    private void cargarPolicias() {
        modeloLista.clear();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/policias"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();

                if (body == null || body.trim().equals("[]")) {
                    lblEstado.setText("No hay cuentas de policía registradas.");
                    return;
                }

                String contenido = body.substring(1, body.length() - 1);
                String[] cuentas = contenido.split("\\},\\{");

                for (String cuenta : cuentas) {
                    String cuentaLimpia = cuenta.replace("{", "").replace("}", "");

                    String id = extraerValor(cuentaLimpia, "idCuentas");
                    String nombre = extraerValor(cuentaLimpia, "nombre");
                    String apellidos = extraerValor(cuentaLimpia, "apellidos");
                    String username = extraerValor(cuentaLimpia, "username");

                    String texto = "ID: " + id + " | " + nombre + " " + apellidos + " | user: " + username;
                    modeloLista.addElement(texto);
                }

                lblEstado.setText("Policías cargados correctamente.");
            } else {
                lblEstado.setText("Error al cargar policías. Código: " + response.statusCode());
                JOptionPane.showMessageDialog(
                        this,
                        "Código: " + response.statusCode() + "\n\n" + response.body(),
                        "Error backend",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (IOException | InterruptedException e) {
            lblEstado.setText("No se pudo conectar con el servidor.");
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar con el servidor.\n\n" + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Elimina la cuenta de policía seleccionada en la lista.
     * 
     * Realiza una petición DELETE al backend con el ID correspondiente.
     */
    private void eliminarPoliciaSeleccionado() {
        String seleccionado = listaPolicias.getSelectedValue();

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta de policía.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = seleccionado.split("\\|")[0].replace("ID:", "").trim();

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres eliminar la cuenta seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/eliminar/policia/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, response.body(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarPolicias();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Código: " + response.statusCode() + "\n\n" + response.body(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Extrae el valor de una clave dentro de un texto JSON simplificado.
     * 
     * @param texto Texto plano que contiene pares clave-valor
     * @param clave Clave que se desea buscar
     * @return Valor asociado a la clave, o cadena vacía si no se encuentra
     */
    private String extraerValor(String texto, String clave) {
        String[] pares = texto.split(",");

        for (String par : pares) {
            String[] partes = par.split(":", 2);
            if (partes.length == 2) {
                String key = partes[0].replace("\"", "").trim();
                String value = partes[1].replace("\"", "").trim();

                if (key.equals(clave)) {
                    return value;
                }
            }
        }
        return "";
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EliminarPoliciaGUI gui = new EliminarPoliciaGUI();
            gui.setVisible(true);
        });
    }
}