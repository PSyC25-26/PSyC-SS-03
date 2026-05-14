package com.example.JailQ.GUI;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EliminarPoliciaGUITest {

    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void setUp() {
        // Ejecutamos la ventana. Servidor backend encendido 
        EliminarPoliciaGUI frame = GuiActionRunner.execute(() -> new EliminarPoliciaGUI());
        window = new FrameFixture(frame);
        window.show(); 
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testComponentesCarganCorrectamente() {
        // Comprueba que los elementos esenciales (botones, lista y estado) están visibles
        window.list("listaPolicias").requireVisible();
        window.button("btnRecargar").requireVisible().requireText("Recargar");
        window.button("btnEliminar").requireVisible().requireText("Eliminar seleccionado");
        window.label("lblEstado").requireVisible();
    }

    @Test
    public void testEliminarSinSeleccionarMuestraAviso() {
        // Nos aseguramos de que no hay nada seleccionado en la lista
        window.list("listaPolicias").clearSelection();
        
        // Hacemos clic en el botón de eliminar
        window.button("btnEliminar").click();
        
        // Comprobamos que el robot detecta el JOptionPane de advertencia
        window.optionPane().requireWarningMessage().requireMessage("Selecciona una cuenta de policía.");
        
        // El robot le da a "OK" para cerrar el mensaje y que el test termine limpiamente
        window.optionPane().okButton().click();
    }

    @Test
    public void testEliminarPoliciaYDarleACancelar() {
        // Comprobamos si la base de datos ha cargado algún policía en la lista
        if (window.list("listaPolicias").contents().length > 0) {
            // Seleccionamos el primero
            window.list("listaPolicias").selectItem(0);
            window.button("btnEliminar").click();
            
            // Verificamos que sale la pregunta
            window.optionPane().requireQuestionMessage();
            
            // El robot hace clic en "NO"
            window.optionPane().noButton().click();
            
            // La lista debe seguir intacta y visible
            window.list("listaPolicias").requireVisible();
        }
    }

    @Test
    public void testEliminarPoliciaYConfirmarConSi() {
        if (window.list("listaPolicias").contents().length > 0) {
            window.list("listaPolicias").selectItem(0);
            window.button("btnEliminar").click();
            
            // Le damos al botón "Sí"
            window.optionPane().yesButton().click(); 
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            window.optionPane().okButton().click();
        }
    }

    @Test
    public void testBotonRecargarActualizaLista() {
        // Hacemos clic en el botón de recargar para cubrir su ActionListener
        window.button("btnRecargar").click();
        
        // Le damos medio segundo al servidor para responder a la petición GET
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        // Verificamos que la interfaz no se ha bloqueado y la lista sigue visible
        window.list("listaPolicias").requireVisible();
    }

    @Test
    public void testEliminarPoliciaFallaConErrorDelServidor() {
        // 1. Obtenemos la lista real de Swing y le metemos un policía falso
        javax.swing.JList lista = window.list("listaPolicias").target();
        
        GuiActionRunner.execute(() -> {
            javax.swing.DefaultListModel modelo = (javax.swing.DefaultListModel) lista.getModel();
            modelo.addElement("ID: 9999 | Fake | user: falso");
        });

        // 2. Hacemos que el robot seleccione a nuestro policía infiltrado
        window.list("listaPolicias").selectItem("ID: 9999 | Fake | user: falso");
        window.button("btnEliminar").click();
        
        // 3. Confirmamos el borrado
        window.optionPane().yesButton().click();
        
        // 4. El servidor buscará el ID 9999, no lo encontrará y devolverá un ERROR.
        // Esto nos permite cubrir la rama 'else' del código.
        window.optionPane().requireMessage(java.util.regex.Pattern.compile("(?s).*Código:.*"));
        window.optionPane().okButton().click();
    }

    @Test
    public void testExtraerValorCasosLimiteConReflexion() {
        // Como 'extraerValor' es un método privado y el servidor siempre envía JSON perfectos,
        // usamos Reflexión para atacarlo directamente con "basura" y cubrir sus validaciones de seguridad.
        GuiActionRunner.execute(() -> {
            try {
                java.lang.reflect.Method metodo = EliminarPoliciaGUI.class.getDeclaredMethod("extraerValor", String.class, String.class);
                metodo.setAccessible(true);
                
                // Caso A: Un JSON válido pero que NO tiene la clave que buscamos
                String sinClave = (String) metodo.invoke(window.target(), "{\"otraClave\":\"valor\"}", "idCuentas");
                org.junit.jupiter.api.Assertions.assertEquals("", sinClave);
                
                // Caso B: Un texto que ni siquiera tiene los dos puntos (:) para que falle el 'partes.length == 2'
                String malformado = (String) metodo.invoke(window.target(), "texto_sin_formato_json", "idCuentas");
                org.junit.jupiter.api.Assertions.assertEquals("", malformado);
                
            } catch (Exception e) {
                org.junit.jupiter.api.Assertions.fail("La reflexión no debería fallar: " + e.getMessage());
            }
        });
    }
}