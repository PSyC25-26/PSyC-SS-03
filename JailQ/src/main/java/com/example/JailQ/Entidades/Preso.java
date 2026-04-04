package com.example.JailQ.Entidades;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;

/**
 * Entidad que representa a un preso dentro del sistema.
 * Contiene información personal, detalles de la condena,
 * delitos cometidos y fechas relevantes como ingreso y nacimiento.
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
    * Este campo es obligatorio.
    */
   @Column(nullable = false)
   private String apellidos;

   /**
    * Fecha de nacimiento del preso.
    * Este campo es obligatorio.
    */
   @Column(nullable = false)
   private LocalDate fechaNacimiento;

   /**
    * Duración de la condena en años.
    * Este campo es obligatorio.
    */
   @Column(nullable = false)
   private Integer condena;

   /**
    * Lista de delitos cometidos por el preso.
    * 
    * <p>
    * Este campo es obligatorio y representa todos los delitos asociados
    * al preso dentro del sistema. Se almacena como una colección de tipo
    * {@link Delito} en la tabla <code>preso_delitos</code>, y cada elemento
    * se persiste como una cadena de texto correspondiente al nombre del enum.
    * </p>
    * 
    * <p>
    * Se utiliza {@link java.util.List} para mantener el orden de los delitos,
    * y se inicializa como un {@link java.util.ArrayList}.
    * </p>
    */
   @ElementCollection
   @Enumerated(EnumType.STRING)
   @CollectionTable(name = "preso_delitos", joinColumns = @JoinColumn(name = "preso_id"))
   @Column(name = "delito", nullable = false)
   private List<Delito> delitos = new ArrayList<>();

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
    * Obtiene la lista de delitos cometidos por el preso.
    *
    * <p>
    * La lista mantiene el orden en el que se agregaron los delitos y
    * contiene instancias del enum {@link Delito} correspondientes
    * a cada delito asociado al preso.
    * </p>
    *
    * @return lista de delitos del preso
    */
   public List<Delito> getDelitos() {
      return delitos;
   }

   /**
    * Establece la lista de delitos cometidos por el preso.
    *
    * <p>
    * La lista pasada reemplaza cualquier contenido previo y no debe ser nula.
    * Es recomendable inicializarla como un {@link java.util.ArrayList} si se crea
    * desde fuera.
    * </p>
    *
    * @param delitos lista de delitos que se asignará al preso
    */
   public void setDelitos(List<Delito> delitos) {
      this.delitos = delitos;
   }

   /**
    * Agrega un delito a la lista de delitos del preso.
    *
    * @param delito el {@link Delito} a agregar
    */
   public void addDelito(Delito delito) {
      if (delito != null) {
         this.delitos.add(delito);
      }
   }

   /**
    * Elimina un delito de la lista de delitos del preso.
    *
    * @param delito el {@link Delito} a eliminar
    * @return true si el delito existía y fue eliminado, false si no estaba
    */
   public boolean removeDelito(Delito delito) {
      return this.delitos.remove(delito);
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