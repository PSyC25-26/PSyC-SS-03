package com.example.JailQ.Facade;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.GUI.GestionPresosGUI;
import com.example.JailQ.Service.PresoService;

@ExtendWith(MockitoExtension.class)
class PresoControllerTest {

    @Mock
    private PresoService presoService;

    @InjectMocks
    private PresoController presoController;

    @Test
    void testTrasladarPreso_Exitoso() {
        // Act
        ResponseEntity<?> respuesta = presoController.trasladarPreso(1, "Martutene");

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(presoService).trasladarPreso(1, "Martutene");
    }

    @Test
    void testTrasladarPreso_IllegalArgumentException() {
        // Arrange
        doThrow(new IllegalArgumentException("Preso no existe"))
                .when(presoService).trasladarPreso(99, "Martutene");

        // Act
        ResponseEntity<?> respuesta = presoController.trasladarPreso(99, "Martutene");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }

    @Test
    void testTrasladarPreso_ExceptionGeneral() {
        // Arrange
        doThrow(new RuntimeException("Error grave de BD"))
                .when(presoService).trasladarPreso(1, "CarcelError");

        // Act
        ResponseEntity<?> respuesta = presoController.trasladarPreso(1, "CarcelError");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
    }

    @Test
    void testModificarCondena_Exitoso() {
        // Arrange
        Preso presoSimulado = new Preso();
        when(presoService.modificarCondena(1, 10)).thenReturn(presoSimulado);

        // Act
        ResponseEntity<?> respuesta = presoController.modificarCondena(1, 10);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
    }

    @Test
    void testModificarCondena_IllegalArgumentException() {
        // Arrange
        when(presoService.modificarCondena(1, -5))
                .thenThrow(new IllegalArgumentException("Condena no válida"));

        // Act
        ResponseEntity<?> respuesta = presoController.modificarCondena(1, -5);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }

    @Test
    void testModificarCondena_ExceptionGeneral() {
        // Arrange
        when(presoService.modificarCondena(1, 5))
                .thenThrow(new RuntimeException("Crash"));

        // Act
        ResponseEntity<?> respuesta = presoController.modificarCondena(1, 5);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
    }


    @Test
    void testFiltrarPresosPorDelito_Exitoso() {
        // Arrange
        when(presoService.filtrarPorDelito(Delito.ROBO)).thenReturn(List.of(new Preso()));

        // Act
        ResponseEntity<?> respuesta = presoController.filtrarPresosPorDelito("ROBO");

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    }

    @Test
    void testFiltrarPresosPorDelito_DelitoInvalido() {
        // Act
        // Pasamos un String que no sea un Enum válido para forzar el catch de IllegalArgumentException
        ResponseEntity<?> respuesta = presoController.filtrarPresosPorDelito("DELITO_INVENTADO");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }

    @Test
    void testFiltrarPresosPorDelito_ExceptionGeneral() {
        // Arrange
        when(presoService.filtrarPorDelito(any())).thenThrow(new RuntimeException("Fallo en disco"));

        // Act
        ResponseEntity<?> respuesta = presoController.filtrarPresosPorDelito("HOMICIDIO");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
    }
        @Test
        public void testExtraerValorJson_campoSimple() throws Exception {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));

        java.lang.reflect.Method metodo = GestionPresosGUI.class
                .getDeclaredMethod("extraerValorJson", String.class, String.class);
        metodo.setAccessible(true);

        String json = "{\"nombre\":\"Michael\",\"apellidos\":\"Scofield\",\"condena\":5}";

        assertEquals("Michael", metodo.invoke(gui, json, "nombre"));
        assertEquals("Scofield", metodo.invoke(gui, json, "apellidos"));
        assertEquals("5", metodo.invoke(gui, json, "condena"));

        // Campo que no existe devuelve vacío
        assertEquals("", metodo.invoke(gui, json, "campoInexistente"));

        gui.dispose();
        }

        @Test
        public void testExtraerValorJson_ignoraCarcelAnidada() throws Exception {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));

        java.lang.reflect.Method metodo = GestionPresosGUI.class
                .getDeclaredMethod("extraerValorJson", String.class, String.class);
        metodo.setAccessible(true);

        // El campo "nombre" dentro de carcel NO debe interferir con el nombre del preso
        String json = "{\"nombre\":\"Juan\",\"carcel\":{\"nombre\":\"Martutene\",\"id\":1}}";

        assertEquals("Juan", metodo.invoke(gui, json, "nombre"));

        gui.dispose();
        }

        @Test
        public void testExtraerDelitosJson() throws Exception {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));

        java.lang.reflect.Method metodo = GestionPresosGUI.class
                .getDeclaredMethod("extraerDelitosJson", String.class);
        metodo.setAccessible(true);

        // Con delitos
        String json = "{\"nombre\":\"Juan\",\"delitos\":[\"ROBO\",\"HOMICIDIO\"]}";
        String resultado = (String) metodo.invoke(gui, json);
        assertTrue(resultado.contains("ROBO"));
        assertTrue(resultado.contains("HOMICIDIO"));

        // Sin delitos (array vacío)
        String jsonVacio = "{\"nombre\":\"Juan\",\"delitos\":[]}";
        String resultadoVacio = (String) metodo.invoke(gui, jsonVacio);
        assertNotNull(resultadoVacio);

        // Sin campo delitos
        String jsonSinDelitos = "{\"nombre\":\"Juan\"}";
        assertEquals("—", metodo.invoke(gui, jsonSinDelitos));

        gui.dispose();
        }

        @Test
        public void testExtraerNombreCarcelJson() throws Exception {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));

        java.lang.reflect.Method metodo = GestionPresosGUI.class
                .getDeclaredMethod("extraerNombreCarcelJson", String.class);
        metodo.setAccessible(true);

        // Con cárcel
        String json = "{\"nombre\":\"Juan\",\"carcel\":{\"idCarcel\":1,\"nombre\":\"Martutene\"}}";
        assertEquals("Martutene", metodo.invoke(gui, json));

        // Sin cárcel
        String jsonSinCarcel = "{\"nombre\":\"Juan\"}";
        assertEquals("", metodo.invoke(gui, jsonSinCarcel));

        gui.dispose();
        }

        @Test
        public void testDividirObjetos() throws Exception {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));

        java.lang.reflect.Method metodo = GestionPresosGUI.class
                .getDeclaredMethod("dividirObjetos", String.class);
        metodo.setAccessible(true);

        // Array con dos objetos
        String json = "[{\"nombre\":\"Juan\"},{\"nombre\":\"Pedro\"}]";
        @SuppressWarnings("unchecked")
        java.util.List<String> resultado = (java.util.List<String>) metodo.invoke(gui, json);
        assertEquals(2, resultado.size());

        // Array vacío
        @SuppressWarnings("unchecked")
        java.util.List<String> vacio = (java.util.List<String>) metodo.invoke(gui, "[]");
        assertEquals(0, vacio.size());

        gui.dispose();
        }

        @Test
        public void testBuscarEnJson_encontrado() throws Exception {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("POLICIA"));

        java.lang.reflect.Method metodo = GestionPresosGUI.class
                .getDeclaredMethod("buscarEnJson", String.class, String.class, String.class);
        metodo.setAccessible(true);

        String json = "[{\"nombre\":\"Michael\",\"apellidos\":\"Scofield\",\"condena\":5}," +
                        "{\"nombre\":\"Lincoln\",\"apellidos\":\"Burrows\",\"condena\":3}]";

        // Encontrado
        String resultado = (String) metodo.invoke(gui, json, "Michael", "Scofield");
        assertNotNull(resultado);
        assertTrue(resultado.contains("Michael"));

        // No encontrado devuelve null
        assertNull(metodo.invoke(gui, json, "Inexistente", "Apellido"));

        gui.dispose();
        }

        @Test
        public void testModoFamiliaComponentesVisibles() {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("FAMILIA"));
        FrameFixture window = new FrameFixture(gui);
        window.show();

        window.textBox("txtBusquedaNombre").requireVisible();
        window.textBox("txtBusquedaApellidos").requireVisible();
        window.button("btnBuscar").requireVisible();
        window.label("lblEstadoBusqueda").requireVisible();

        window.cleanUp();
        GuiActionRunner.execute(() -> gui.dispose());
        }

        @Test
        public void testModoFamilia_buscarSinDatos_muestraError() {
        GestionPresosGUI gui = GuiActionRunner.execute(() -> new GestionPresosGUI("FAMILIA"));
        FrameFixture window = new FrameFixture(gui);
        window.show();

        // Click sin rellenar campos
        window.button("btnBuscar").click();
        window.label("lblEstadoBusqueda")
                .requireText("Introduce nombre y apellidos para buscar.");

        window.cleanUp();
        GuiActionRunner.execute(() -> gui.dispose());
        }
}