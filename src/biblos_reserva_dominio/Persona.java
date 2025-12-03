package biblos_reserva_dominio;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Persona {
    private final StringProperty nombre;
    private final StringProperty correo;
    private String contraseña; // No usar Property para contraseñas por seguridad

    public Persona() {
        this.nombre = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.contraseña = "";
    }

    public Persona(String nombre, String correo, String contraseña) {
        this.nombre = new SimpleStringProperty(nombre);
        this.correo = new SimpleStringProperty(correo);
        this.contraseña = contraseña;
    }

    // Getters y setters tradicionales
    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getCorreo() {
        return correo.get();
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    // Properties para JavaFX (binding con UI)
    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty correoProperty() {
        return correo;
    }

    // Métodos de autenticación
    public boolean login(String correoIngresado, String contraseñaIngresada) {
        return this.correo.get().equals(correoIngresado) && 
               this.contraseña.equals(contraseñaIngresada);
    }

    public void logout() {
        System.out.println("Usuario " + getNombre() + " ha cerrado sesión");
    }

    @Override
    public String toString() {
        return "Nombre: " + getNombre() + ", Correo: " + getCorreo();
    }
}