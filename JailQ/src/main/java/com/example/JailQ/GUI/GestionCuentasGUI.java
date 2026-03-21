package com.example.JailQ.GUI;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GestionCuentasGUI extends JFrame {

    private JTextField txtNombre, txtApellidos, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbTipoCuenta;
    private JTextArea txtConsola;
    private final HttpClient httpClient;

    public GestionCuentasGUI() {
        httpClient = HttpClient.newHttpClient();

        // Configuración básica de la ventana
        setTitle("JailQ - Gestión de Cuentas");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL SUPERIOR: Formulario para añadir ---
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Añadir Nueva Cuenta"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField();
        panelFormulario.add(txtApellidos);

        panelFormulario.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panelFormulario.add(txtUsername);

        panelFormulario.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelFormulario.add(txtPassword);

        panelFormulario.add(new JLabel("Tipo de Cuenta:"));
        cbTipoCuenta = new JComboBox<>(new String[]{"POLICIA", "FAMILIA", "GUBERNAMENTAL"});
        panelFormulario.add(cbTipoCuenta);

        JButton btnAnadir = new JButton("Añadir Cuenta");
        btnAnadir.addActionListener(e -> anadirCuenta());
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(btnAnadir);

        // --- PANEL CENTRAL: acceso a la GUI de eliminar policías ---
        JPanel panelEliminar = new JPanel(new FlowLayout());
        panelEliminar.setBorder(BorderFactory.createTitledBorder("Eliminar Cuenta"));

        JButton btnAbrirEliminarPolicia = new JButton("Eliminar cuentas de policía");
        btnAbrirEliminarPolicia.addActionListener(e -> abrirVentanaEliminarPolicia());
        panelEliminar.add(btnAbrirEliminarPolicia);

        // --- PANEL INFERIOR: Consola de resultados ---
        txtConsola = new JTextArea(10, 30);
        txtConsola.setEditable(false);
        txtConsola.setLineWrap(true);
        txtConsola.setWrapStyleWord(true);

        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Resultado del Servidor"));

        // Añadir todo a la ventana principal
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH);
        panelNorte.add(panelEliminar, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
        add(scrollConsola, BorderLayout.CENTER);
    }

    /**
     * Simula lo que hacíamos con Postman en POST /cuentas/crear
     */
    private void anadirCuenta() {
        try {
            String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"apellidos\":\"%s\", \"username\":\"%s\", \"password\":\"%s\", \"tipoCuenta\":\"%s\"}",
                txtNombre.getText(),
                txtApellidos.getText(),
                txtUsername.getText(),
                new String(txtPassword.getPassword()),
                cbTipoCuenta.getSelectedItem()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/crear"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                txtConsola.setText("✅ÉXITO (201): Cuenta creada.\n" + response.body());
                limpiarFormulario();
            } else {
                txtConsola.setText(" ERROR (" + response.statusCode() + "):\n" + response.body());
            }
        } catch (Exception ex) {
            txtConsola.setText(" Excepción: " + ex.getMessage() + "\n¿Está encendido el servidor Spring Boot?");
        }
    }

    private void abrirVentanaEliminarPolicia() {
        new EliminarPoliciaGUI().setVisible(true);
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cbTipoCuenta.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionCuentasGUI().setVisible(true);
        });
    }
}