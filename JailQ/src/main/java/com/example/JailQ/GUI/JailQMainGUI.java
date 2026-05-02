package com.example.JailQ.GUI;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
 
/**
 * Ventana principal del sistema JailQ.
 *
 * Gestiona el estado de sesión internamente:
 * - Sin sesión: solo Gestión de Cuentas disponible.
 * - Con sesión POLICIA: todos los módulos disponibles.
 * - Con sesión otro tipo: Cárceles y Cuentas disponibles, Presos bloqueado.
 */
public class JailQMainGUI extends JFrame {
 
    private String username   = null;
    private String tipoCuenta = null;
 
    private JLabel  lblBienvenida;
    private JButton btnCuentas;
    private JButton btnCarceles;
    private JButton btnPresos;
    private JButton btnSesion;
    private JLabel  lblEstado;
 
    private final HttpClient httpClient;
 
    // ──────────────────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────────────────
 
    public JailQMainGUI() {
        httpClient = HttpClient.newHttpClient();
 
        setTitle("JailQ - Sistema de Gestión Penitenciaria");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(5, 5));
 
        try {
            var stream = getClass().getResourceAsStream("/assets/jailqualitylogobase.png");
            if (stream != null) setIconImage(ImageIO.read(stream));
        } catch (Exception ignored) { }
 
        add(crearPanelCabecera(), BorderLayout.NORTH);
        add(crearPanelBotones(),  BorderLayout.CENTER);
        add(crearPanelEstado(),   BorderLayout.SOUTH);
 
        actualizarBotones();
        comprobarConexionServidor();
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Panel de cabecera
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Panel superior con logo, título, subtítulo y bienvenida dinámica.
     */
    private JPanel crearPanelCabecera() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Jail Quality"));
 
        JLabel lblLogo = cargarLogo(120, 80);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblLogo);
        panel.add(Box.createVerticalStrut(6));
 
        JLabel lblTitulo = new JLabel("JAIL QUALITY", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(2));
 
        JLabel lblSub = new JLabel("Sistema de Gestión Penitenciaria", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSub);
        panel.add(Box.createVerticalStrut(4));
 
        lblBienvenida = new JLabel(" ", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBienvenida.setForeground(new Color(30, 120, 200));
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblBienvenida);
        panel.add(Box.createVerticalStrut(4));
 
        return panel;
    }
 
    /** Actualiza la etiqueta de bienvenida con el usuario y tipo de cuenta. */
    private void actualizarCabecera() {
        lblBienvenida.setText(username != null
                ? "Bienvenido, " + username + "  [" + tipoCuenta + "]"
                : " ");
    }
 
    /** Carga el logo desde /assets/jailqualitylogobase.png y lo escala. */
    private JLabel cargarLogo(int ancho, int alto) {
        try {
            var stream = getClass().getResourceAsStream("/assets/jailqualitylogobase.png");
            if (stream != null) {
                BufferedImage original = ImageIO.read(stream);
                BufferedImage scaled   = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = scaled.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(original, 0, 0, ancho, alto, null);
                g2.dispose();
                return new JLabel(new ImageIcon(scaled));
            }
        } catch (Exception ignored) { }
        JLabel sustituto = new JLabel("JQ");
        sustituto.setFont(new Font("Segoe UI", Font.BOLD, 36));
        return sustituto;
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Panel de botones
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Panel central con los tres botones de módulo centrados horizontalmente.
     */
    private JPanel crearPanelBotones() {
        JPanel grid = new JPanel(new GridLayout(3, 1, 8, 8));
        grid.setBorder(BorderFactory.createTitledBorder("Módulos del sistema"));
 
        btnCuentas  = crearBotonModulo("🔑", "Gestión de Cuentas",
                "Administrar cuentas de policía y personal",
                () -> abrirVentana(new GestionCuentasGUI()));
 
        btnCarceles = crearBotonModulo("🏛", "Gestión de Cárceles",
                "Crear, consultar y eliminar centros penitenciarios",
                () -> abrirVentana(new GestionCarcelGUI()));
 
        btnPresos   = crearBotonModulo("👤", "Gestión de Presos",
                "Registrar, consultar y eliminar internos",
                () -> mostrarMenuPresos());
 
        grid.add(btnCuentas);
        grid.add(btnCarceles);
        grid.add(btnPresos);
 
        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.add(grid);
        return centrado;
    }
 
    /**
     * Crea un botón de módulo con icono, título, descripción e indicador derecho.
     * El estado habilitado/deshabilitado se gestiona con setBotonHabilitado().
     */
    private JButton crearBotonModulo(String icono, String titulo,
                                     String descripcion, Runnable accion) {
        JButton boton = new JButton();
        boton.setLayout(new BorderLayout(10, 0));
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(380, 72));
 
        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        lblIcono.setPreferredSize(new Dimension(40, 40));
        boton.add(lblIcono, BorderLayout.WEST);
 
        JPanel panelTexto = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTexto.setOpaque(false);
 
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
 
        JLabel lblDesc = new JLabel("<html>" + descripcion + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
 
        panelTexto.add(lblTitulo);
        panelTexto.add(lblDesc);
        boton.add(panelTexto, BorderLayout.CENTER);
 
        JLabel lblIndicador = new JLabel("›");
        lblIndicador.setFont(new Font("Segoe UI", Font.BOLD, 22));
        boton.add(lblIndicador, BorderLayout.EAST);
 
        // Guardamos referencias y la acción como propiedades del botón
        boton.putClientProperty("lblDesc",      lblDesc);
        boton.putClientProperty("lblTitulo",    lblTitulo);
        boton.putClientProperty("lblIndicador", lblIndicador);
        boton.putClientProperty("accion",       accion);
        boton.putClientProperty("descOriginal", descripcion);
        boton.setCursor(Cursor.getDefaultCursor());
 
        return boton;
    }
 
    /**
     * Habilita o deshabilita visualmente un botón y actualiza su acción.
     * Habilitado: fondo blanco, flecha azul, cursor de mano.
     * Deshabilitado: fondo gris, candado, sin acción.
     */
    private void setBotonHabilitado(JButton boton, boolean habilitar, String motivo) {
        JLabel lblDesc      = (JLabel)   boton.getClientProperty("lblDesc");
        JLabel lblTitulo    = (JLabel)   boton.getClientProperty("lblTitulo");
        JLabel lblIndicador = (JLabel)   boton.getClientProperty("lblIndicador");
        String descOriginal = (String)   boton.getClientProperty("descOriginal");
        Runnable accion     = (Runnable) boton.getClientProperty("accion");
 
        for (var l : boton.getActionListeners()) boton.removeActionListener(l);
 
        if (habilitar) {
            boton.setBackground(Color.WHITE);
            boton.setOpaque(true);
            lblTitulo.setForeground(Color.BLACK);
            lblDesc.setText("<html>" + descOriginal + "</html>");
            lblDesc.setForeground(new Color(100, 100, 100));
            lblIndicador.setText("›");
            lblIndicador.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblIndicador.setForeground(new Color(30, 120, 200));
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.addActionListener(e -> accion.run());
        } else {
            boton.setBackground(new Color(235, 238, 243));
            boton.setOpaque(true);
            lblTitulo.setForeground(new Color(150, 155, 165));
            lblDesc.setText("<html>" + motivo + "</html>");
            lblDesc.setForeground(new Color(170, 175, 185));
            lblIndicador.setText("🔒");
            lblIndicador.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            lblIndicador.setForeground(new Color(180, 185, 195));
            boton.setCursor(Cursor.getDefaultCursor());
        }
 
        boton.repaint();
    }
 
    /**
     * Recalcula qué botones están habilitados según el estado de sesión.
     * Cuentas: siempre. Cárceles: con sesión. Presos: con sesión y POLICIA.
     */
    private void actualizarBotones() {
        boolean sesion   = username != null;
        boolean policia  = "POLICIA".equals(tipoCuenta);
 
        setBotonHabilitado(btnCuentas,  true,           null);
        setBotonHabilitado(btnCarceles, sesion,         "Inicia sesión para acceder a este módulo");
        setBotonHabilitado(btnPresos,   sesion && policia,
                sesion ? "Solo accesible para cuentas de POLICIA"
                       : "Inicia sesión para acceder a este módulo");
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Panel de estado
    // ──────────────────────────────────────────────────────────────────────────
 
    /**
     * Panel inferior con el estado del servidor y el botón de sesión.
     */
    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Estado del servidor"));
 
        lblEstado = new JLabel("Comprobando...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lblEstado);
 
        JButton btnRefrescar = new JButton("Reconectar");
        btnRefrescar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> comprobarConexionServidor());
        panel.add(btnRefrescar);
 
        panel.add(new JLabel("|"));
 
        btnSesion = new JButton("Iniciar sesión");
        btnSesion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSesion.setForeground(new Color(26, 61, 111));
        btnSesion.setFocusPainted(false);
        btnSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSesion.addActionListener(e -> gestionarSesion());
        panel.add(btnSesion);
 
        return panel;
    }
 
    /** Actualiza el texto y color del botón de sesión. */
    private void actualizarBotonSesion() {
        if (username == null) {
            btnSesion.setText("Iniciar sesión");
            btnSesion.setForeground(new Color(26, 61, 111));
        } else {
            btnSesion.setText("Cerrar sesión");
            btnSesion.setForeground(new Color(180, 30, 30));
        }
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Lógica de sesión
    // ──────────────────────────────────────────────────────────────────────────
 
    private void gestionarSesion() {
        if (username == null) iniciarSesion();
        else                  cerrarSesion();
    }
 
    /**
     * Abre el diálogo de login. Si tiene éxito, actualiza la sesión y refresca la UI.
     */
    private void iniciarSesion() {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setVisible(true);
 
        if (dialog.isLoginCorrecto()) {
            username   = dialog.getUsername();
            tipoCuenta = dialog.getTipoCuenta();
            aplicarCambioSesion();
        }
    }
 
    /** Pide confirmación y cierra la sesión si el usuario acepta. */
    private void cerrarSesion() {
        int r = JOptionPane.showConfirmDialog(this,
                "¿Deseas cerrar la sesión de " + username + "?",
                "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            username   = null;
            tipoCuenta = null;
            aplicarCambioSesion();
        }
    }
 
    /** Aplica el nuevo estado de sesión a todos los componentes dinámicos. */
    private void aplicarCambioSesion() {
        actualizarCabecera();
        actualizarBotones();
        actualizarBotonSesion();
    }
 
    // ──────────────────────────────────────────────────────────────────────────
    // Navegación y conexión
    // ──────────────────────────────────────────────────────────────────────────
 
    /** Oculta esta ventana, abre el módulo indicado y vuelve al cerrarlo. */
    private void abrirVentana(JFrame ventana) {
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                setVisible(true);
                comprobarConexionServidor();
            }
        });
        setVisible(false);
        ventana.setVisible(true);
    }
 
    /**
     * Comprueba si el servidor está activo con un GET a /carcel.
     * Se ejecuta en un hilo secundario para no bloquear la UI.
     */
    private void comprobarConexionServidor() {
        lblEstado.setText("Comprobando...");
        lblEstado.setForeground(Color.DARK_GRAY);
 
        new Thread(() -> {
            boolean ok = false;
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/carcel"))
                        .GET().build();
                int codigo = httpClient.send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
                ok = codigo >= 200 && codigo < 300;
            } catch (Exception ignored) { }
 
            boolean conectado = ok;
            SwingUtilities.invokeLater(() -> {
                lblEstado.setText(conectado ? "✅ Conectado" : "❌ Sin conexión");
                lblEstado.setForeground(conectado ? new Color(0, 128, 0) : new Color(180, 30, 30));
            });
        }).start();
    }
    private void mostrarMenuPresos() {
        String[] opciones = {"Crear nuevo preso", "Listado / Modificar / Eliminar", "Cancelar"};

        int seleccion = JOptionPane.showOptionDialog(
                this,
                "Seleccione la operación que desea realizar en el módulo de presos:",
                "Gestión de Presos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        switch (seleccion) {
            case 0: // Crear nuevo preso
                abrirVentana(new GestionPresosGUI());
                break;
            case 1: // Listado / Modificar / Eliminar
                abrirVentana(new ListadoPresosGUI());
                break;
            default:
                // Si pulsa cancelar o cierra la ventana, no hace nada y vuelve al main
                break;
        }
    }
}