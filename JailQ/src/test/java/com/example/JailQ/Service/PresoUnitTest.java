package com.example.JailQ.Service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Entidades.Delito;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
/**
 * Pruebas unitarias para la clase {@link Preso}.
 * <p>
 * Esta clase verifica la correcta funcionalidad de los métodos
 * de manejo de delitos en un objeto {@link Preso}.
 * Se utilizan pruebas con JUnit 5.
 * </p>
 */
class PresoUnitTest {

    /**
     * Instancia de {@link Preso} utilizada en las pruebas.
     */
    private Preso preso;

    /**
     * Configuración inicial antes de cada prueba.
     * <p>
     * Se crea un objeto {@link Preso} y se inicializa su lista de delitos vacía.
     * </p>
     */
    @BeforeEach
    void setUp() {
        preso = new Preso();
        preso.setDelitos(new ArrayList<>());
    }

    /**
     * Prueba que verifica que se puede agregar un delito válido a un {@link Preso}.
     * <p>
     * Se agrega el delito {@link Delito#ROBO} y se comprueba que la lista de delitos contiene dicho delito.
     * </p>
     */
    @Test
    @DisplayName("Agregar un delito válido")
    void testAddDelito_Valido() {
        preso.addDelito(Delito.ROBO);

        assertEquals(1, preso.getDelitos().size());
        assertTrue(preso.getDelitos().contains(Delito.ROBO));
    }

    /**
     * Prueba que verifica que agregar un delito {@code null} no modifica la lista de delitos.
     */
    @Test
    @DisplayName("Agregar un delito null no modifica la lista")
    void testAddDelito_Null() {
        preso.addDelito(null);

        assertEquals(0, preso.getDelitos().size());
    }

    /**
     * Prueba que verifica que se puede eliminar un delito existente de un {@link Preso}.
     * <p>
     * Se agrega un delito {@link Delito#HOMICIDIO} y luego se elimina.
     * Se comprueba que la operación devuelve {@code true} y que la lista queda vacía.
     * </p>
     */
    @Test
    @DisplayName("Eliminar un delito existente")
    void testRemoveDelito_Existente() {
        preso.addDelito(Delito.HOMICIDIO);

        boolean removed = preso.removeDelito(Delito.HOMICIDIO);

        assertTrue(removed);
        assertEquals(0, preso.getDelitos().size());
    }

    /**
     * Prueba que verifica que intentar eliminar un delito que no existe devuelve {@code false}.
     * <p>
     * Se intenta eliminar el delito {@link Delito#ROBO} de un preso sin delitos.
     * </p>
     */
    @Test
    @DisplayName("Eliminar un delito que no existe devuelve false")
    void testRemoveDelito_NoExistente() {
        boolean removed = preso.removeDelito(Delito.ROBO);

        assertFalse(removed);
        assertEquals(0, preso.getDelitos().size());
    }
}