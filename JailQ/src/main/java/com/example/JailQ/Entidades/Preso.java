package com.example.JailQ.Entidades;

import java.time.LocalDate;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

/**
 * Entidad que representa a un preso dentro del sistema.
 * Contiene información personal, detalles de la condena,
 * el delito cometido y fechas relevantes como ingreso y nacimiento.
 */
@Entity
public class Preso {
  /**
     * Identificador único del preso.
     */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private @Nullable Integer idPreso;

  /**
     * Nombre del preso.
     * Este campo es obligatorio.
     */
  @Column(nullable = false)
  private String nombre;

  /**
     * Apellidos del preso.
     */
  private String apellidos;

  /**
     * Fecha de nacimiento del preso.
     */
  private LocalDate fechaNacimiento;

  /**
     * Duración de la condena en años.
     * Este campo es obligatorio.
     */
  @Column(nullable = false)
  private Integer condena;

  /**
     * Delito cometido por el preso.
     * Este campo es obligatorio.
     */
  @Column(nullable = false)
  private Delito delitoPreso;

  /**
     * Fecha de ingreso del preso al centro penitenciario.
     * Este campo es obligatorio.
     */
  @Column(nullable = false)
  private LocalDate fechaIngreso;

  /**
     * Obtiene el identificador del preso.
     * 
     * @return id del preso
     */
  public Integer getId() {
    return idPreso;
  }

  /**
     * Establece el identificador del preso.
     * 
     * @param idPreso nuevo id del preso
     */
  public void setId(Integer idPreso) {
    this.idPreso = idPreso;
  }

  /**
     * Obtiene el nombre del preso.
     * 
     * @return nombre del preso
     */
  public String getNombre() {
    return nombre;
  }

  /**
     * Establece el nombre del preso.
     * 
     * @param nombre nombre del preso
     */
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /**
     * Obtiene los apellidos del preso.
     * 
     * @return apellidos del preso
     */
  public String getApellidos() {
    return apellidos;
  }

  /**
     * Establece los apellidos del preso.
     * 
     * @param apellidos apellidos del preso
     */
  public void setApellidos(String apellidos) {
    this.apellidos = apellidos;
  }

  /**
     * Obtiene la fecha de nacimiento del preso.
     * 
     * @return fecha de nacimiento
     */
  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  /**
     * Establece la fecha de nacimiento del preso.
     * 
     * @param fechaNacimiento fecha de nacimiento
     */
  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  /**
     * Obtiene la duración de la condena en años.
     * 
     * @return duración de la condena
     */
  public Integer getCondena() {
    return condena;
  }

  /**
     * Establece la duración de la condena.
     * 
     * @param condena duración en años
     */
  public void setCondena(Integer condena) {
    this.condena = condena;
  }

  /**
     * Obtiene el delito cometido por el preso.
     * 
     * @return delito del preso
     */
  public Delito getDelitoPreso() {
    return delitoPreso;
  }

  /**
     * Establece el delito del preso.
     * 
     * @param delitoPreso delito cometido
     */
  public void setDelitoPreso(Delito delitoPreso) {
    this.delitoPreso = delitoPreso;
  }

  /**
     * Obtiene la fecha de ingreso del preso al centro penitenciario.
     * 
     * @return fecha de ingreso
     */
  public LocalDate getFechaIngreso() {
    return fechaIngreso;
  }

  /**
     * Establece la fecha de ingreso del preso.
     * 
     * @param fechaIngreso fecha de ingreso
     */
  public void setFechaIngreso(LocalDate fechaIngreso) {
    this.fechaIngreso = fechaIngreso;
  }

}