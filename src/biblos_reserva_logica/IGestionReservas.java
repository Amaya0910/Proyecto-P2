
package biblos_reserva_logica;

import biblos_reserva_dominio.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface IGestionReservas {
    boolean crearReserva(Reserva reserva);
    boolean cancelarReserva(int idReserva);
    List<Reserva> listarReservasPorUsuario(int idUsuario);
    List<Reserva> listarTodasLasReservas();
    List<Reserva> listarReservasPorCancha(int idCancha, LocalDate fecha);
    List<Reserva> listarReservasActivas();
    boolean verificarDisponibilidad(int idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    List<LocalTime> obtenerHorariosDisponibles(int idCancha, LocalDate fecha);
}