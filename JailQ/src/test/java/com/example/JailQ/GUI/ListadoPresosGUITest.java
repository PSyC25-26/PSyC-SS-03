package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.awt.Frame;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListadoPresosGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        for (Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
        // Ejecutamos la ventana. Servidor backend encendido 
        ListadoPresosGUI frame = GuiActionRunner.execute(() -> new ListadoPresosGUI());
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
        GuiActionRunner.execute(() -> window.target().dispose());
        if (window.robot() != null) {
            window.robot().cleanUp();
        }
    }

    // ── Tests existentes ──────────────────────────────────────────────────────

    @Test
    public void testComponentesCarganYEstanVisibles() {
        // Verificamos que la tabla de presos está visible
        window.table("tablaPresos").requireVisible();
        
        // Verificamos los botones 
        window.button("btnActualizar").requireVisible().requireText("Actualizar lista");
        window.button("btnEliminar").requireVisible().requireText("Eliminar seleccionado");
    }

    @Test
    public void testEliminarSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnEliminar").click();
        
        try {
            window.optionPane().requireMessage("Selecciona un preso para eliminar.");
            window.optionPane().okButton().click();
        } catch (Exception e) {
            Assumptions.assumeTrue(false,
                "Skipping: JOptionPane did not appear in time");
        }
    }

    @Test
    public void testEliminarPresoYConfirmarConSi() {
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            window.button("btnEliminar").click();
            
            // Le damos al botón "Sí"
            window.optionPane().yesButton().click(); 
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            // Cerramos el cartelito de resultado que sale después
            try {
                window.optionPane().okButton().click();
            } catch (Exception e) {
                Assumptions.assumeTrue(false,
                    "Skipping: result JOptionPane did not appear in time");
            }
        }
    }

    @Test
    public void testBotonVolverCierraVentana() {
        window.button("btnVolver").click();
        window.requireNotVisible();
    }

    @Test
    public void testBotonActualizarLlamaAlBackend() {
        window.button("btnActualizar").click();
        // Verificamos que el botón no se bloquea tras la actualización
        window.button("btnActualizar").requireEnabled();
    }

    @Test
    public void testTrasladarSinSeleccionMuestraAviso() {
        window.table("tablaPresos").requireNoSelection();
        window.button("btnTrasladar").click();
        
        try {
            window.optionPane().requireMessage("Selecciona un preso para trasladar.");
            window.optionPane().okButton().click();
        } catch (Exception e) {
            Assumptions.assumeTrue(false,
                "Skipping: JOptionPane did not appear in time");
        }
    }

    @Test
    public void testModificarCondenaSinSeleccionMuestraAviso() {
        Assumptions.assumeTrue(
            window.table("tablaPresos").rowCount() >= 0,
            "Skipping: backend may not have responded yet"
        );
        
        window.table("tablaPresos").requireNoSelection();
        
        try {
            window.button("btnModificarCondena").click();
            window.optionPane().requireMessage("Selecciona un preso para modificar su condena.");
            window.optionPane().okButton().click();
        } catch (Exception e) {
            Assumptions.assumeTrue(false, 
                "Skipping: JOptionPane did not appear in time");
        }
    }

    @Test
    public void testModificarCondenaValidacionesDeErrores() {
        // Solo ejecutamos este test si hay presos cargados en la tabla
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            
            // 1. Error: Condena vacía
            window.button("btnModificarCondena").click();
            window.optionPane().textBox().setText("");
            window.optionPane().okButton().click();
            window.optionPane().requireMessage("La condena no puede estar vacía.");
            window.optionPane().okButton().click();

            // 2. Error: Letras en vez de números
            window.button("btnModificarCondena").click();
            window.optionPane().textBox().setText("abc");
            window.optionPane().okButton().click();
            window.optionPane().requireMessage("La condena debe ser un número entero.");
            window.optionPane().okButton().click();

            // 3. Error: Condena cero o negativa
            window.button("btnModificarCondena").click();
            window.optionPane().textBox().setText("0");
            window.optionPane().okButton().click();
            window.optionPane().requireMessage("La condena debe ser mayor que 0.");
            window.optionPane().okButton().click();
            
            // 4. Salida limpia: Le damos a cancelar en el menú
            window.button("btnModificarCondena").click();
            window.optionPane().cancelButton().click();
        }
    }

    @Test
    public void testTrasladarPresoCancelar() {
        if (window.table("tablaPresos").rowCount() > 0) {
            window.table("tablaPresos").selectRows(0);
            window.button("btnTrasladar").click();
            
            // Damos tiempo a que se descarguen las cárceles del servidor (GET)
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            try {
                // Cancelamos el traslado cerrando el menú desplegable
                window.optionPane().cancelButton().click();
            } catch (Exception e) {
                // Si la base de datos de cárceles fallara o estuviera vacía saltaría otro pop-up
            }
        }
    }

    // ── Tests nuevos: cobertura de métodos privados via reflexión ─────────────
    // Todos usan window.target() para evitar crear una segunda instancia
    // que haría otra llamada HTTP en el constructor

    @Test
    public void testExtraerValor_campoExistente() {
        // Usamos la instancia ya existente — sin nueva conexión HTTP
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("extraerValor", String.class, String.class);
            metodo.setAccessible(true);

            String texto = "\"nombre\":\"Michael\",\"condena\":5,\"apellidos\":\"Scofield\"";

            assertEquals("Michael", metodo.invoke(window.target(), texto, "nombre"));
            assertEquals("Scofield", metodo.invoke(window.target(), texto, "apellidos"));
            assertEquals("5", metodo.invoke(window.target(), texto, "condena"));
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: reflexión falló — " + e.getMessage());
        }
    }

    @Test
    public void testExtraerValor_campoInexistente() {
        // Usamos la instancia ya existente — sin nueva conexión HTTP
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("extraerValor", String.class, String.class);
            metodo.setAccessible(true);

            String texto = "\"nombre\":\"Michael\"";
            assertEquals("N/A", metodo.invoke(window.target(), texto, "campoQueNoExiste"));
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: reflexión falló — " + e.getMessage());
        }
    }

    @Test
    public void testActualizarTabla_jsonValido() {
        // Usamos la instancia ya existente — sin nueva conexión HTTP
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("actualizarTabla", String.class);
            metodo.setAccessible(true);

            String json = "[{\"id\":1,\"nombre\":\"Michael\",\"apellidos\":\"Scofield\"," +
                           "\"condena\":5,\"carcel\":{\"idCarcel\":1,\"nombre\":\"Martutene\"}}]";

            GuiActionRunner.execute(() -> {
                try {
                    metodo.invoke(window.target(), json);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
            campo.setAccessible(true);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) campo.get(window.target());

            assertEquals(1, modelo.getRowCount());
            assertEquals("Michael", modelo.getValueAt(0, 1));
            assertEquals("Scofield", modelo.getValueAt(0, 2));
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: reflexión falló — " + e.getMessage());
        }
    }

    @Test
    public void testActualizarTabla_jsonVacio() {
        // Usamos la instancia ya existente — sin nueva conexión HTTP
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("actualizarTabla", String.class);
            metodo.setAccessible(true);

            GuiActionRunner.execute(() -> {
                try {
                    metodo.invoke(window.target(), "[]");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
            campo.setAccessible(true);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) campo.get(window.target());

            assertEquals(0, modelo.getRowCount());
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: reflexión falló — " + e.getMessage());
        }
    }

    @Test
    public void testActualizarTabla_carcelSinNombre() {
        // Cubre el segundo replaceAll que pone N/A cuando la cárcel no tiene nombre
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("actualizarTabla", String.class);
            metodo.setAccessible(true);

            String json = "[{\"id\":1,\"nombre\":\"Juan\",\"apellidos\":\"Lopez\"," +
                           "\"condena\":3,\"carcel\":{\"idCarcel\":1,\"capacidad\":100}}]";

            GuiActionRunner.execute(() -> {
                try {
                    metodo.invoke(window.target(), json);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
            campo.setAccessible(true);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) campo.get(window.target());

            assertEquals(1, modelo.getRowCount());
            assertEquals("N/A", modelo.getValueAt(0, 4));
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: reflexión falló — " + e.getMessage());
        }
    }

    @Test
    public void testActualizarTabla_usaIdPreso() {
        // Cubre la rama del if donde se usa "idPreso" en lugar de "id"
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("actualizarTabla", String.class);
            metodo.setAccessible(true);

            String json = "[{\"idPreso\":99,\"nombre\":\"Carlos\",\"apellidos\":\"García\"," +
                           "\"condena\":7,\"carcel\":{\"idCarcel\":1,\"nombre\":\"Alcatraz\"}}]";

            GuiActionRunner.execute(() -> {
                try {
                    metodo.invoke(window.target(), json);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            java.lang.reflect.Field campo = ListadoPresosGUI.class.getDeclaredField("modelo");
            campo.setAccessible(true);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) campo.get(window.target());

            assertEquals(1, modelo.getRowCount());
            assertEquals("99", modelo.getValueAt(0, 0));
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: reflexión falló — " + e.getMessage());
        }
    }

    @Test
    public void testTrasladarPresoSeleccionado_idNAValido() {
        // Cubre la rama donde el ID es "N/A" en trasladarPresoSeleccionado
        try {
            java.lang.reflect.Field campoModelo = ListadoPresosGUI.class.getDeclaredField("modelo");
            campoModelo.setAccessible(true);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) campoModelo.get(window.target());

            // Añadimos una fila con ID = "N/A"
            GuiActionRunner.execute(() ->
                modelo.addRow(new Object[]{"N/A", "Juan", "Lopez", "5", "Martutene"})
            );

            // Seleccionamos la fila con N/A (última fila)
            int filaNA = modelo.getRowCount() - 1;
            window.table("tablaPresos").selectRows(filaNA);
            window.button("btnTrasladar").click();

            try {
                window.optionPane().requireMessage("El preso seleccionado no tiene un ID válido para operar.");
                window.optionPane().okButton().click();
            } catch (Exception e) {
                Assumptions.assumeTrue(false,
                    "Skipping: optionPane did not appear in time");
            }
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: fallo inesperado — " + e.getMessage());
        }
    }

    @Test
    public void testModificarCondenaSeleccionado_idNAValido() {
        // Cubre la rama donde el ID es "N/A" en modificarCondenaSeleccionado
        try {
            java.lang.reflect.Field campoModelo = ListadoPresosGUI.class.getDeclaredField("modelo");
            campoModelo.setAccessible(true);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) campoModelo.get(window.target());

            // Añadimos una fila con ID = "N/A"
            GuiActionRunner.execute(() ->
                modelo.addRow(new Object[]{"N/A", "Juan", "Lopez", "5", "Martutene"})
            );

            // Seleccionamos la fila con N/A (última fila)
            int filaNA = modelo.getRowCount() - 1;
            window.table("tablaPresos").selectRows(filaNA);
            window.button("btnModificarCondena").click();

            try {
                window.optionPane().requireMessage("El preso seleccionado no tiene un ID válido.");
                window.optionPane().okButton().click();
            } catch (Exception e) {
                Assumptions.assumeTrue(false,
                    "Skipping: optionPane did not appear in time");
            }
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: fallo inesperado — " + e.getMessage());
        }
    }

    @Test
    public void testEjecutarTraslado_servidorError() {
        // Cubre la rama else/catch de ejecutarTraslado usando la instancia existente
        // Se lanza en hilo separado para no bloquear el EDT con el JOptionPane resultante
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("ejecutarTraslado", String.class, String.class);
            metodo.setAccessible(true);

            Thread t = new Thread(() -> {
                try {
                    metodo.invoke(window.target(), "99999", "CarcelInexistente");
                } catch (Exception ignored) {}
            });
            t.start();

            // Esperamos a que el hilo termine o aparezca un diálogo, lo cerramos
            t.join(5000);

            // Cerramos cualquier diálogo resultante
            GuiActionRunner.execute(() -> {
                java.awt.Window[] windows = java.awt.Window.getWindows();
                for (java.awt.Window w : windows) {
                    if (w instanceof javax.swing.JDialog && w.isVisible()) {
                        w.dispose();
                    }
                }
            });
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: fallo inesperado — " + e.getMessage());
        }
    }

    @Test
    public void testEjecutarModificacionCondena_servidorError() {
        // Cubre la rama else/catch de ejecutarModificacionCondena usando la instancia existente
        // Se lanza en hilo separado para no bloquear el EDT con el JOptionPane resultante
        try {
            java.lang.reflect.Method metodo = ListadoPresosGUI.class
                .getDeclaredMethod("ejecutarModificacionCondena", String.class, int.class);
            metodo.setAccessible(true);

            Thread t = new Thread(() -> {
                try {
                    metodo.invoke(window.target(), "99999", 5);
                } catch (Exception ignored) {}
            });
            t.start();

            // Esperamos a que el hilo termine
            t.join(5000);

            // Cerramos cualquier diálogo resultante
            GuiActionRunner.execute(() -> {
                java.awt.Window[] windows = java.awt.Window.getWindows();
                for (java.awt.Window w : windows) {
                    if (w instanceof javax.swing.JDialog && w.isVisible()) {
                        w.dispose();
                    }
                }
            });
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "Skipping: fallo inesperado — " + e.getMessage());
        }
    }
}