package com.example.JailQ.GUI;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GestionCuentasGUI extends JFrame {

    private JTextField txtNombre, txtApellidos, txtUsername, txtIdBorrar;
    private JPasswordField txtPassword;
    private JComboBox<String> cbTipoCuenta;
    private JTextArea txtConsola;
    private final HttpClient httpClient;

    public GestionCuentasGUI() {
        httpClient = HttpClient.newHttpClient();

        // Configuración básica de la ventana
        setTitle("JailQ - Gestión de Cuentas");
        setSize(400, 550);
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
        panelFormulario.add(new JLabel("")); // Espacio vacío
        panelFormulario.add(btnAnadir);

        // --- PANEL CENTRAL: Formulario para borrar ---
        JPanel panelBorrar = new JPanel(new FlowLayout());
        panelBorrar.setBorder(BorderFactory.createTitledBorder("Eliminar Cuenta"));
        
        panelBorrar.add(new JLabel("ID Cuenta a borrar:"));
        txtIdBorrar = new JTextField(5);
        panelBorrar.add(txtIdBorrar);
        
        JButton btnBorrar = new JButton("Eliminar");
        btnBorrar.addActionListener(e -> eliminarCuenta());
        panelBorrar.add(btnBorrar);

        // --- PANEL INFERIOR: Consola de resultados ---
        txtConsola = new JTextArea(10, 30);
        txtConsola.setEditable(false);
        txtConsola.setLineWrap(true);
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Resultado del Servidor"));

        // Añadir todo a la ventana principal
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH);
        panelNorte.add(panelBorrar, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
        add(scrollConsola, BorderLayout.CENTER);
    }

    /**
     * Simula lo que hacíamos con Postman en POST /cuentas/crear
     */
    private void anadirCuenta() {
        try {
            // 1. Construimos el JSON a mano con los datos de las cajas de texto
            String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"apellidos\":\"%s\", \"username\":\"%s\", \"password\":\"%s\", \"tipoCuenta\":\"%s\"}",
                txtNombre.getText(), txtApellidos.getText(), txtUsername.getText(),
                new String(txtPassword.getPassword()), cbTipoCuenta.getSelectedItem()
            );

            // 2. Preparamos la petición POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/crear"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // 3. Enviamos la petición y leemos la respuesta
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 201) {
                txtConsola.setText("✅ ÉXITO (201): Cuenta creada.\n" + response.body());
                limpiarFormulario();
            } else {
                txtConsola.setText("❌ ERROR (" + response.statusCode() + "):\n" + response.body());
            }
        } catch (Exception ex) {
            txtConsola.setText("⚠️ Excepción: " + ex.getMessage() + "\n¿Está encendido el servidor Spring Boot?");
        }
    }

    /**
     * Simula lo que hacíamos con Postman en DELETE /cuentas/eliminar/{id}
     */
    private void eliminarCuenta() {
        String id = txtIdBorrar.getText().trim();
        if (id.isEmpty()) {
            txtConsola.setText("⚠️ Por favor, introduce un ID válido.");
            return;
        }

        try {
            // Preparamos la petición DELETE
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/eliminar/" + id))
                    .DELETE()
                    .build();

            // Enviamos la petición
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                txtConsola.setText("✅ ÉXITO (200): " + response.body());
                txtIdBorrar.setText("");
            } else {
                txtConsola.setText("❌ ERROR (" + response.statusCode() + "):\n" + response.body());
            }
        } catch (Exception ex) {
            txtConsola.setText("⚠️ Excepción: " + ex.getMessage() + "\n¿Está encendido el servidor Spring Boot?");
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }

    // Método principal para arrancar SÓLO la interfaz gráfica
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionCuentasGUI().setVisible(true);
        });
    }
}