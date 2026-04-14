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
 * Diálogo modal de inicio de sesión del sistema JailQ.
 *
 * <p>Se abre sobre {@link JailQMainGUI} cuando el usuario pulsa
 * «Iniciar sesión». Al cerrarse, la ventana principal consulta
 * {@link #isLoginCorrecto()}, {@link #getUsername()} y
 * {@link #getTipoCuenta()} para actualizar su estado de sesión.
 *
 * <p>Envía las credenciales al endpoint
 * {@code POST /cuentas/login/policia} y extrae el {@code tipoCuenta}
 * del JSON de respuesta sin depender de ninguna librería JSON externa.
 */
public class LoginDialog extends JDialog {
 
    private final HttpClient httpClient;
 
    private JTextField    txtUsername;
    private JPasswordField txtPassword;
    private JLabel        lblEstado;
 
    /** Resultado del intento de login. */
    private boolean loginCorrecto = false;
    private String  username      = null;
    private String  tipoCuenta    = null;
 
    // ──────────────────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Construye el diálogo de login modal sobre la ventana padre indicada.
     *
     * @param parent Ventana padre sobre la que se centra el diálogo.
     */
    public LoginDialog(JFrame parent) {
        super(parent, "JailQ - Iniciar sesión", true);
        httpClient = HttpClient.newHttpClient();
 
        setSize(400, 260);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 
        initComponents();
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Inicialización de componentes
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Construye y organiza todos los componentes visuales del diálogo.
     */
    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(panelPrincipal);
 
        // Título
        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelPrincipal.add(titulo, BorderLayout.NORTH);
 
        // Campos
        JPanel panelCampos = new JPanel(new GridLayout(4, 1, 6, 6));
 
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblUser);
 
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelCampos.add(txtUsername);
 
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblPass);
 
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.addActionListener(e -> hacerLogin());
        panelCampos.add(txtPassword);
 
        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
 
        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
 
        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> hacerLogin());
 
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnLogin);
 
        lblEstado = new JLabel(" ");
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
 
        panelInferior.add(panelBoton, BorderLayout.NORTH);
        panelInferior.add(lblEstado,  BorderLayout.SOUTH);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Lógica de login
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Recoge las credenciales y realiza la petición de autenticación.
     *
     * <p>Si el login es correcto (HTTP 200), guarda el resultado en los campos
     * internos y cierra el diálogo para que {@link JailQMainGUI} pueda
     * consultar los resultados. Si falla, muestra el error sin cerrar el diálogo.
     */
    private void hacerLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
 
        if (user.isEmpty() || pass.isEmpty()) {
            lblEstado.setForeground(Color.RED);
            lblEstado.setText("Rellena username y password.");
            return;
        }
 
        try {
            String json = "{\"username\": \"%s\", \"password\": \"%s\"}"
                    .formatted(user, pass);
 
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/cuentas/login/policia"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
 
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
 
            if (response.statusCode() == 200) {
                loginCorrecto = true;
                username      = user;
                tipoCuenta    = extraerTipoCuenta(response.body());
                dispose();  // Cierra el diálogo; la ventana padre retoma el control
 
            } else if (response.statusCode() == 401) {
                lblEstado.setForeground(Color.RED);
                lblEstado.setText("Usuario o contraseña incorrectos.");
                txtPassword.setText("");
 
            } else {
                lblEstado.setForeground(Color.RED);
                lblEstado.setText("Error del servidor (código " + response.statusCode() + ").");
            }
 
        } catch (IOException | InterruptedException e) {
            lblEstado.setForeground(Color.RED);
            lblEstado.setText("No se pudo conectar con el servidor.");
        }
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Extracción del tipoCuenta
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Extrae el valor del campo {@code tipoCuenta} del JSON de respuesta
     * del backend sin depender de ninguna librería JSON externa.
     *
     * <p>Spring Boot serializa la entidad {@code Cuenta} con el campo en
     * la forma {@code "tipoCuenta":"POLICIA"}.
     *
     * @param jsonBody Cuerpo de la respuesta HTTP.
     * @return Valor de {@code tipoCuenta} en mayúsculas, o {@code "DESCONOCIDO"}.
     */
    private String extraerTipoCuenta(String jsonBody) {
        String clave = "\"tipoCuenta\":\"";
        int inicio = jsonBody.indexOf(clave);
        if (inicio == -1) return "DESCONOCIDO";
        inicio += clave.length();
        int fin = jsonBody.indexOf("\"", inicio);
        if (fin == -1) return "DESCONOCIDO";
        return jsonBody.substring(inicio, fin).toUpperCase();
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Getters de resultado
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Indica si el último intento de login fue correcto.
     *
     * @return {@code true} si el login fue exitoso.
     */
    public boolean isLoginCorrecto() { return loginCorrecto; }
 
    /**
     * Devuelve el nombre de usuario autenticado.
     *
     * @return Username autenticado, o {@code null} si el login no fue correcto.
     */
    public String getUsername()   { return username;   }
 
    /**
     * Devuelve el tipo de cuenta del usuario autenticado.
     *
     * @return Tipo de cuenta ({@code "POLICIA"}, {@code "GUBERNAMENTAL"},
     *         {@code "FAMILIA"} o {@code "DESCONOCIDO"}), o {@code null}.
     */
    public String getTipoCuenta() { return tipoCuenta; }
}