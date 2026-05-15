package com.example.JailQ.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz de listado de presos con soporte para JSON anidado.
 * Permite listar, trasladar, eliminar y modificar la condena de presos.
 */
public class ListadoPresosGUI extends JFrame {

    private JTable tablaPresos;
    private DefaultTableModel modelo;
    private final HttpClient httpClient;

    private final String BASE_URL = "http://localhost:8080/preso";
    private final String CARCEL_URL = "http://localhost:8080/carcel";

    public ListadoPresosGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Listado de Presos");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        String[] columnas = {"ID", "Nombre", "Apellidos", "Condena", "Cárcel"};

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPresos = new JTable(modelo);
        tablaPresos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPresos.setRowHeight(24);
        tablaPresos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        tablaPresos.setName("tablaPresos"); 
        JScrollPane scrollPane = new JScrollPane(tablaPresos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Presos registrados"));

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(245, 247, 250));

        JButton btnActualizar = new JButton("Actualizar lista");
        JButton btnTrasladar = new JButton("Trasladar preso");
        JButton btnModificarCondena = new JButton("Modificar condena");
        btnActualizar.setName("btnActualizar"); 
        
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.setName("btnEliminar"); 
        JButton btnVolver = new JButton("← Volver al menú principal");

        btnTrasladar.setName("btnTrasladar");
        btnModificarCondena.setName("btnModificarCondena");
        btnVolver.setName("btnVolver");
        
        estilizarBoton(btnActualizar, new Color(230, 235, 242), new Color(26, 61, 111));
        estilizarBoton(btnTrasladar, new Color(100, 200, 255), Color.BLACK);
        estilizarBoton(btnModificarCondena, new Color(255, 210, 100), Color.BLACK);
        estilizarBoton(btnEliminar, new Color(255, 100, 100), Color.BLACK);
        estilizarBoton(btnVolver, new Color(220, 225, 235), new Color(26, 61, 111));

        panelBotones.add(btnActualizar);
        panelBotones.add(btnTrasladar);
        panelBotones.add(btnModificarCondena);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVolver);

        btnActualizar.addActionListener(e -> cargarPresos());
        btnTrasladar.addActionListener(e -> trasladarPresoSeleccionado());
        btnModificarCondena.addActionListener(e -> modificarCondenaSeleccionado());
        btnEliminar.addActionListener(e -> eliminarPresoSeleccionado());
        btnVolver.addActionListener(e -> dispose());

        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);

        cargarPresos();
    }

    private void estilizarBoton(JButton boton, Color fondo, Color texto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        String jsonLimpio = json.replaceAll(
                "\"carcel\"\\s*:\\s*\\{[^}]*\"nombre\"\\s*:\\s*\"([^\"]+)\"[^}]*\\}",
                "\"carcel\":\"$1\""
        );

        jsonLimpio = jsonLimpio.replaceAll(
                "\"carcel\"\\s*:\\s*\\{[^}]*\\}",
                "\"carcel\":\"N/A\""
        );

        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(jsonLimpio);

        while (matcher.find()) {
            String objeto = matcher.group(1);

            String id = extraerValor(objeto, "idPreso");
            if (id.equals("N/A")) {
                id = extraerValor(objeto, "id");
            }

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
        // En lugar de URLEncoder, reemplazamos espacios por %20 para el PathVariable
        String nombreLimpio = nombreCarcel.replace(" ", "%20");
        String urlFinal = BASE_URL + "/trasladar/" + idPreso + "/" + nombreLimpio;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFinal)) // URI.create ya maneja caracteres especiales si están escapados
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JOptionPane.showMessageDialog(this, "Traslado exitoso a " + nombreCarcel);
            cargarPresos();
        } else {
            // Esto te dirá exactamente qué intentó buscar el servidor
            JOptionPane.showMessageDialog(this, "Error: El servidor no encontró la cárcel [" + nombreCarcel + "]");
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error de red: " + ex.getMessage());
    }
}

    private void modificarCondenaSeleccionado() {
        int fila = tablaPresos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un preso para modificar su condena.");
            return;
        }

        String idPreso = modelo.getValueAt(fila, 0).toString();
        String nombrePreso = modelo.getValueAt(fila, 1).toString();
        String condenaActual = modelo.getValueAt(fila, 3).toString();

        if (idPreso.equals("N/A")) {
            JOptionPane.showMessageDialog(this, "El preso seleccionado no tiene un ID válido.");
            return;
        }

        String nuevaCondenaTexto = JOptionPane.showInputDialog(
                this,
                "Introduce la nueva condena para " + nombrePreso + ":\nCondena actual: " + condenaActual + " años",
                "Modificar condena",
                JOptionPane.QUESTION_MESSAGE
        );

        if (nuevaCondenaTexto == null) {
            return;
        }

        nuevaCondenaTexto = nuevaCondenaTexto.trim();

        if (nuevaCondenaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La condena no puede estar vacía.");
            return;
        }

        int nuevaCondena;

        try {
            nuevaCondena = Integer.parseInt(nuevaCondenaTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La condena debe ser un número entero.");
            return;
        }

        if (nuevaCondena <= 0) {
            JOptionPane.showMessageDialog(this, "La condena debe ser mayor que 0.");
            return;
        }

        ejecutarModificacionCondena(idPreso, nuevaCondena);
    }

    private void ejecutarModificacionCondena(String idPreso, int nuevaCondena) {
        try {
            String urlFinal = BASE_URL + "/modificar-condena/" + idPreso + "/" + nuevaCondena;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlFinal))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Condena modificada correctamente.");
                cargarPresos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar la condena: " + response.body());
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

        if (id.equals("N/A")) {
            JOptionPane.showMessageDialog(this, "El preso seleccionado no tiene un ID válido.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar preso con ID " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/eliminar/" + id))
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Preso eliminado correctamente.");
                    cargarPresos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + response.body());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de red: " + ex.getMessage());
            }
        }
    }
}