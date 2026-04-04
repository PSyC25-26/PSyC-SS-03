package com.example.JailQ.Service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Entidades.Delito;

import org.junit.jupiter.api.*;
import java.util.ArrayList;

class PresoUnitTest {

    private Preso preso;

    @BeforeEach
    void setUp() {
        preso = new Preso();
        preso.setDelitos(new ArrayList<>());
    }

    @Test
    @DisplayName("Agregar un delito válido")
    void testAddDelito_Valido() {
        preso.addDelito(Delito.ROBO);

        assertEquals(1, preso.getDelitos().size());
        assertTrue(preso.getDelitos().contains(Delito.ROBO));
    }

    @Test
    @DisplayName("Agregar un delito null no modifica la lista")
    void testAddDelito_Null() {
        preso.addDelito(null);

        assertEquals(0, preso.getDelitos().size());
    }

    @Test
    @DisplayName("Eliminar un delito existente")
    void testRemoveDelito_Existente() {
        preso.addDelito(Delito.HOMICIDIO);

        boolean removed = preso.removeDelito(Delito.HOMICIDIO);

        assertTrue(removed);
        assertEquals(0, preso.getDelitos().size());
    }

    @Test
    @DisplayName("Eliminar un delito que no existe devuelve false")
    void testRemoveDelito_NoExistente() {
        boolean removed = preso.removeDelito(Delito.ROBO);

        assertFalse(removed);
        assertEquals(0, preso.getDelitos().size());
    }
}