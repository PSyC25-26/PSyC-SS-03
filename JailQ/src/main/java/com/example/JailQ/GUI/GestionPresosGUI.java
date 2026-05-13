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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Carcel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GestionPresosGUI extends JFrame {

    private final HttpClient cliente;
    private final String     tipoCuenta;

    // ── Campos modo POLICIA ───────────────────────────────────────────────────
    private JTextField        txtNombre, txtApellidos, txtFechaNacimiento, txtCondena;
    private JComboBox<String> cbCarcel;
    private JTextArea         txtConsola;
    private JList<Delito>     listaDelitos;

    // Diccionario para guardar Nombre -> ID de la cárcel
    private Map<String, Integer> mapaCarceles = new HashMap<>();

    // ── Campos modo FAMILIA ───────────────────────────────────────────────────
    private JTextField txtBusquedaNombre;
    private JTextField txtBusquedaApellidos;
    private JPanel     panelResultado;
    private JLabel     lblEstadoBusqueda;

    // ──────────────────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Construye la ventana en el modo correspondiente al tipo de cuenta.
     *
     * @param tipoCuenta "POLICIA" para gestión completa, "FAMILIA" para consulta.
     */
    public GestionPresosGUI(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
        cliente = HttpClientSingleton.getInstance().getClient();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        if ("FAMILIA".equals(tipoCuenta)) {
            iniciarModoFamilia();
        } else {
            iniciarModoPolicia();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Modo FAMILIA: búsqueda por nombre + apellidos
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Configura la ventana en modo FAMILIA: solo muestra un formulario de
     * búsqueda por nombre y apellidos y presenta los datos del preso encontrado.
     */
    private void iniciarModoFamilia() {
        setTitle("JailQ - Consulta de Preso");
        setSize(480, 500);

        // ── Panel de búsqueda ──────────────────────────────────────────────
        JPanel panelBusqueda = new JPanel(new GridLayout(5, 1, 6, 6));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar preso"));

        panelBusqueda.add(new JLabel("Nombre:"));
        txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setName("txtBusquedaNombre");
        txtBusquedaNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelBusqueda.add(txtBusquedaNombre);

        panelBusqueda.add(new JLabel("Apellidos:"));
        txtBusquedaApellidos = new JTextField();
        txtBusquedaApellidos.setName("txtBusquedaApellidos");
        txtBusquedaApellidos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelBusqueda.add(txtBusquedaApellidos);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setName("btnBuscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarPresoPorNombreApellidos());
        panelBusqueda.add(btnBuscar);

        // ── Panel de resultado ─────────────────────────────────────────────
        panelResultado = new JPanel(new GridLayout(0, 2, 8, 8));
        panelResultado.setBorder(BorderFactory.createTitledBorder("Datos del preso"));
        panelResultado.setVisible(false);

        // ── Estado ────────────────────────────────────────────────────────
        lblEstadoBusqueda = new JLabel(" ", SwingConstants.CENTER);
        lblEstadoBusqueda.setName("lblEstadoBusqueda");
        lblEstadoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // ── Botón volver ──────────────────────────────────────────────────
        JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setName("btnVolverFam");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> dispose());

        JPanel panelSur = new JPanel(new BorderLayout(5, 5));
        panelSur.add(lblEstadoBusqueda, BorderLayout.NORTH);
        panelSur.add(btnVolver,         BorderLayout.SOUTH);

        add(panelBusqueda,                   BorderLayout.NORTH);
        add(new JScrollPane(panelResultado), BorderLayout.CENTER);
        add(panelSur,                        BorderLayout.SOUTH);
    }

    /**
     * Busca un preso por nombre y apellidos exactos (sin distinguir mayúsculas)
     * descargando la lista completa de GET /preso/todos y filtrando en cliente.
     * Si encuentra coincidencia, muestra todos los datos del preso.
     */
    private void buscarPresoPorNombreApellidos() {
        String nombre    = txtBusquedaNombre.getText().trim();
        String apellidos = txtBusquedaApellidos.getText().trim();

        if (nombre.isEmpty() || apellidos.isEmpty()) {
            lblEstadoBusqueda.setForeground(Color.RED);
            lblEstadoBusqueda.setText("Introduce nombre y apellidos para buscar.");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/preso/todos"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    cliente.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                lblEstadoBusqueda.setForeground(Color.RED);
                lblEstadoBusqueda.setText("Error del servidor: " + response.statusCode());
                return;
            }

            String presoJson = buscarEnJson(response.body(), nombre, apellidos);

            if (presoJson == null) {
                panelResultado.setVisible(false);
                lblEstadoBusqueda.setForeground(Color.RED);
                lblEstadoBusqueda.setText("No se encontró ningún preso con ese nombre y apellidos.");
            } else {
                mostrarDatosPreso(presoJson);
                lblEstadoBusqueda.setForeground(new Color(0, 128, 0));
                lblEstadoBusqueda.setText("Preso encontrado.");
            }

        } catch (Exception ex) {
            lblEstadoBusqueda.setForeground(Color.RED);
            lblEstadoBusqueda.setText("No se pudo conectar con el servidor.");
        }
    }

    /**
     * Recorre el array JSON buscando el objeto cuyo campo "nombre" y "apellidos"
     * coincidan con los términos buscados (ignorando mayúsculas).
     *
     * Usa conteo de llaves en lugar de regex para manejar correctamente los
     * objetos anidados (p.ej. el campo "carcel":{...}) sin que interfieran
     * con la separación entre presos.
     *
     * @param json      Respuesta completa de GET /preso/todos
     * @param nombre    Nombre a buscar
     * @param apellidos Apellidos a buscar
     * @return El bloque JSON del preso encontrado, o null si no existe
     */
    private String buscarEnJson(String json, String nombre, String apellidos) {
        for (String objeto : dividirObjetos(json)) {
            String nombreJson    = extraerValorJson(objeto, "nombre");
            String apellidosJson = extraerValorJson(objeto, "apellidos");
            if (nombreJson.equalsIgnoreCase(nombre)
                    && apellidosJson.equalsIgnoreCase(apellidos)) {
                return objeto;
            }
        }
        return null;
    }

    /**
     * Divide el JSON del array en objetos individuales contando llaves de
     * apertura y cierre, lo que permite manejar correctamente cualquier nivel
     * de anidamiento sin que los objetos internos (como "carcel") rompan el parseo.
     *
     * @param json String con el array JSON completo
     * @return Lista de bloques JSON, uno por preso
     */
    private List<String> dividirObjetos(String json) {
        List<String> resultado = new ArrayList<>();
        int nivel  = 0;
        int inicio = -1;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (nivel == 0) inicio = i;
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0 && inicio != -1) {
                    resultado.add(json.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return resultado;
    }

    /**
     * Parsea el JSON del preso y rellena el panel de resultado con
     * todos sus campos en formato etiqueta-valor.
     *
     * @param presoJson Bloque JSON de un único preso
     */
    private void mostrarDatosPreso(String presoJson) {
        panelResultado.removeAll();

        agregarFilaResultado("Nombre",           extraerValorJson(presoJson, "nombre"));
        agregarFilaResultado("Apellidos",        extraerValorJson(presoJson, "apellidos"));
        agregarFilaResultado("Fecha nacimiento", extraerValorJson(presoJson, "fechaNacimiento"));
        agregarFilaResultado("Fecha ingreso",    extraerValorJson(presoJson, "fechaIngreso"));
        agregarFilaResultado("Condena (años)",   extraerValorJson(presoJson, "condena"));
        agregarFilaResultado("Delitos",          extraerDelitosJson(presoJson));
        agregarFilaResultado("Cárcel",           extraerNombreCarcelJson(presoJson));

        panelResultado.setVisible(true);
        panelResultado.revalidate();
        panelResultado.repaint();
    }

    /** Añade una fila etiqueta-valor al panel de resultado. */
    private void agregarFilaResultado(String etiqueta, String valor) {
        JLabel lbl = new JLabel(etiqueta + ":");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel val = new JLabel(valor.isEmpty() ? "—" : valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panelResultado.add(lbl);
        panelResultado.add(val);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Modo POLICIA: formulario completo de registro
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Configura la ventana en modo POLICIA: muestra el formulario completo
     * para registrar nuevos presos, idéntico al comportamiento anterior.
     */
    private void iniciarModoPolicia() {
        setTitle("JailQ - Gestión de Presos");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelFormulario = new JPanel(new GridLayout(6, 1, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Preso"));

        txtNombre = new JTextField();
        txtNombre.setName("txtNombre"); 
        
        txtApellidos = new JTextField();
        txtApellidos.setName("txtApellidos"); 
        
        txtFechaNacimiento = new JTextField();
        txtFechaNacimiento.setName("txtFechaNacimiento"); 
        
        txtCondena = new JTextField();
        txtCondena.setName("txtCondena"); 

        cbCarcel = new JComboBox<>();
        cbCarcel.setName("cbCarcel"); 
        //Cargamos las cárceles desde la base de datos al iniciar
        cargarCarcelesDesdeBD();

        listaDelitos = new JList<>(Delito.values());
        listaDelitos.setName("listaDelitos"); 
        listaDelitos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollDelitos = new JScrollPane(listaDelitos);
        scrollDelitos.setPreferredSize(new Dimension(0, 150));

        panelFormulario.add(crearCelda("Nombre:", txtNombre));
        panelFormulario.add(crearCelda("Apellidos:", txtApellidos));
        panelFormulario.add(crearCelda("Fecha Nacimiento (AAAA-MM-DD):", txtFechaNacimiento));
        panelFormulario.add(crearCelda("Condena (años):", txtCondena));
        panelFormulario.add(crearCeldaPanel("Cárcel:", cbCarcel));
        panelFormulario.add(crearCeldaPanel("Delito:", scrollDelitos));

        JButton btnAnadir = new JButton("Registrar Preso");
        btnAnadir.setName("btnAnadir");
        btnAnadir.addActionListener(e -> enviarPreso());

        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.add(panelFormulario, BorderLayout.CENTER);
        panelCentral.add(btnAnadir, BorderLayout.SOUTH);

        txtConsola = new JTextArea(5, 30);
        txtConsola.setName("txtConsola"); 
        txtConsola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Estado del Servidor"));

        // ── Botón Volver ──────────────────────────────────────────────────────
        JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setName("btnVolver"); 
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

            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

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
            
            //Enviamos el ID como Integer, que es lo que espera el Backend
            jsonMap.put("carcel", idCarcel);

            String jsonBody = mapper.writeValueAsString(jsonMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/preso/crear"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

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

    // ──────────────────────────────────────────────────────────────────────────
    // Utilidades de extracción JSON (modo FAMILIA)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Extrae el valor de un campo simple del JSON ignorando el bloque
     * "carcel":{...} para evitar capturar campos del objeto anidado.
     */
    private String extraerValorJson(String json, String campo) {
        // Eliminamos el bloque carcel:{...} antes de buscar para evitar
        // que el regex capture campos del objeto anidado (ej. "nombre":"Oiar")
        String jsonSinCarcel = json.replaceAll("\"carcel\"\\s*:\\s*\\{[^}]*\\}", "");
        Pattern p = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"?([^,\"\\}\\]]+)\"?");
        Matcher m = p.matcher(jsonSinCarcel);
        if (m.find()) {
            return m.group(1).trim().replace("\"", "");
        }
        return "";
    }

    /** Extrae el array de delitos del JSON y lo formatea separado por comas. */
    private String extraerDelitosJson(String json) {
        Pattern p = Pattern.compile("\"delitos\"\\s*:\\s*\\[([^\\]]*)\\]");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).replaceAll("\"", "").replace(",", ", ").trim();
        }
        return "—";
    }

    /** Extrae el nombre de la cárcel del objeto anidado "carcel":{...}. */
    private String extraerNombreCarcelJson(String json) {
        Pattern p = Pattern.compile("\"carcel\"\\s*:\\s*\\{[^}]*\"nombre\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Utilidades de layout (modo POLICIA)
    // ──────────────────────────────────────────────────────────────────────────

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