package com.example.JailQ.GUI;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Carcel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GestionPresosGUI extends JFrame {

    private JTextField txtNombre, txtApellidos, txtFechaNacimiento, txtCondena;
    private JComboBox<String> cbCarcel;
    private JTextArea txtConsola;
    private final HttpClient httpClient;
    private JList<Delito> listaDelitos;
    
    //Diccionario para guardar Nombre -> ID de la cárcel
    private Map<String, Integer> mapaCarceles = new HashMap<>();

    public GestionPresosGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Gestión de Presos");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 1, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Preso"));

        txtNombre = new JTextField();
        txtApellidos = new JTextField();
        txtFechaNacimiento = new JTextField();
        txtCondena = new JTextField();

        cbCarcel = new JComboBox<>();
        //Cargamos las cárceles desde la base de datos al iniciar
        cargarCarcelesDesdeBD();

        listaDelitos = new JList<>(Delito.values());
        listaDelitos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollDelitos = new JScrollPane(listaDelitos);
        scrollDelitos.setPreferredSize(new Dimension(0, 150));

        panelFormulario.add(crearCelda("Nombre:", txtNombre));
        panelFormulario.add(crearCelda("Apellidos:", txtApellidos));
        panelFormulario.add(crearCelda("Fecha Nacimiento (AAAA-MM-DD):", txtFechaNacimiento));
        panelFormulario.add(crearCelda("Condena (años):", txtCondena));
        panelFormulario.add(crearCeldaPanel("Cárcel (Cargada de BD):", cbCarcel));
        panelFormulario.add(crearCeldaPanel("Delito:", scrollDelitos));

        JButton btnAnadir = new JButton("Registrar Preso");
        btnAnadir.addActionListener(e -> enviarPreso());

        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.add(panelFormulario, BorderLayout.CENTER);
        panelCentral.add(btnAnadir, BorderLayout.SOUTH);

        txtConsola = new JTextArea(5, 30);
        txtConsola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Estado del Servidor"));

        // ── Botón Volver ──────────────────────────────────────────────────────
        JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> dispose());
 
        JPanel panelSur = new JPanel(new BorderLayout(5, 5));
        panelSur.add(scrollConsola, BorderLayout.CENTER);
        panelSur.add(btnVolver, BorderLayout.SOUTH);
 
        add(panelCentral, BorderLayout.CENTER);
        add(panelSur,     BorderLayout.SOUTH);
		
    }

    /**
     * Hace un GET a /carcel para obtener las cárceles reales
     */
    private void cargarCarcelesDesdeBD() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/carcel"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                // Convertimos el JSON en una lista de objetos Carcel
                List<Carcel> carceles = mapper.readValue(response.body(), new TypeReference<List<Carcel>>(){});
                
                cbCarcel.removeAllItems();
                for (Carcel c : carceles) {
                    cbCarcel.addItem(c.getNombre());
                    mapaCarceles.put(c.getNombre(), c.getIdCarcel());
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudieron cargar las cárceles: " + e.getMessage());
            cbCarcel.addItem("Error al cargar datos");
        }
    }

    private void enviarPreso() {
        try {
            String nombreCarcel = (String) cbCarcel.getSelectedItem();
            Integer idCarcel = mapaCarceles.get(nombreCarcel);

            if (idCarcel == null) {
                txtConsola.setText("Error: Selecciona una cárcel válida.");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("nombre", txtNombre.getText());
            jsonMap.put("apellidos", txtApellidos.getText());
            jsonMap.put("fechaNacimiento", txtFechaNacimiento.getText());
            jsonMap.put("fechaIngreso", LocalDate.now().toString());
            jsonMap.put("condena", Double.parseDouble(txtCondena.getText().replace(",", ".")));
            jsonMap.put("delitos", listaDelitos.getSelectedValuesList());
            
            //Se enviamos el ID como Integer, que es lo que espera el Backend
            jsonMap.put("carcel", idCarcel);

            String jsonBody = mapper.writeValueAsString(jsonMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/preso/crear"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                txtConsola.setText("ÉXITO: Preso registrado.\n" + response.body());
                limpiarFormulario();
            } else {
                txtConsola.setText("ERROR " + response.statusCode() + ":\n" + response.body());
            }

        } catch (Exception ex) {
            txtConsola.setText("Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtFechaNacimiento.setText("");
        txtCondena.setText("");
        if (cbCarcel.getItemCount() > 0) cbCarcel.setSelectedIndex(0);
        listaDelitos.clearSelection();
    }

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

    private JPanel crearCeldaPanel(String etiqueta, javax.swing.JComponent componente) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(new JLabel(etiqueta));
        panel.add(componente);
        return panel;
    }
}