package com.example.JailQ.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Interfaz de listado de presos con soporte para JSON anidado (relación Carcel).
 */
public class ListadoPresosGUI extends JFrame {

    private JTable tablaPresos;
    private DefaultTableModel modelo;
    private final HttpClient httpClient;
    
    //URLs ajustadas según los controladores del backend
    private final String BASE_URL = "http://localhost:8080/preso";
    private final String CARCEL_URL = "http://localhost:8080/carcel";

    public ListadoPresosGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Listado de Presos");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        String[] columnas = {"ID", "Nombre", "Apellidos", "Condena", "Cárcel"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPresos = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaPresos);

        JPanel panelBotones = new JPanel();
        JButton btnActualizar = new JButton("Actualizar Lista");
        JButton btnTrasladar = new JButton("Trasladar Preso");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        
        btnTrasladar.setBackground(new Color(100, 200, 255));
        btnEliminar.setBackground(new Color(255, 100, 100));

        panelBotones.add(btnActualizar);
        panelBotones.add(btnTrasladar);
        panelBotones.add(btnEliminar);

        // Eventos
        btnActualizar.addActionListener(e -> cargarPresos());
        btnTrasladar.addActionListener(e -> trasladarPresoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarPresoSeleccionado());

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Carga inicial
        cargarPresos();
    }

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
                JOptionPane.showMessageDialog(this, "Error del servidor: " + response.statusCode());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }


    private void actualizarTabla(String json) {
        modelo.setRowCount(0);
        
        String jsonLimpio = json.replaceAll("\"carcel\"\\s*:\\s*\\{[^}]*\"nombre\"\\s*:\\s*\"([^\"]+)\"[^}]*\\}", "\"carcel\":\"$1\"");
        jsonLimpio = jsonLimpio.replaceAll("\"carcel\"\\s*:\\s*\\{[^}]*\\}", "\"carcel\":\"N/A\"");

        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(jsonLimpio);

        while (matcher.find()) {
            String objeto = matcher.group(1);
            
            String id = extraerValor(objeto, "idPreso");
            if (id.equals("N/A")) id = extraerValor(objeto, "id");
            
            String nombre = extraerValor(objeto, "nombre");
            String apellidos = extraerValor(objeto, "apellidos");
            String condena = extraerValor(objeto, "condena");
            
            String carcel = extraerValor(objeto, "carcel"); 

            if (!nombre.equals("N/A")) {
                modelo.addRow(new Object[]{id, nombre, apellidos, condena, carcel});
            }
        }
    }

    private String extraerValor(String texto, String campo) {
        Pattern p = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
        Matcher m = p.matcher(texto);
        if (m.find()) {
            String valor = m.group(1).trim();
            return valor.replace("\"", "");
        }
        return "N/A";
    }

    private void trasladarPresoSeleccionado() {
        int fila = tablaPresos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un preso para trasladar.");
            return;
        }

        String idPreso = modelo.getValueAt(fila, 0).toString();
        String nombrePreso = modelo.getValueAt(fila, 1).toString();
        
        String carcelActual = modelo.getValueAt(fila, 4).toString();

        if (idPreso.equals("N/A")) {
            JOptionPane.showMessageDialog(this, "El preso seleccionado no tiene un ID válido para operar.");
            return;
        }

        List<String> carceles = obtenerNombresCarceles();
        if (carceles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las cárceles de destino.");
            return;
        }

        String carcelDestino = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona la cárcel de destino para " + nombrePreso + ":",
                "Trasladar Preso",
                JOptionPane.QUESTION_MESSAGE,
                null,
                carceles.toArray(),
                carcelActual
        );

        if (carcelDestino != null) {
            ejecutarTraslado(idPreso, carcelDestino);
        }
    }

    private List<String> obtenerNombresCarceles() {
        List<String> nombres = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CARCEL_URL))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            Pattern p = Pattern.compile("\"nombre\"\\s*:\\s*\"(.*?)\"");
            Matcher m = p.matcher(response.body());
            while (m.find()) {
                nombres.add(m.group(1));
            }
        } catch (Exception ex) {
            System.err.println("Error obteniendo cárceles: " + ex.getMessage());
        }
        return nombres;
    }

    private void ejecutarTraslado(String idPreso, String nombreCarcel) {
        try {
            String carcelCodificada = URLEncoder.encode(nombreCarcel, StandardCharsets.UTF_8);
            String urlFinal = BASE_URL + "/trasladar/" + idPreso + "/" + carcelCodificada;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlFinal))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Traslado exitoso a " + nombreCarcel);
                cargarPresos();
            } else {
                JOptionPane.showMessageDialog(this, "Error en el traslado: " + response.body());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de red: " + ex.getMessage());
        }
    }

    private void eliminarPresoSeleccionado() {
        int fila = tablaPresos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un preso para eliminar.");
            return;
        }
        
        String id = modelo.getValueAt(fila, 0).toString();
        if (id.equals("N/A")) return;

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar preso con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/eliminar/" + id))
                        .DELETE()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    cargarPresos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + response.body());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ListadoPresosGUI().setVisible(true));
    }
}