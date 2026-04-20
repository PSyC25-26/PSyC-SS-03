package com.example.JailQ.Integration;

import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Entidades.TipoCuenta;
import com.example.JailQ.TestcontainersConfiguration;
import com.example.JailQ.Service.CarcelService;
import com.example.JailQ.Service.CuentaService;
import com.example.JailQ.Service.PresoService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JailQIntegrationTest {

    @LocalServerPort
    private int port; 

    // @SpyBean envuelve los servicios reales. Funcionan normal en la BD, 
    // pero podemos forzarlos a lanzar excepciones cuando nos interese.
    // @MockitoSpyBean envuelve los servicios reales en versiones modernas de Spring.
    @MockitoSpyBean
    private CarcelService carcelService;

    @MockitoSpyBean
    private CuentaService cuentaService;

    @MockitoSpyBean
    private PresoService presoService;

    // Configuramos RestTemplate para que NO lance excepciones cuando el servidor devuelva errores 400, 404 o 500.
    private final RestTemplate restTemplate = new RestTemplate() {{
        setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false; 
            }
        });
    }};

    @Test
    public void testCoberturaCompletaFacade_CaminoFeliz() throws Exception {
        String baseUrl = "http://localhost:" + port;

        // 1. CREAR CUENTA
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Test");
        cuenta.setApellidos("Cobertura");
        cuenta.setUsername("agente_jacoco");
        cuenta.setPassword("1234");
        cuenta.setTipoCuenta(TipoCuenta.POLICIA);
        ResponseEntity<Cuenta> resCuenta = restTemplate.postForEntity(baseUrl + "/cuentas/crear", cuenta, Cuenta.class);
        Integer idCuenta = resCuenta.getBody().getIdCuentas();

        // 2. LOGIN
        restTemplate.postForEntity(baseUrl + "/cuentas/login/policia", cuenta, Cuenta.class);

        // 3. CREAR CÁRCEL
        Carcel carcel = new Carcel();
        carcel.setNombre("Carcel_JaCoCo");
        carcel.setLocalidad("Testing");
        carcel.setCapacidad(100);
        ResponseEntity<Carcel> resCarcel = restTemplate.postForEntity(baseUrl + "/carcel/crear", carcel, Carcel.class);
        Integer idCarcel = resCarcel.getBody().getIdCarcel();

        // 4. CREAR PRESO
        String jsonPreso = String.format(
            "{\"nombre\":\"Preso Test\", \"apellidos\":\"JaCoCo\", \"fechaNacimiento\":\"1990-01-01\", \"condena\":5, \"fechaIngreso\":\"%s\", \"carcel\":%d, \"delitos\":[\"ROBO\"]}",
            LocalDate.now().toString(), idCarcel
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestPreso = new HttpEntity<>(jsonPreso, headers);
        ResponseEntity<String> resPreso = restTemplate.postForEntity(baseUrl + "/preso/crear", requestPreso, String.class);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode presoNode = mapper.readTree(resPreso.getBody());
        int idPreso = presoNode.get("id").asInt();

        // 5. CUBRIR TODOS LOS ENDPOINTS GET
        restTemplate.getForEntity(baseUrl + "/carcel", String.class);
        restTemplate.getForEntity(baseUrl + "/carcel/" + idCarcel, String.class);
        restTemplate.getForEntity(baseUrl + "/carcel/ocupacion", String.class);
        restTemplate.getForEntity(baseUrl + "/carcel/" + idCarcel + "/ocupacion", String.class);
        restTemplate.getForEntity(baseUrl + "/carcel/estadisticas-completas", String.class);
        restTemplate.getForEntity(baseUrl + "/preso/todos", String.class);
        restTemplate.getForEntity(baseUrl + "/preso/" + idPreso, String.class);
        restTemplate.getForEntity(baseUrl + "/cuentas/policias", String.class);

        // 6. CREAR UNA CUENTA EXTRA SOLO PARA BORRARLA COMO POLICÍA
        Cuenta poliFalso = new Cuenta();
        poliFalso.setNombre("Borrame");
        poliFalso.setApellidos("Ya");
        poliFalso.setUsername("policia_temporal");
        poliFalso.setPassword("1234");
        poliFalso.setTipoCuenta(TipoCuenta.POLICIA);
        ResponseEntity<Cuenta> resPoli = restTemplate.postForEntity(baseUrl + "/cuentas/crear", poliFalso, Cuenta.class);
        Integer idPoli = resPoli.getBody().getIdCuentas();

        // 7. DELETES EXITOSOS (Limpiamos y cubrimos las ramas de éxito)
        restTemplate.delete(baseUrl + "/preso/eliminar/" + idPreso);       
        restTemplate.delete(baseUrl + "/carcel/eliminar/" + idCarcel);     
        restTemplate.delete(baseUrl + "/cuentas/eliminar/" + idCuenta);    
        restTemplate.delete(baseUrl + "/cuentas/eliminar/policia/" + idPoli); 
    }

    @Test
    public void testErroresYExcepcionesFacade_CaminoTriste() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Forzar 400s y 404s mediante validaciones
        Carcel carcelMala = new Carcel();
        carcelMala.setCapacidad(100);
        assertEquals(HttpStatus.BAD_REQUEST, restTemplate.postForEntity(baseUrl + "/carcel/crear", carcelMala, String.class).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, restTemplate.getForEntity(baseUrl + "/carcel/9999", String.class).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, restTemplate.exchange(baseUrl + "/carcel/eliminar/9999", HttpMethod.DELETE, null, String.class).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, restTemplate.exchange(baseUrl + "/preso/eliminar/9999", HttpMethod.DELETE, null, String.class).getStatusCode());

        Cuenta loginMalo = new Cuenta();
        loginMalo.setUsername("inventado");
        loginMalo.setPassword("mal");
        assertEquals(HttpStatus.UNAUTHORIZED, restTemplate.postForEntity(baseUrl + "/cuentas/login/policia", loginMalo, String.class).getStatusCode());

        String jsonPresoMalo = "{\"nombre\":\"Malo\", \"apellidos\":\"Malo\", \"fechaNacimiento\":\"1990-01-01\", \"condena\":5, \"fechaIngreso\":\"2024-01-01\"}";
        assertEquals(HttpStatus.BAD_REQUEST, restTemplate.postForEntity(baseUrl + "/preso/crear", new HttpEntity<>(jsonPresoMalo, headers), String.class).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, restTemplate.getForEntity(baseUrl + "/carcel/9999/ocupacion", String.class).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, restTemplate.exchange(baseUrl + "/cuentas/eliminar/policia/9999", HttpMethod.DELETE, null, String.class).getStatusCode());    }

    @Test
    public void testErrores500_Facade_CaminoCatastrofico() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSONs básicos para enviar en las peticiones de error
        String jsonCarcel = "{\"nombre\":\"X\",\"localidad\":\"Y\",\"capacidad\":100}";
        String jsonCuenta = "{\"username\":\"X\",\"password\":\"Y\",\"tipoCuenta\":\"POLICIA\"}";
        String jsonPreso = "{\"nombre\":\"X\",\"apellidos\":\"Y\",\"fechaNacimiento\":\"1990-01-01\",\"condena\":5,\"fechaIngreso\":\"2024-01-01\",\"carcel\":1}";

        // --- FORZAR 500 EN CARCEL CONTROLLER ---
        doThrow(new RuntimeException("Simulacro 500")).when(carcelService).anadirCarcel(any());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.postForEntity(baseUrl + "/carcel/crear", new HttpEntity<>(jsonCarcel, headers), String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(carcelService).eliminarCarcel(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.exchange(baseUrl + "/carcel/eliminar/1", HttpMethod.DELETE, null, String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(carcelService).obtenerCarceles();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/carcel", String.class).getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/carcel/estadisticas-completas", String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(carcelService).obtenerCarcelPorId(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/carcel/1", String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(carcelService).obtenerOcupacionPorCarcel();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/carcel/ocupacion", String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(carcelService).obtenerOcupacionDeCarcel(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/carcel/1/ocupacion", String.class).getStatusCode());

        // --- FORZAR 500 EN CUENTA CONTROLLER ---
        doThrow(new RuntimeException("Simulacro 500")).when(cuentaService).anadirCuenta(any());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.postForEntity(baseUrl + "/cuentas/crear", new HttpEntity<>(jsonCuenta, headers), String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(cuentaService).eliminarCuenta(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.exchange(baseUrl + "/cuentas/eliminar/1", HttpMethod.DELETE, null, String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(cuentaService).eliminarCuentaPolicia(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.exchange(baseUrl + "/cuentas/eliminar/policia/1", HttpMethod.DELETE, null, String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(cuentaService).obtenerCuentasPolicia();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/cuentas/policias", String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(cuentaService).loginPolicia(anyString(), anyString());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.postForEntity(baseUrl + "/cuentas/login/policia", new HttpEntity<>(jsonCuenta, headers), String.class).getStatusCode());

        // --- FORZAR 500 EN PRESO CONTROLLER ---
        doThrow(new RuntimeException("Simulacro 500")).when(presoService).anadirPreso(any());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.postForEntity(baseUrl + "/preso/crear", new HttpEntity<>(jsonPreso, headers), String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(presoService).obtenerTodos();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/preso/todos", String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(presoService).obtenerPorId(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.getForEntity(baseUrl + "/preso/1", String.class).getStatusCode());

        doThrow(new RuntimeException("Simulacro 500")).when(presoService).eliminar(anyInt());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, restTemplate.exchange(baseUrl + "/preso/eliminar/1", HttpMethod.DELETE, null, String.class).getStatusCode());
    }
}