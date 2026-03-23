package com.example.JailQ.Service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.JailQ.Dao.PresoDAO;
import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Preso;
import com.example.JailQ.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class PresoServiceTest {
    @Autowired
    private PresoDAO presoDAO;

    @Autowired
    private PresoService presoService;

    @BeforeEach
    public void setUp(){
        presoDAO.deleteAll();
        //Para limpiar las instancias creadas en cada test. 
    }

    //Test para comprobar que se añada bien a la BDD
    @Test
    void anadirBienPreso(){
        Preso presoTest = new Preso();
        presoTest.setNombre("Markel");
        presoTest.setApellidos("Damian");
        presoTest.setCondena(2);
        presoTest.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        presoTest.setFechaIngreso(LocalDate.now());
        presoTest.setDelitoPreso(Delito.TRAF_PERSONAS);
        presoService.anadirPreso(presoTest);

        assertEquals(1, presoDAO.count(), "Debería haber un preso guardado en la BD");
        
    }

    @Test
    void anadirPresoMenor(){
        
        Preso menor = new Preso();
        menor.setNombre("Juanito");
        menor.setApellidos("Pérez");
        menor.setFechaNacimiento(LocalDate.now().minusYears(10)); //El preso tiene 10 años
        menor.setCondena(2);
        menor.setFechaIngreso(LocalDate.now());

        presoService.anadirPreso(menor);

        //Verificamos que la BDD sigue vacía
        assertEquals(0, presoDAO.count(), "No se debería haber guardado un menor de edad");
    
    }
}
