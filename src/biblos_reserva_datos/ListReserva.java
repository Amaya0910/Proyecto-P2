/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Christ-son
 */
package biblos_reserva_datos;

import biblos_reserva_dominio.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ListReserva implements IBDReserva {
    
    private List<Reserva> reservas;

    public ListReserva() {
        this.reservas = new ArrayList<>();
    }

    @Override
    public void crear(Reserva reserva) {
        this.reservas.add(reserva);
    }

    @Override
    public Reserva buscarPorId(int id) {
        for (Reserva r : this.reservas) {
            if (r.getIdReserva() == id) return r;
        }
        return null;
    }

    @Override
    public List<Reserva> listarTodas() {
        return new ArrayList<>(this.reservas);
    }

    @Override
    public List<Reserva> listarActivas() {
        List<Reserva> activas = new ArrayList<>();
        for (Reserva r : this.reservas) {
            if (r.getEstado().equals("Activa")) activas.add(r);
        }
        return activas;
    }

    @Override
    public List<Reserva> listarPorUsuario(int idUsuario) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : this.reservas) {
            if (r.getUsuario().getIdUsuario() == idUsuario && r.getEstado().equals("Activa")) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public List<Reserva> listarPorCancha(int idCancha) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : this.reservas) {
            if (r.getCancha().getId() == idCancha) resultado.add(r);
        }
        return resultado;
    }

    @Override
    public List<Reserva> listarPorFecha(LocalDate fecha) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : this.reservas) {
            if (r.getFecha().equals(fecha) && r.getEstado().equals("Activa")) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public List<Reserva> listarPorCanchaYFecha(int idCancha, LocalDate fecha) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : this.reservas) {
            if (r.getCancha().getId() == idCancha && 
                r.getFecha().equals(fecha) && 
                r.getEstado().equals("Activa")) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public boolean cancelar(int id) {
        Reserva r = buscarPorId(id);
        if (r != null && r.getEstado().equals("Activa")) {
            r.cancelarReserva();
            return true;
        }
        return false;
    }

    @Override
    public boolean existe(int id) {
        return buscarPorId(id) != null;
    }

    @Override
    public boolean hayConflicto(int idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<Reserva> reservasDia = listarPorCanchaYFecha(idCancha, fecha);
        for (Reserva r : reservasDia) {
            if (r.tieneConflictoHorario(inicio, fin)) return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.reservas.size();
    }
}
