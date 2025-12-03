package biblos_reserva_dominio;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    private final IntegerProperty idReserva;
    private final ObjectProperty<LocalDate> fecha;
    private final ObjectProperty<LocalTime> horaInicio;
    private final ObjectProperty<LocalTime> horaFin;
    private final ObjectProperty<Usuario> usuario;
    private final ObjectProperty<Cancha> cancha;
    private final StringProperty estado; // "Activa", "Cancelada", "Completada"

    // Constructor por defecto
    public Reserva() {
        this.idReserva = new SimpleIntegerProperty(0);
        this.fecha = new SimpleObjectProperty<>(LocalDate.now());
        this.horaInicio = new SimpleObjectProperty<>(LocalTime.now());
        this.horaFin = new SimpleObjectProperty<>(LocalTime.now().plusHours(1));
        this.usuario = new SimpleObjectProperty<>(null);
        this.cancha = new SimpleObjectProperty<>(null);
        this.estado = new SimpleStringProperty("Activa");
    }

    // Constructor con parámetros (orden 1)
    public Reserva(int idReserva, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                   Usuario usuario, Cancha cancha) {
        this.idReserva = new SimpleIntegerProperty(idReserva);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.horaInicio = new SimpleObjectProperty<>(horaInicio);
        this.horaFin = new SimpleObjectProperty<>(horaFin);
        this.usuario = new SimpleObjectProperty<>(usuario);
        this.cancha = new SimpleObjectProperty<>(cancha);
        this.estado = new SimpleStringProperty("Activa");
    }

    // Constructor con parámetros (orden 2) - CORREGIDO
    // Este es el que usa ArchivoReserva
    public Reserva(int idReserva, Usuario usuario, Cancha cancha, LocalDate fecha, 
                   LocalTime horaInicio, LocalTime horaFin) {
        this.idReserva = new SimpleIntegerProperty(idReserva);
        this.usuario = new SimpleObjectProperty<>(usuario);
        this.cancha = new SimpleObjectProperty<>(cancha);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.horaInicio = new SimpleObjectProperty<>(horaInicio);
        this.horaFin = new SimpleObjectProperty<>(horaFin);
        this.estado = new SimpleStringProperty("Activa");
    }

    // ==================== GETTERS Y SETTERS ====================
    
    public int getIdReserva() {
        return idReserva.get();
    }

    public void setIdReserva(int idReserva) {
        this.idReserva.set(idReserva);
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public void setFecha(LocalDate fecha) {
        this.fecha.set(fecha);
    }

    public LocalTime getHoraInicio() {
        return horaInicio.get();
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio.set(horaInicio);
    }

    public LocalTime getHoraFin() {
        return horaFin.get();
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin.set(horaFin);
    }

    public Usuario getUsuario() {
        return usuario.get();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario.set(usuario);
    }

    public Cancha getCancha() {
        return cancha.get();
    }

    public void setCancha(Cancha cancha) {
        this.cancha.set(cancha);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    // ==================== PROPIEDADES JAVAFX ====================
    
    public IntegerProperty idReservaProperty() { 
        return idReserva; 
    }
    
    public ObjectProperty<LocalDate> fechaProperty() { 
        return fecha; 
    }
    
    public ObjectProperty<LocalTime> horaInicioProperty() { 
        return horaInicio; 
    }
    
    public ObjectProperty<LocalTime> horaFinProperty() { 
        return horaFin; 
    }
    
    public ObjectProperty<Usuario> usuarioProperty() { 
        return usuario; 
    }
    
    public ObjectProperty<Cancha> canchaProperty() { 
        return cancha; 
    }
    
    public StringProperty estadoProperty() { 
        return estado; 
    }

    // ==================== MÉTODOS DE NEGOCIO ====================
    
    /**
     * Cancela la reserva si está activa
     */
    public boolean cancelarReserva() {
        if ("Activa".equals(getEstado())) {
            setEstado("Cancelada");
            return true;
        }
        return false;
    }

    /**
     * Detecta si hay conflicto con otro horario
     */
    public boolean tieneConflictoHorario(LocalTime inicioNuevo, LocalTime finNuevo) {
        return !(finNuevo.isBefore(getHoraInicio()) ||
                 inicioNuevo.isAfter(getHoraFin()) ||
                 inicioNuevo.equals(getHoraFin()));
    }

    /**
     * Obtiene lista de horas ocupadas por esta reserva
     * Ejemplo: 14:00-16:00 retorna [14:00, 15:00]
     */
    public List<LocalTime> obtenerHorasOcupadas() {
        List<LocalTime> horas = new ArrayList<>();
        LocalTime actual = getHoraInicio();

        while (actual.isBefore(getHoraFin())) {
            horas.add(actual);
            actual = actual.plusHours(1);
        }

        return horas;
    }

    /**
     * Verifica si la reserva ocupa una hora específica - CORREGIDO
     * @param hora Hora a verificar
     * @return true si la hora está dentro del rango de la reserva
     */
    public boolean ocupaHorario(LocalTime hora) {
        if (hora == null) {
            return false;
        }
        
        // Verifica si la hora está en el rango [horaInicio, horaFin)
        return !hora.isBefore(getHoraInicio()) && hora.isBefore(getHoraFin());
    }

    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Calcula la duración de la reserva en horas
     */
    public long getDuracionHoras() {
        return java.time.Duration.between(getHoraInicio(), getHoraFin()).toHours();
    }

    /**
     * Retorna la fecha en formato dd/MM/yyyy
     */
    public String getFechaFormateada() {
        return getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Retorna el horario en formato HH:mm - HH:mm
     */
    public String getHorarioFormateado() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        return getHoraInicio().format(fmt) + " - " + getHoraFin().format(fmt);
    }

    /**
     * Retorna información de la cancha
     */
    public String getNombreCancha() {
        return cancha.get() != null ? cancha.get().getNombre() : "Sin cancha";
    }

    /**
     * Retorna información del usuario
     */
    public String getNombreUsuario() {
        return usuario.get() != null ? usuario.get().getNombre() : "Sin usuario";
    }

    // ==================== MÉTODOS OVERRIDE ====================
    
    @Override
    public String toString() {
        return String.format("Reserva #%d - %s - %s - %s",
                getIdReserva(),
                getFechaFormateada(),
                getHorarioFormateado(),
                getNombreCancha());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Reserva)) return false;
        Reserva r = (Reserva) obj;
        return getIdReserva() == r.getIdReserva();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getIdReserva());
    }
}
