package biblos_reserva_logica;

import biblos_reserva_dominio.Cancha;
import biblos_reserva_dominio.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Clase de utilidad para visualizar la disponibilidad de canchas por hora
 */
public class VisualizadorHorarios {
    
    private static final LocalTime HORA_APERTURA = LocalTime.of(6, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(23, 0);
    
    /**
     * Genera una representación visual de la disponibilidad de una cancha en un día
     */
    public static String generarCalendarioDiario(Cancha cancha, LocalDate fecha, 
                                                  List<Reserva> reservas) {
        StringBuilder calendario = new StringBuilder();
        
        calendario.append("\n╔════════════════════════════════════════════════════════════╗\n");
        calendario.append(String.format("║  DISPONIBILIDAD: %-40s║\n", cancha.getNombre()));
        calendario.append(String.format("║  Fecha: %-47s║\n", fecha.toString()));
        calendario.append("╠════════════════════════════════════════════════════════════╣\n");
        calendario.append("║  Hora  │  Estado                                         ║\n");
        calendario.append("╠════════════════════════════════════════════════════════════╣\n");
        
        LocalTime horaActual = HORA_APERTURA;
        
        while (horaActual.isBefore(HORA_CIERRE)) {
            String horaStr = String.format("%02d:00", horaActual.getHour());
            String estado = obtenerEstadoHora(horaActual, reservas);
            String simbolo = estado.equals("Disponible") ? "✓" : "✗";
            
            calendario.append(String.format("║ %s │ %s %-44s║\n", 
                                          horaStr, simbolo, estado));
            
            horaActual = horaActual.plusHours(1);
        }
        
        calendario.append("╚════════════════════════════════════════════════════════════╝\n");
        
        return calendario.toString();
    }
    
    /**
     * Determina el estado de una hora específica
     */
    private static String obtenerEstadoHora(LocalTime hora, List<Reserva> reservas) {
        for (Reserva reserva : reservas) {
            if (reserva.getEstado().equals("Activa") && reserva.ocupaHorario(hora)) {
                return String.format("RESERVADO por %s", 
                                   reserva.getUsuario().getNombre());
            }
        }
        return "Disponible";
    }
    
    /**
     * Genera un resumen compacto de las horas disponibles
     */
    public static String generarResumenDisponibilidad(List<LocalTime> horasDisponibles) {
        if (horasDisponibles.isEmpty()) {
            return "❌ No hay horarios disponibles para este día";
        }
        
        StringBuilder resumen = new StringBuilder();
        resumen.append("\n✓ Horarios disponibles:\n");
        
        int contador = 0;
        for (LocalTime hora : horasDisponibles) {
            resumen.append(String.format("  [%02d:00] ", hora.getHour()));
            contador++;
            if (contador % 6 == 0) {
                resumen.append("\n");
            }
        }
        
        resumen.append(String.format("\n\nTotal: %d horas disponibles\n", horasDisponibles.size()));
        
        return resumen.toString();
    }
    
    /**
     * Genera una tabla con todas las reservas del día
     */
    public static String generarTablaReservas(List<Reserva> reservas, LocalDate fecha) {
        StringBuilder tabla = new StringBuilder();
        
        tabla.append("\n╔════════════════════════════════════════════════════════════════════╗\n");
        tabla.append(String.format("║  RESERVAS DEL DÍA: %-47s║\n", fecha.toString()));
        tabla.append("╠════════════════════════════════════════════════════════════════════╣\n");
        
        if (reservas.isEmpty()) {
            tabla.append("║  No hay reservas para este día                                    ║\n");
        } else {
            tabla.append("║  ID  │ Cancha              │ Horario      │ Usuario             ║\n");
            tabla.append("╠════════════════════════════════════════════════════════════════════╣\n");
            
            for (Reserva reserva : reservas) {
                String cancha = truncar(reserva.getCancha().getNombre(), 19);
                String horario = reserva.getHorarioFormateado();
                String usuario = truncar(reserva.getUsuario().getNombre(), 19);
                
                tabla.append(String.format("║ %-4d│ %-19s│ %-12s│ %-19s║\n",
                                         reserva.getIdReserva(), cancha, horario, usuario));
            }
        }
        
        tabla.append("╚════════════════════════════════════════════════════════════════════╝\n");
        
        return tabla.toString();
    }
    
    /**
     * Verifica si dos rangos de tiempo se solapan
     */
    public static boolean hayConflictoHorario(LocalTime inicio1, LocalTime fin1,
                                             LocalTime inicio2, LocalTime fin2) {
        return !(fin1.isBefore(inicio2) || inicio1.isAfter(fin2) || inicio1.equals(fin2));
    }
    
    /**
     * Trunca un texto a una longitud máxima
     */
    private static String truncar(String texto, int maxLength) {
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Genera estadísticas de uso de una cancha
     */
    public static String generarEstadisticas(Cancha cancha, List<Reserva> todasLasReservas) {
        long reservasActivas = todasLasReservas.stream()
                .filter(r -> r.getCancha().getId() == cancha.getId())
                .filter(r -> r.getEstado().equals("Activa"))
                .count();
        
        long reservasCanceladas = todasLasReservas.stream()
                .filter(r -> r.getCancha().getId() == cancha.getId())
                .filter(r -> r.getEstado().equals("Cancelada"))
                .count();
        
        long totalHorasReservadas = todasLasReservas.stream()
                .filter(r -> r.getCancha().getId() == cancha.getId())
                .filter(r -> r.getEstado().equals("Activa"))
                .mapToLong(Reserva::getDuracionHoras)
                .sum();
        
        StringBuilder stats = new StringBuilder();
        stats.append("\n╔════════════════════════════════════════╗\n");
        stats.append(String.format("║  ESTADÍSTICAS: %-23s║\n", truncar(cancha.getNombre(), 23)));
        stats.append("╠════════════════════════════════════════╣\n");
        stats.append(String.format("║  Reservas activas:    %-16d║\n", reservasActivas));
        stats.append(String.format("║  Reservas canceladas: %-16d║\n", reservasCanceladas));
        stats.append(String.format("║  Total horas reservadas: %-13d║\n", totalHorasReservadas));
        stats.append("╚════════════════════════════════════════╝\n");
        
        return stats.toString();
    }
}