package biblos_reserva_dominio;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario extends Persona {
    private final IntegerProperty idUsuario;
    private final StringProperty contacto;

    public Usuario() {
        super();
        this.idUsuario = new SimpleIntegerProperty(0);
        this.contacto = new SimpleStringProperty("");
    }

    public Usuario(int idUsuario, String contacto, String nombre, String correo, String contraseña) {
        super(nombre, correo, contraseña);
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.contacto = new SimpleStringProperty(contacto);
    }

    // Getters y setters tradicionales
    public int getIdUsuario() {
        return idUsuario.get();
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario.set(idUsuario);
    }

    public String getContacto() {
        return contacto.get();
    }

    public void setContacto(String contacto) {
        this.contacto.set(contacto);
    }

    // Properties para JavaFX
    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public StringProperty contactoProperty() {
        return contacto;
    }

    // Métodos de negocio (placeholder - se implementarán con el gestor)
    public void buscarCancha(String criterio) {
        System.out.println("Buscando cancha: " + criterio);
    }

    public void reservarCancha(Cancha cancha) {
        System.out.println("Reservando cancha: " + cancha.getNombre());
    }

    public void verHistorialReserva() {
        System.out.println("Mostrando historial de: " + getNombre());
    }

    @Override
    public String toString() {
        return String.format("Usuario #%d: %s (Contacto: %s)", 
                           getIdUsuario(), getNombre(), getContacto());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return getIdUsuario() == usuario.getIdUsuario();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getIdUsuario());
    }

    public int getId() {
        return getIdUsuario();
    }
    
    public String getContrasena() {
        return getContraseña();
    }
}