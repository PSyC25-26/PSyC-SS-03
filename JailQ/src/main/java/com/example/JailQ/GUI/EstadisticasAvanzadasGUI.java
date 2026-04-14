package com.example.JailQ.GUI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;



public class EstadisticasAvanzadasGUI extends JFrame {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    private JComboBox<CarcelDatos> comboCarceles;
    private JLabel lblPorcentaje;
    private JProgressBar barraOcupacion;
    private JButton btnActualizar;

    public EstadisticasAvanzadasGUI() {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        setTitle("JailQ - Ocupación Detallada");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- PANEL SUPERIOR: Selector de Cárcel ---
        JPanel panelNorte = new JPanel(new FlowLayout());
        panelNorte.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10));
        panelNorte.add(new JLabel("Selecciona una Cárcel:"));
        
        comboCarceles = new JComboBox<>();
        comboCarceles.setPreferredSize(new Dimension(200, 30));
        comboCarceles.addActionListener(e -> mostrarDatosSeleccionados());
        panelNorte.add(comboCarceles);

        btnActualizar = new JButton("↻ Cargar/Actualizar");
        btnActualizar.addActionListener(e -> descargarDatos());
        panelNorte.add(btnActualizar);

        add(panelNorte, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Barra de progreso e información ---
        JPanel panelCentro = new JPanel(new GridLayout(3, 1, 5, 5));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JLabel lblTituloBarra = new JLabel("Nivel de Ocupación:", SwingConstants.CENTER);
        lblTituloBarra.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblTituloBarra);

        barraOcupacion = new JProgressBar();
        barraOcupacion.setStringPainted(true); // Para que se vea el texto dentro de la barra
        barraOcupacion.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(barraOcupacion);

        lblPorcentaje = new JLabel("Esperando datos...", SwingConstants.CENTER);
        lblPorcentaje.setFont(new Font("Arial", Font.ITALIC, 14));
        panelCentro.add(lblPorcentaje);

        add(panelCentro, BorderLayout.CENTER);

        // Cargar los datos automáticamente al abrir
        descargarDatos();
    }

    /**
     * Se conecta al servidor, descarga la lista y rellena el JComboBox.
     */
    private void descargarDatos() {
        comboCarceles.removeAllItems();
        lblPorcentaje.setText("Conectando con el servidor...");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/carcel/estadisticas-completas"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Traducimos el JSON mágico a una lista de objetos de Java
                List<CarcelDatos> lista = objectMapper.readValue(response.body(), new TypeReference<List<CarcelDatos>>(){});
                
                for (CarcelDatos carcel : lista) {
                    comboCarceles.addItem(carcel);
                }
                lblPorcentaje.setText("Datos cargados correctamente.");
            } else {
                lblPorcentaje.setText("Error al cargar: " + response.statusCode());
            }
        } catch (Exception ex) {
            lblPorcentaje.setText("Error de conexión. ¿Servidor encendido?");
        }
    }

    /**
     * Calcula los porcentajes y actualiza la barra de progreso.
     */
    private void mostrarDatosSeleccionados() {
        CarcelDatos seleccion = (CarcelDatos) comboCarceles.getSelectedItem();
        if (seleccion != null) {
            int max = seleccion.capacidad;
            int actual = seleccion.ocupacion;
            double porcentaje = (double) actual / max * 100;

            // Configuramos la barra
            barraOcupacion.setMaximum(max);
            barraOcupacion.setValue(actual);
            barraOcupacion.setString(actual + " / " + max);

            // Configuramos el texto de abajo
            lblPorcentaje.setText(String.format("La cárcel está al %.1f%% de su capacidad.", porcentaje));

            // ¡Toque visual extra! Cambiamos el color de la barra según la saturación
            if (porcentaje >= 90) {
                barraOcupacion.setForeground(Color.RED); // Crítico
            } else if (porcentaje >= 75) {
                barraOcupacion.setForeground(Color.ORANGE); // Aviso
            } else {
                barraOcupacion.setForeground(new Color(50, 205, 50)); // Verde (Correcto)
            }
        }
    }

    // --- CLASE INTERNA PARA MAPEAR EL JSON ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CarcelDatos {
        public int id;
        public String nombre;
        public int capacidad;
        public int ocupacion;

        // El toString() es crucial: es lo que Swing muestra en el menú desplegable
        @Override
        public String toString() {
            return nombre;
        }
    }
}