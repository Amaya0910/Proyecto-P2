/**
 *
 * @author Christ-son
 */
package biblos_reserva_datos;

import biblos_reserva_dominio.Cancha;
import java.util.ArrayList;
import java.util.List;

public class ListCancha implements IBDCancha {

    private List<Cancha> canchas;

    public ListCancha() {
        this.canchas = new ArrayList<>();
    }

    @Override
    public void agregar(Cancha cancha) {
        this.canchas.add(cancha);
    }

    @Override
    public Cancha buscarPorId(int id) {
        for (Cancha c : this.canchas) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public Cancha buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        for (Cancha c : this.canchas) {
            if (c.getNombre().equalsIgnoreCase(nombre.trim())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Cancha> listarTodas() {
        return new ArrayList<>(this.canchas);
    }

    @Override
    public List<Cancha> listarDisponibles() {
        List<Cancha> disponibles = new ArrayList<>();
        for (Cancha c : this.canchas) {
            if (c.getEstado().equals("Disponible")) {
                disponibles.add(c);
            }
        }
        return disponibles;
    }

    @Override
    public boolean modificar(Cancha cancha) {
        Cancha existente = buscarPorId(cancha.getId());
        if (existente == null) {
            return false;
        }
        existente.setNombre(cancha.getNombre());
        existente.setEstado(cancha.getEstado());
        return true;
    }

    @Override
    public boolean eliminar(int id) {
        Cancha c = buscarPorId(id);
        if (c != null) {
            this.canchas.remove(c);
            return true;
        }
        return false;
    }

    @Override
    public boolean existe(int id) {
        return buscarPorId(id) != null;
    }

    @Override
    public int size() {
        return this.canchas.size();
    }
}
