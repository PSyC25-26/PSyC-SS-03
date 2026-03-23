package com.example.JailQ.Service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.JailQ.Dao.CarcelDAO;
import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class CarcelServiceTest {

    @Autowired
    private CarcelDAO carcelDAO;

    @Autowired
    private CarcelService carcelService;

    @BeforeEach
    public void setUp() {
        carcelDAO.deleteAll();
    }

    @Test
    void anadirBienCarcel() {
        Carcel carcelTest = new Carcel();
        carcelTest.setNombre("Cárcel Norte");
        carcelTest.setDescripcion("Centro penitenciario de máxima seguridad");
        carcelTest.setLocalidad("Bilbao");
        carcelTest.setCapacidad(500);

        Carcel guardada = carcelService.anadirCarcel(carcelTest);

        assertNotNull(guardada.getIdCarcel());
        assertEquals(1, carcelDAO.count());
    }

    @Test
    void anadirCarcelSinNombre() {
        Carcel carcelMala = new Carcel();
        carcelMala.setDescripcion("Centro pequeño");
        carcelMala.setLocalidad("Madrid");
        carcelMala.setCapacidad(100);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            carcelService.anadirCarcel(carcelMala);
        });

        assertEquals("El nombre de la cárcel es obligatorio.", ex.getMessage());
        assertEquals(0, carcelDAO.count());
    }

    @Test
    void anadirCarcelCapacidadInvalida() {
        Carcel carcelMala = new Carcel();
        carcelMala.setNombre("Cárcel Sur");
        carcelMala.setDescripcion("Centro penitenciario");
        carcelMala.setLocalidad("Sevilla");
        carcelMala.setCapacidad(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            carcelService.anadirCarcel(carcelMala);
        });

        assertEquals("La capacidad debe ser un número mayor que cero.", ex.getMessage());
        assertEquals(0, carcelDAO.count());
    }

}
