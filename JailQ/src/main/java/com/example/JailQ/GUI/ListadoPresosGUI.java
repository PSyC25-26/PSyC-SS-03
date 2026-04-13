package com.example.JailQ.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ListadoPresosGUI extends JFrame {

    private JTable tablaPresos;
    private DefaultTableModel modelo;
    private final HttpClient httpClient;
    private final String BASE_URL = "http://localhost:8080/preso";

    public ListadoPresosGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Listado de Presos");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Configuración de la tabla
        String[] columnas = {"ID", "Nombre", "Apellidos", "Condena", "Delito"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que el usuario edite la tabla directamente
            }
        };
        tablaPresos = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaPresos);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnActualizar = new JButton("Actualizar Lista");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(255, 100, 100)); // Un tono rojo suave

        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        // Eventos
        btnActualizar.addActionListener(e -> cargarPresos());
        btnEliminar.addActionListener(e -> eliminarPresoSeleccionado());

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Carga inicial
        cargarPresos();
    }

    /**
     * Realiza una petición GET al servidor para obtener el JSON de presos.
     */
    private void cargarPresos() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/todos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                actualizarTabla(response.body());
            } else {
                JOptionPane.showMessageDialog(this, "Error al obtener presos: " + response.statusCode());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }

    /**
     * Procesa el JSON recibido y rellena el modelo de la tabla.
     * Nota: Se usa Regex simple para no añadir dependencias externas de JSON.
     */
private void actualizarTabla(String json) {
    modelo.setRowCount(0); // Limpiar tabla

    //Separamos el JSON en bloques de presos (cada bloque empieza con { y termina con })
    Pattern presoPattern = Pattern.compile("\\{(.*?)\\}");
    Matcher presoMatcher = presoPattern.matcher(json);

    while (presoMatcher.find()) {
        String datosPreso = presoMatcher.group(1);


    String id = extraerCampo(datosPreso, "id(?:Preso)?");
    String nombre = extraerCampo(datosPreso, "nombre");
    String apellidos = extraerCampo(datosPreso, "apellidos");
    String condena = extraerCampo(datosPreso, "condena");

    String delito = "N/A";
    Pattern pDelito = Pattern.compile("\"delitos\"\\s*:\\s*\\[\\s*\"(.*?)\"");
    Matcher mDelito = pDelito.matcher(datosPreso);
    if (mDelito.find()) {
        delito = mDelito.group(1);
    }

    if (nombre != null) {
        modelo.addRow(new Object[]{id, nombre, apellidos, condena, delito});
    }
    }
}

/**
 * Método auxiliar para buscar un campo específico dentro de un fragmento de JSON
 */
private String extraerCampo(String texto, String nombreCampo) {
    Pattern p = Pattern.compile("\"" + nombreCampo + "\":\"?(.*?)\"?[,}]");
    Matcher m = p.matcher(texto);
    if (m.find()) {
        return m.group(1);
    }
    return "N/A";
}

    /**
     * Obtiene el ID de la fila seleccionada y envía una petición DELETE.
     */
    private void eliminarPresoSeleccionado() {
        int fila = tablaPresos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un preso de la lista.");
            return;
        }

        String id = modelo.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de eliminar al preso con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/eliminar/" + id))
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 204) {
                    JOptionPane.showMessageDialog(this, "Preso eliminado correctamente.");
                    cargarPresos(); // Refrescar lista
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + response.body());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ListadoPresosGUI().setVisible(true));
    }
}