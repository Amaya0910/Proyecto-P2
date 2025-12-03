package biblos_reserva_dominio;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cancha {
    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty estado;

    public Cancha() {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty("");
        this.estado = new SimpleStringProperty("Disponible");
    }

    public Cancha(int id, String nombre, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.estado = new SimpleStringProperty(estado);
    }

    // Getters y setters tradicionales
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    // Properties para JavaFX (para vincular con TableView)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    // Métodos de negocio
    public boolean estaDisponible() {
        return "Disponible".equalsIgnoreCase(estado.get());
    }

    public void marcarComoReservada() {
        setEstado("Reservada");
    }

    public void marcarComoDisponible() {
        setEstado("Disponible");
    }

    public void marcarEnMantenimiento() {
        setEstado("Mantenimiento");
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", getId(), getNombre(), getEstado());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cancha cancha = (Cancha) obj;
        return getId() == cancha.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}