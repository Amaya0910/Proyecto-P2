package biblos_reserva_dominio;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.List;

public class Administracion extends Persona {
    private final IntegerProperty idAdmin;

    public Administracion() {
        super();
        this.idAdmin = new SimpleIntegerProperty(0);
    }

    public Administracion(int idAdmin, String nombre, String correo, String contraseña) {
        super(nombre, correo, contraseña);
        this.idAdmin = new SimpleIntegerProperty(idAdmin);
    }

  
    public int getIdAdmin() {
        return idAdmin.get();
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin.set(idAdmin);
    }

    
    public IntegerProperty idAdminProperty() {
        return idAdmin;
    }

    // Métodos de administración (la lógica real estará en el gestor)
    public void agregarCancha(Cancha cancha) {
        System.out.println("Admin: Agregando cancha " + cancha.getNombre());
    }

    public void modificarCancha(Cancha cancha) {
        System.out.println("Admin: Modificando cancha " + cancha.getNombre());
    }

    public void eliminarCancha(Cancha cancha) {
        System.out.println("Admin: Eliminando cancha " + cancha.getNombre());
    }

    public void verReporteReservas(List<?> reservas) {
        System.out.println("Admin: Generando reporte de " + reservas.size() + " reservas");
    }

    @Override
    public String toString() {
        return String.format("Administrador #%d: %s", getIdAdmin(), getNombre());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Administracion admin = (Administracion) obj;
        return getIdAdmin() == admin.getIdAdmin();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getIdAdmin());
    }
}