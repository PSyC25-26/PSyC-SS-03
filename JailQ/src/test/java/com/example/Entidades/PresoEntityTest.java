package com.example.Entidades;

import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Entidades.Delito;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PresoEntityTest {

    @Test
    public void testGettersYSetters() {
        Preso preso = new Preso();

        Carcel carcel = new Carcel();
        carcel.setNombre("Martutene");
        carcel.setLocalidad("Donostia");
        carcel.setCapacidad(200);

        preso.setNombre("Michael");
        preso.setApellidos("Scofield");
        preso.setFechaNacimiento(LocalDate.of(1974, 9, 8));
        preso.setFechaIngreso(LocalDate.now());
        preso.setCondena(5);
        preso.setCarcel(carcel);
        preso.setDelitos(new ArrayList<>(List.of(Delito.ROBO)));

        assertEquals("Michael", preso.getNombre());
        assertEquals("Scofield", preso.getApellidos());
        assertEquals(LocalDate.of(1974, 9, 8), preso.getFechaNacimiento());
        assertEquals(5, preso.getCondena());
        assertEquals(carcel, preso.getCarcel());
        assertTrue(preso.getDelitos().contains(Delito.ROBO));
    }

    @Test
    public void testAddYRemoveDelito() {
        Preso preso = new Preso();

        preso.addDelito(Delito.HOMICIDIO);
        assertTrue(preso.getDelitos().contains(Delito.HOMICIDIO));

        // addDelito con null no debe lanzar excepción ni añadir nada
        preso.addDelito(null);
        assertEquals(1, preso.getDelitos().size());

        boolean removed = preso.removeDelito(Delito.HOMICIDIO);
        assertTrue(removed);
        assertTrue(preso.getDelitos().isEmpty());

        // removeDelito de algo que no existe devuelve false
        boolean removedAgain = preso.removeDelito(Delito.ROBO);
        assertFalse(removedAgain);
    }

    @Test
    public void testSetCarcelById() {
        Preso preso = new Preso();
        preso.setCarcelById(42);
        assertNotNull(preso.getCarcel());
        assertEquals(42, preso.getCarcel().getIdCarcel());

        // Con null no debe asignar cárcel
        Preso preso2 = new Preso();
        preso2.setCarcelById(null);
        assertNull(preso2.getCarcel());
    }

    @Test
    public void testSetId() {
        Preso preso = new Preso();
        preso.setId(99);
        assertEquals(99, preso.getId());
    }
}