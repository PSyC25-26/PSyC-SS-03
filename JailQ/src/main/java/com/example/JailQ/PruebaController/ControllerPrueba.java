package com.example.JailQ.PruebaController;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.JailQ.Dao.CuentaDAO;
import com.example.JailQ.Dao.CarcelDAO;
import com.example.JailQ.Dao.PresoDAO;
import com.example.JailQ.Entidades.Carcel;
import com.example.JailQ.Entidades.Cuenta;
import com.example.JailQ.Entidades.Delito;
import com.example.JailQ.Entidades.Preso;

@Controller 
@RequestMapping(path="/demo")
public class ControllerPrueba {// controller para pruebas de BD
  @Autowired 
  private CuentaDAO cuentaDAO;

  @Autowired
  private CarcelDAO carcelDAO;

  @Autowired
  private PresoDAO presoDAO;

  @PostMapping(path="/addCuenta")
  public @ResponseBody String addNewCuenta (@RequestParam String username, @RequestParam String password) {

    Cuenta n = new Cuenta();
    n.setUsername(username);
    n.setPassword(password);
    cuentaDAO.save(n);
    return "Saved";
  }

  @PostMapping(path="/addCarcel")
  public @ResponseBody String addNewCarcel (@RequestParam String nombre, @RequestParam String localidad, @RequestParam Integer capacidad) {

    Carcel n = new Carcel();
    n.setNombre(nombre);
    n.setLocalidad(localidad);
    n.setCapacidad(capacidad);
    carcelDAO.save(n);
    return "Saved";
  }

  @PostMapping(path="/addPreso")
  public @ResponseBody String addNewPreso (@RequestParam String nombre, @RequestParam Integer condena, @RequestParam Delito delitoPreso, @RequestParam LocalDate fechaIngreso) {

    Preso n = new Preso();
    n.setNombre(nombre);
    n.setCondena(condena);
    n.setDelitoPreso(delitoPreso);
    n.setFechaIngreso(fechaIngreso);
    presoDAO.save(n);
    return "Saved";
  }

  @GetMapping(path="/allCuentas")
  public @ResponseBody Iterable<Cuenta> getAllCuentas() {
    return cuentaDAO.findAll();
  }

  @GetMapping(path="/allCarceles")
  public @ResponseBody Iterable<Carcel> getAllCarceles() {
    return carcelDAO.findAll();
  }

  @GetMapping(path="/allPresos")
  public @ResponseBody Iterable<Preso> getAllPresos() {
    return presoDAO.findAll();
  }
}