/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblos_reserva_logica;

import biblos_reserva_dominio.Cancha;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface IGestionCanchas {
    Cancha buscarCanchaPorNombre(String nombre);
    Cancha buscarCanchaPorId(int id);
    List<Cancha> listarTodasLasCanchas();
    List<Cancha> listarCanchasDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    boolean agregarCancha(Cancha cancha);
    boolean modificarCancha(Cancha cancha);
    boolean eliminarCancha(int idCancha);
}