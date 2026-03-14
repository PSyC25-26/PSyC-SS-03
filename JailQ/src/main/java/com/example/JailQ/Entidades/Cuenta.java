package com.example.JailQ.Entidades;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private @Nullable Integer idCuentas;

    private TipoCuenta TipoCuenta;

    private String nombre;

    private String apellidos;

    private String username;

    private String password;

    public Integer getIdCuentas() {
        return idCuentas;
    }

    public void setIdCuentas(Integer idCuentas) {
        this.idCuentas = idCuentas;
    }

    public TipoCuenta getTipoCuenta() {
        return TipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        TipoCuenta = tipoCuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
