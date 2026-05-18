package com.example.JailQ.Facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Preso;
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
}