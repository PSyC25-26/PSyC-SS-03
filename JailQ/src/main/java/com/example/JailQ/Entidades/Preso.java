package com.example.JailQ.Entidades;

import java.time.LocalDate;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Preso {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private @Nullable Integer idPreso;

  private String nombre;

  private String apellidos;

  private LocalDate fechaNacimiento;

  private Integer condena;

  private Delito delitoPreso;

  private LocalDate fechaIngreso;

  public Integer getId() {
    return idPreso;
  }

  public void setId(Integer idPreso) {
    this.idPreso = idPreso;
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

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public Integer getCondena() {
    return condena;
  }

  public void setCondena(Integer condena) {
    this.condena = condena;
  }

  public Delito getDelitoPreso() {
    return delitoPreso;
  }

  public void setDelitoPreso(Delito delitoPreso) {
    this.delitoPreso = delitoPreso;
  }

  public LocalDate getFechaIngreso() {
    return fechaIngreso;
  }

  public void setFechaIngreso(LocalDate fechaIngreso) {
    this.fechaIngreso = fechaIngreso;
  }

}