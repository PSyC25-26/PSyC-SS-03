package com.example.JailQ.GUI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FiltrarPresosPorDelitoGUI extends JFrame {

    private JComboBox<String> comboDelitos;
    private JButton botonFiltrar;
    private JButton botonVolver;
    private JTextArea areaResultados;
    private JLabel labelEstado;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FiltrarPresosPorDelitoGUI() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

        setTitle("JailQ - Filtrar presos por delito");
        setSize(780, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBorder(new EmptyBorder(18, 18, 18, 18));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        panelPrincipal.add(crearPanelCabecera(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelResultados(), BorderLayout.CENTER);
        panelPrincipal.add(crearPanelInferior(), BorderLayout.SOUTH);

        add(panelPrincipal);

        botonFiltrar.addActionListener(e -> filtrarPresos());
        botonVolver.addActionListener(e -> dispose());
    }

    private JPanel crearPanelCabecera() {
        JPanel panelCabecera = new JPanel(new BorderLayout(10, 10));
        panelCabecera.setBackground(Color.WHITE);
        panelCabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Filtrar presos por delito");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(26, 61, 111));

        JLabel subtitulo = new JLabel("Consulta los internos registrados según el delito seleccionado.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(90, 90, 90));

        panelTexto.add(titulo);
        panelTexto.add(Box.createVerticalStrut(4));
        panelTexto.add(subtitulo);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelFiltro.setBackground(Color.WHITE);

        JLabel labelDelito = new JLabel("Delito:");
        labelDelito.setFont(new Font("Segoe UI", Font.BOLD, 12));

        comboDelitos = new JComboBox<>(new String[]{
                "HOMICIDIO",
                "SECUESTRO",
                "ROBO",
                "AGRESION_SEXUAL",
                "PEDOFILIA",
                "ESTAFA",
                "TRAF_DROGAS",
                "TRAF_PERSONAS",
                "TERRORISMO",
                "PIROMANIA",
                "BLANQUEO_DINERO",
                "FALSIFICACION_DOC"
        });

        comboDelitos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboDelitos.setPreferredSize(new Dimension(190, 30));

        botonFiltrar = new JButton("Filtrar");
        botonFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botonFiltrar.setFocusPainted(false);
        botonFiltrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonFiltrar.setBackground(new Color(26, 61, 111));
        botonFiltrar.setForeground(Color.WHITE);

        panelFiltro.add(labelDelito);
        panelFiltro.add(comboDelitos);
        panelFiltro.add(botonFiltrar);

        panelCabecera.add(panelTexto, BorderLayout.CENTER);
        panelCabecera.add(panelFiltro, BorderLayout.EAST);

        return panelCabecera;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createTitledBorder("Resultados de la búsqueda"));

        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaResultados.setForeground(new Color(40, 40, 40));
        areaResultados.setBackground(Color.WHITE);
        areaResultados.setMargin(new Insets(10, 10, 10, 10));
        areaResultados.setText("Selecciona un delito y pulsa Filtrar para ver los presos encontrados.");

        JScrollPane scrollPane = new JScrollPane(areaResultados);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBackground(new Color(245, 247, 250));

        labelEstado = new JLabel("Listo para filtrar.");
        labelEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelEstado.setForeground(new Color(80, 80, 80));

        botonVolver = new JButton("← Volver al menú principal");
        botonVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botonVolver.setFocusPainted(false);
        botonVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonVolver.setBackground(new Color(230, 235, 242));
        botonVolver.setForeground(new Color(26, 61, 111));

        panelInferior.add(labelEstado, BorderLayout.WEST);
        panelInferior.add(botonVolver, BorderLayout.EAST);

        return panelInferior;
    }

    private void filtrarPresos() {
        String delitoSeleccionado = (String) comboDelitos.getSelectedItem();

        if (delitoSeleccionado == null || delitoSeleccionado.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un delito.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String url = "http://localhost:8080/preso/filtrar/delito/" + delitoSeleccionado;

        try {
            labelEstado.setText("Buscando presos con delito: " + delitoSeleccionado + "...");
            labelEstado.setForeground(new Color(26, 61, 111));
            areaResultados.setText("");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                mostrarResultados(response.body());
                labelEstado.setText("Filtro realizado correctamente.");
                labelEstado.setForeground(new Color(0, 128, 0));

            } else if (response.statusCode() == 400) {
                labelEstado.setText("Delito no válido.");
                labelEstado.setForeground(new Color(180, 120, 0));

                JOptionPane.showMessageDialog(
                        this,
                        response.body(),
                        "Delito no válido",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {
                labelEstado.setText("Error al filtrar presos.");
                labelEstado.setForeground(new Color(180, 30, 30));

                JOptionPane.showMessageDialog(
                        this,
                        "Error del servidor: " + response.statusCode() + "\n" + response.body(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {
            labelEstado.setText("No se pudo conectar con el servidor.");
            labelEstado.setForeground(new Color(180, 30, 30));

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar con el backend.\nComprueba que Spring Boot está arrancado en http://localhost:8080.",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void mostrarResultados(String json) {
        try {
            JsonNode presos = objectMapper.readTree(json);

            if (!presos.isArray() || presos.size() == 0) {
                areaResultados.setText("No se encontraron presos con ese delito.");
                return;
            }

            StringBuilder sb = new StringBuilder();

            sb.append("Presos encontrados: ")
                    .append(presos.size())
                    .append("\n");

            sb.append("============================================================\n\n");

            for (JsonNode preso : presos) {
                sb.append("ID: ")
                        .append(preso.has("id") ? preso.get("id").asText() : "Sin ID")
                        .append("\n");

                sb.append("Nombre: ")
                        .append(preso.has("nombre") ? preso.get("nombre").asText() : "")
                        .append("\n");

                sb.append("Apellidos: ")
                        .append(preso.has("apellidos") ? preso.get("apellidos").asText() : "")
                        .append("\n");

                sb.append("Fecha nacimiento: ")
                        .append(preso.has("fechaNacimiento") ? preso.get("fechaNacimiento").asText() : "")
                        .append("\n");

                sb.append("Condena: ")
                        .append(preso.has("condena") ? preso.get("condena").asText() : "")
                        .append(" años\n");

                sb.append("Fecha ingreso: ")
                        .append(preso.has("fechaIngreso") ? preso.get("fechaIngreso").asText() : "")
                        .append("\n");

                sb.append("Delitos: ");

                if (preso.has("delitos") && preso.get("delitos").isArray()) {
                    for (JsonNode delito : preso.get("delitos")) {
                        sb.append(delito.asText()).append(" ");
                    }
                }

                sb.append("\n");

                if (preso.has("carcel") && preso.get("carcel").has("nombre")) {
                    sb.append("Cárcel: ")
                            .append(preso.get("carcel").get("nombre").asText())
                            .append("\n");
                }

                sb.append("\n------------------------------------------------------------\n\n");
            }

            areaResultados.setText(sb.toString());
            areaResultados.setCaretPosition(0);

        } catch (Exception e) {
            areaResultados.setText("Error al procesar la respuesta del servidor.\n\nRespuesta recibida:\n" + json);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FiltrarPresosPorDelitoGUI gui = new FiltrarPresosPorDelitoGUI();
            gui.setVisible(true);
        });
    }
}