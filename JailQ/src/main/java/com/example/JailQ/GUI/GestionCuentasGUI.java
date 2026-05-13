package com.example.JailQ.GUI;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Interfaz Gráfica de Usuario (GUI) para la gestión de cuentas en el sistema JailQ.
 * <p>
 * Esta clase proporciona un formulario basado en Java Swing que permite:
 * <ul>
 * <li>Registrar nuevas cuentas con sus datos correspondientes.</li>
 * <li>Acceder a la ventana secundaria para eliminar cuentas de policía.</li>
 * <li>Visualizar en una consola integrada las respuestas del servidor.</li>
 * </ul>
 */



public class GestionCuentasGUI extends JFrame {

    private JTextField txtNombre, txtApellidos, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbTipoCuenta;
    private JTextArea txtConsola;
    private final HttpClient cliente;

    /**
     * Constructor de la clase. Inicializa el cliente HTTP y configura
     * todos los componentes visuales de la ventana (paneles, campos de texto y botones).
     */

    public GestionCuentasGUI() {
        cliente = HttpClientSingleton.getInstance().getClient();

        setTitle("JailQ - Gestión de Cuentas");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL SUPERIOR: Formulario para añadir ---
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Añadir Nueva Cuenta"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        txtNombre.setName("txtNombre"); 
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField();
        txtApellidos.setName("txtApellidos"); 
        panelFormulario.add(txtApellidos);

        panelFormulario.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        txtUsername.setName("txtUsername"); 
        panelFormulario.add(txtUsername);

        panelFormulario.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        txtPassword.setName("txtPassword"); 
        panelFormulario.add(txtPassword);

        panelFormulario.add(new JLabel("Tipo de Cuenta:"));
        cbTipoCuenta = new JComboBox<>(new String[]{"POLICIA", "FAMILIA", "GUBERNAMENTAL"});
        cbTipoCuenta.setName("cbTipoCuenta"); 
        panelFormulario.add(cbTipoCuenta);

        JButton btnAnadir = new JButton("Añadir Cuenta");
        btnAnadir.setName("btnAnadir"); 
        btnAnadir.addActionListener(e -> anadirCuenta());
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(btnAnadir);

        // --- PANEL CENTRAL: Formulario para borrar ---
        JPanel panelBorrar = new JPanel(new FlowLayout());
        panelBorrar.setBorder(BorderFactory.createTitledBorder("Eliminar Cuenta"));
        
        JButton btnAbrirEliminar = new JButton("Eliminar Policía");
        btnAbrirEliminar.setName("btnAbrirEliminar"); 
        btnAbrirEliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAbrirEliminar.setFocusPainted(false);
        btnAbrirEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAbrirEliminar.addActionListener(e -> new EliminarPoliciaGUI().setVisible(true));
        panelBorrar.add(btnAbrirEliminar);

        // --- PANEL INFERIOR: Consola de resultados ---
        txtConsola = new JTextArea(10, 30);
        txtConsola.setName("txtConsola"); 
        txtConsola.setEditable(false);
        txtConsola.setLineWrap(true);
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Resultado del Servidor"));

	    JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setName("btnVolver"); 
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> dispose());

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH);
        panelNorte.add(panelBorrar,     BorderLayout.SOUTH);

        JPanel panelSur = new JPanel(new BorderLayout(5, 5));
        panelSur.add(scrollConsola, BorderLayout.CENTER);
        panelSur.add(btnVolver,     BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
        add(panelSur,   BorderLayout.CENTER);
    }

    /**
     * Recopila los datos del formulario y realiza una petición POST al servidor.
     * <p>
     * Construye un objeto JSON con los datos de la cuenta y lo envía al endpoint
     * {@code /cuentas/crear}. El resultado se muestra en la consola de la interfaz.
     */
    private void anadirCuenta() {
        try {
            String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"apellidos\":\"%s\", \"username\":\"%s\", \"password\":\"%s\", \"tipoCuenta\":\"%s\"}",
                txtNombre.getText(), txtApellidos.getText(), txtUsername.getText(),
                new String(txtPassword.getPassword()), cbTipoCuenta.getSelectedItem()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/crear"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

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
     * Restablece todos los campos del formulario de creación a su estado inicial (vacíos).
     */
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }

}