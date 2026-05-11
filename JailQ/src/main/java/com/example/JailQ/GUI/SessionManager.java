package com.example.JailQ.GUI;

/**
 * Singleton thread-safe que centraliza el estado de sesión del usuario autenticado.
 *
 * Antes, username y tipoCuenta vivían como campos privados de JailQMainGUI,
 * lo que impedía que cualquier otra ventana consultara el rol activo.
 * SessionManager resuelve esto exponiendo el estado de sesión de forma global
 * sin necesidad de pasar parámetros entre ventanas.
 *
 * Implementación con double-checked locking y volatile para garantizar
 * thread-safety sin coste de sincronización en cada llamada.
 *
 * Uso:
 *   SessionManager.getInstance().iniciarSesion(username, tipoCuenta);
 *   SessionManager.getInstance().getTipoCuenta();
 *   SessionManager.getInstance().haySesion();
 */
public class SessionManager {
    private static volatile SessionManager instance;

    private String username   = null;
    private String tipoCuenta = null;

    private SessionManager() { }

    /**
     * Devuelve la instancia única de SessionManager.
     * Usa double-checked locking para thread-safety eficiente.
     *
     * @return la única instancia del singleton
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Registra una sesión activa con el usuario y tipo de cuenta indicados.
     *
     * @param username   nombre de usuario autenticado
     * @param tipoCuenta tipo de cuenta (POLICIA, FAMILIA, GUBERNAMENTAL)
     */
    public synchronized void iniciarSesion(String username, String tipoCuenta) {
        this.username   = username;
        this.tipoCuenta = tipoCuenta;
    }

    /**
     * Cierra la sesión actual, limpiando username y tipoCuenta.
     */
    public synchronized void cerrarSesion() {
        this.username   = null;
        this.tipoCuenta = null;
    }

    /** @return nombre del usuario autenticado, o null si no hay sesión */
    public String getUsername()   { return username;   }

    /** @return tipo de cuenta activo, o null si no hay sesión */
    public String getTipoCuenta() { return tipoCuenta; }

    /** @return true si hay una sesión activa */
    public boolean haySesion()    { return username != null; }
}