package com.example.JailQ.GUI;

import java.net.http.HttpClient;

/**
 * Singleton thread-safe que proporciona una instancia compartida de HttpClient
 * para todas las ventanas del sistema JailQ.
 *
 * HttpClient está diseñado para ser reutilizado: crear una instancia por ventana
 * desperdicia recursos (hilos, conexiones). Este singleton garantiza que solo
 * existe una instancia durante toda la ejecución.
 *
 * Implementación con double-checked locking y volatile para garantizar
 * thread-safety sin coste de sincronización en cada llamada.
 *
 * Uso: HttpClientSingleton.getInstance().getClient()
 */
public class HttpClientSingleton {

    private static volatile HttpClientSingleton instance;

    private final HttpClient cliente;

    private HttpClientSingleton() {
        this.cliente = HttpClient.newHttpClient();
    }

    /**
     * Devuelve la instancia única de HttpClientSingleton.
     * Usa double-checked locking para thread-safety eficiente.
     *
     * @return la única instancia del singleton
     */
    public static HttpClientSingleton getInstance() {
        if (instance == null) {
            synchronized (HttpClientSingleton.class) {
                if (instance == null) {
                    instance = new HttpClientSingleton();
                }
            }
        }
        return instance;
    }

    /**
     * Devuelve el HttpClient compartido listo para enviar peticiones.
     *
     * @return la única instancia de HttpClient de la aplicación
     */
    public HttpClient getClient() {
        return cliente;
    }
}