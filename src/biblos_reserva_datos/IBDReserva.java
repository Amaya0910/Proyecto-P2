/**
 *
 * @author Christ-son
 */
package biblos_reserva_datos;

import biblos_reserva_dominio.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IBDReserva {

    public void crear(Reserva reserva);

    public Reserva buscarPorId(int id);

    public List<Reserva> listarTodas();

    public List<Reserva> listarActivas();

    public List<Reserva> listarPorUsuario(int idUsuario);

    public List<Reserva> listarPorCancha(int idCancha);

    public List<Reserva> listarPorFecha(LocalDate fecha);

    public List<Reserva> listarPorCanchaYFecha(int idCancha, LocalDate fecha);

    public boolean cancelar(int id);

    public boolean existe(int id);

    public boolean hayConflicto(int idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin);

    public int size();
}
