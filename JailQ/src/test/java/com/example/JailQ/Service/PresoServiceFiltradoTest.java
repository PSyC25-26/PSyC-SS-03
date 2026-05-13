package com.example.JailQ.Service;


import com.example.JailQ.Dao.PresoDAO;
import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Preso;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresoServiceFiltradoTest {

    @Mock
    private PresoDAO presoDAO;

    @InjectMocks
    private PresoService presoService;

    @Test
    void filtrarPorDelito_deberiaDevolverSoloPresosConEseDelito() {
        // Arrange
        Preso preso1 = new Preso();
        preso1.setId(1);
        preso1.setNombre("Juan");
        preso1.setApellidos("Gomez");
        preso1.setDelitos(Arrays.asList(Delito.ROBO, Delito.ESTAFA));

        Preso preso2 = new Preso();
        preso2.setId(2);
        preso2.setNombre("Pedro");
        preso2.setApellidos("Lopez");
        preso2.setDelitos(Arrays.asList(Delito.HOMICIDIO));

        Preso preso3 = new Preso();
        preso3.setId(3);
        preso3.setNombre("Luis");
        preso3.setApellidos("Martinez");
        preso3.setDelitos(Arrays.asList(Delito.ROBO, Delito.SECUESTRO));

        when(presoDAO.findAll()).thenReturn(Arrays.asList(preso1, preso2, preso3));

        // Act
        List<Preso> resultado = presoService.filtrarPorDelito(Delito.ROBO);

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(preso1));
        assertTrue(resultado.contains(preso3));
        assertFalse(resultado.contains(preso2));
    }

    @Test
    void filtrarPorDelito_siDelitoEsNull_deberiaLanzarExcepcion() {
        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> presoService.filtrarPorDelito(null)
        );

        assertEquals("El delito no puede ser nulo.", exception.getMessage());
    }
}