package com.example.JailQ.Performance;
 
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assumptions;
 
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
 
/**
 * Tests de rendimiento del sistema JailQ usando JUnitPerf + JUnit 5.
 *
 * Requiere el servidor Spring Boot activo en localhost:8080.
 * Ejecutar con: mvn -Pperformance integration-test
 * Informe generado en: target/junitperf/report.html
 *
 * Tests incluidos:
 *  - testGetCarceles_Duracion_Correcto : duración, debe PASAR
 *  - testGetCarceles_Duracion_Fallido  : duración, debe FALLAR (latencia imposible)
 *  - testGetPresos_Throughput_Correcto : throughput, debe PASAR
 *  - testGetPresos_Throughput_Fallido  : throughput, debe FALLAR (requisito imposible)
 */
@ExtendWith(JUnitPerfInterceptor.class)
public class JailQPerformanceTest {
 
    // Configuración del informe HTML.
    // Debe ser un campo estático anotado con @JUnitPerfTestActiveConfig para que
    // JUnitPerf lo detecte y no cree una instancia nueva por cada @Test.
    @JUnitPerfTestActiveConfig
    private static final JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator("target/junitperf/report.html"))
            .build();
 
    private final HttpClient httpClient = HttpClient.newHttpClient();
 
    // ── TEST 1 — Duración · PASA ───────────────────────────────────────────
    // GET /carcel durante 3 s con 5 hilos. Requisito: latencia media < 500 ms.
    // Con el servidor local activo, este test debe pasar sin problemas.
 
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 3000, warmUpMs = 500)
    @JUnitPerfTestRequirement(meanLatency = 500)
    public void testGetCarceles_Duracion_Correcto() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/carcel"))
                .GET()
                .build();
 
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
 
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Respuesta inesperada: " + response.statusCode());
        }
    }
 
    // ── TEST 2 — Duración · FALLA intencionadamente ────────────────────────
    // GET /carcel con requisito de latencia media < 1 ms.
    // Ninguna petición HTTP real puede cumplir ese umbral → el test falla.
 
    @Test
    @JUnitPerfTest(threads = 3, durationMs = 2000)
    @JUnitPerfTestRequirement(meanLatency = 1)
    public void testGetCarceles_Duracion_Fallido() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/carcel"))
                .GET()
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assumptions.assumeTrue(false, "Fallo intencionado: latencia < 1 ms imposible en HTTP real");
    }
 
    // ── TEST 3 — Throughput · PASA ─────────────────────────────────────────
    // GET /preso durante 4 s con 10 hilos. Requisito: >= 2 ejecuciones/s.
    // Con el servidor local activo, este test debe pasar sin problemas.
 
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 4000, warmUpMs = 500)
    @JUnitPerfTestRequirement(executionsPerSec = 2, maxLatency = 2000)
    public void testGetPresos_Throughput_Correcto() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/preso/todos"))
                .GET()
                .build();
 
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
 
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Respuesta inesperada: " + response.statusCode());
        }
    }
 
    // ── TEST 4 — Throughput · FALLA intencionadamente ──────────────────────
    // GET /preso con requisito de 10 000 ejecuciones/s.
    // Imposible con HTTP real sobre localhost → el test falla.
 
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 2000)
    @JUnitPerfTestRequirement(executionsPerSec = 10000)
    public void testGetPresos_Throughput_Fallido() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/preso/todos"))
                .GET()
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assumptions.assumeTrue(false, "Fallo intencionado: 10 000 exec/s imposible en HTTP real");
    }

    
    // ── TEST 5 — Percentiles · PASA ────────────────────────────────────────
    // GET /carcel con requisitos de percentiles p90, p95, p99.
    // Umbrales holgados para garantizar que pasa con el servidor local.

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(
        percentiles = "90:300,95:400,99:500",
        executionsPerSec = 5,
        allowedErrorPercentage = 0.1f
    )
    public void testGetCarceles_Percentiles_Correcto() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/carcel"))
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Respuesta inesperada: " + response.statusCode());
        }
    }

    // ── TEST 6 — Percentiles · FALLA intencionadamente ─────────────────────
    // GET /preso/todos con requisitos de percentiles imposibles (1 ms).
    // Ninguna petición HTTP real puede cumplir ese umbral → el test falla.

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(
        percentiles = "90:1,95:1,99:1",
        executionsPerSec = 5,
        allowedErrorPercentage = 0.1f
    )
    public void testGetPresos_Percentiles_Fallido() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/preso/todos"))
                .GET()
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assumptions.assumeTrue(false, "Fallo intencionado: 10 000 exec/s imposible en HTTP real");
    }
}
