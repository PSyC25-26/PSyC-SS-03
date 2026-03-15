package com.example.JailQ.Entidades;

/**
 * Enumeración que representa los distintos tipos de delitos
 * que pueden estar asociados a un recluso dentro del sistema.
 * 
 * Cada valor corresponde a una categoría de delito registrada
 * en la aplicación.
 */
public enum Delito {
    /**
     * Delito de homicidio, que implica causar la muerte a otra persona.
     */
    HOMICIDIO,
     /**
     * Delito de secuestro, consistente en privar ilegalmente de libertad a una persona.
     */
    SECUESTRO,
    /**
     * Delito de robo, que implica apropiarse de bienes ajenos mediante fuerza o intimidación.
     */
    ROBO,
    /**
     * Delito de agresión sexual contra otra persona.
     */
    AGRESION_SEXUAL,
    /**
     * Delito relacionado con abuso sexual a menores.
     */
    PEDOFILIA,
    /**
     * Delito de estafa o fraude con fines económicos.
     */
    ESTAFA,
    /**
     * Delito de tráfico de drogas ilegales.
     */
    TRAF_DROGAS,
    /**
     * Delito de tráfico ilegal de personas.
     */
    TRAF_PERSONAS,
    /**
     * Delito de terrorismo o participación en actividades terroristas.
     */
    TERRORISMO,
    /**
     * Delito de provocar incendios de forma intencionada.
     */
    PIROMANIA,
    /**
     * Delito de blanqueo de dinero procedente de actividades ilícitas.
     */
    BLANQUEO_DINERO,
    /**
     * Delito de falsificación de documentos oficiales o privados.
     */
    FALSIFICACION_DOC
}
