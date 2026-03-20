package com.example.JailQ.Entidades;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

/**
 * Entidad que representa una cuenta de usuario dentro del sistema.
 * 
 * Una cuenta contiene la información personal del usuario y sus
 * credenciales de acceso, así como el tipo de cuenta que determina
 * su rol dentro de la aplicación.
 */
@Entity
public class Cuenta {
    /**
     * Identificador único de la cuenta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private @Nullable Integer idCuentas;

    /**
     * Tipo de cuenta que define el rol del usuario en el sistema.
     */
    private TipoCuenta tipoCuenta;

    /**
     * Nombre del usuario propietario de la cuenta.
     */
    private String nombre;

    /**
     * Apellidos del usuario propietario de la cuenta.
     */
    private String apellidos;

    /**
     * Nombre de usuario utilizado para iniciar sesión.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String username;

    /**
     * Contraseña utilizada para la autenticación del usuario.
     * Este campo no puede ser nulo.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Obtiene el identificador de la cuenta.
     * 
     * @return identificador único de la cuenta
     */
    public Integer getIdCuentas() {
        return idCuentas;
    }

    /**
     * Establece el identificador de la cuenta.
     * 
     * @param idCuentas nuevo identificador de la cuenta
     */
    public void setIdCuentas(Integer idCuentas) {
        this.idCuentas = idCuentas;
    }

    /**
     * Obtiene el tipo de cuenta.
     * 
     * @return tipo de cuenta del usuario
     */
    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    /**
     * Establece el tipo de cuenta del usuario.
     * 
     * @param tipoCuenta tipo de cuenta a asignar
     */
    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    /**
     * Obtiene el nombre del usuario.
     * 
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * 
     * @param nombre nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del usuario.
     * 
     * @return apellidos del usuario
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     * 
     * @param apellidos apellidos del usuario
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el nombre de usuario de la cuenta.
     * 
     * @return username del usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario de la cuenta.
     * 
     * @param username nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la contraseña de la cuenta.
     * 
     * @return contraseña del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña de la cuenta.
     * 
     * @param password contraseña del usuario
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
