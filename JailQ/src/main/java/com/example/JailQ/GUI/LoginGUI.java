package com.example.JailQ.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Interfaz gráfica para iniciar sesión como POLICIA.
 */
public class LoginGUI extends JFrame {

    private final HttpClient httpClient;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblEstado;

    public LoginGUI() {
        httpClient = HttpClient.newHttpClient();

        setTitle("JailQ - Login Policía");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(panelPrincipal);

        JLabel titulo = new JLabel("Iniciar sesión como POLICIA");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel(new GridLayout(4, 1, 8, 8));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 13));
        panelCampos.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        panelCampos.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 13));
        panelCampos.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panelCampos.add(txtPassword);

        panelPrincipal.add(panelCampos, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.addActionListener(e -> hacerLogin());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnLogin);

        lblEstado = new JLabel(" ");
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));

        panelInferior.add(panelBoton, BorderLayout.NORTH);
        panelInferior.add(lblEstado, BorderLayout.SOUTH);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
    }

    private void hacerLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblEstado.setText("Debes rellenar username y password.");
            JOptionPane.showMessageDialog(
                    this,
                    "Debes rellenar username y password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            String json = """
                    {
                      "username": "%s",
                      "password": "%s"
                    }
                    """.formatted(username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/login/policia"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                lblEstado.setText("Login correcto.");

                JOptionPane.showMessageDialog(
                        this,
                        "Login correcto. Bienvenido, " + username,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                JailQMainGUI main = new JailQMainGUI(username);
                main.setVisible(true);
                this.dispose();

            } else if (response.statusCode() == 401) {
                lblEstado.setText("Credenciales incorrectas.");
                txtPassword.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario o contraseña incorrectos.",
                        "Login fallido",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                lblEstado.setText("Error en el servidor.");

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginGUI gui = new LoginGUI();
            gui.setVisible(true);
        });
    }
}