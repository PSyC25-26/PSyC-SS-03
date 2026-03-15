package com.example.JailQ.Entidades;

/**
 * Enumeración que representa los distintos tipos de cuenta
 * que pueden existir dentro del sistema.
 * 
 * Cada tipo de cuenta define el rol o perfil del usuario
 * que interactúa con la aplicación.
 */
public enum TipoCuenta {
    /**
     * Cuenta perteneciente a un agente de policía.
     * Tiene permisos para gestionar información del sistema.
     */
    POLICIA,
     /**
     * Cuenta perteneciente a un familiar de un recluso.
     * Permite consultar información relacionada con el interno.
     */
    FAMILIA,
    /**
     * Cuenta perteneciente a personal gubernamental.
     * Generalmente utilizada para supervisión o administración.
     */
    GUBERNAMENTAL

}
