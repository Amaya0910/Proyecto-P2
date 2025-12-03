package biblos_reserva_datos;

import biblos_reserva_dominio.Cancha;
import biblos_reserva_dominio.Reserva;
import biblos_reserva_dominio.Usuario;
import biblos_reserva_dominio.Administracion;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// ✅ Asegúrate que la clase sea public
public class GestorCanchas {

    private IBDCancha bdCancha;
    private IBDUsuario bdUsuario;
    private IBDReserva bdReserva;
    private Administracion administrador;

    private int contadorReservas = 1;
    private int contadorCanchas = 6;
    private int contadorUsuarios = 1;

    // Constructor por defecto - usa memoria
    public GestorCanchas() {
        this.bdCancha = new ListCancha();
        this.bdUsuario = new ListUsuario();
        this.bdReserva = new ListReserva();
        inicializarAdmin();
        inicializarCanchasPorDefecto();
    }

    // Constructor para usar archivos TXT
    public GestorCanchas(String rutaUsuarios, String rutaReservas) {
        this.bdCancha = new ListCancha();
        this.bdUsuario = new ArchivoUsuario(rutaUsuarios);
        inicializarAdmin();
        inicializarCanchasPorDefecto();
        this.bdReserva = new ArchivoReserva(rutaReservas, bdUsuario, bdCancha);
        actualizarContadores();
    }

    // Constructor con inyección de dependencias
    public GestorCanchas(IBDCancha bdCancha, IBDUsuario bdUsuario, IBDReserva bdReserva) {
        this.bdCancha = bdCancha;
        this.bdUsuario = bdUsuario;
        this.bdReserva = bdReserva;
        inicializarAdmin();
    }

    private void inicializarAdmin() {
        administrador = new Administracion(1, "Admin", "admin@biblos.com", "admin123");
    }

    private void inicializarCanchasPorDefecto() {
        bdCancha.agregar(new Cancha(1, "Cancha Futbol 5 - VIP", "Disponible"));
        bdCancha.agregar(new Cancha(2, "Cancha Futbol 7 - Premium", "Disponible"));
        bdCancha.agregar(new Cancha(3, "Cancha Futbol 11 - Estadio", "Disponible"));
        bdCancha.agregar(new Cancha(4, "Cancha Futbol 5 - Express", "Disponible"));
        bdCancha.agregar(new Cancha(5, "Cancha Futbol 8 - Pro", "Mantenimiento"));
    }

    private void actualizarContadores() {
        List<Usuario> usuarios = bdUsuario.listarTodos();
        for (Usuario u : usuarios) {
            if (u.getIdUsuario() >= contadorUsuarios) {
                contadorUsuarios = u.getIdUsuario() + 1;
            }
        }
        List<Reserva> reservas = bdReserva.listarTodas();
        for (Reserva r : reservas) {
            if (r.getIdReserva() >= contadorReservas) {
                contadorReservas = r.getIdReserva() + 1;
            }
        }
    }

    // ============== MÉTODOS DE CANCHAS ==============

    public Cancha buscarCanchaPorNombre(String nombre) {
        return bdCancha.buscarPorNombre(nombre);
    }

    public Cancha buscarCanchaPorId(int id) {
        return bdCancha.buscarPorId(id);
    }

    public List<Cancha> mostrarTodasLasCanchas() {
        return bdCancha.listarTodas();
    }

    public List<Cancha> mostrarCanchasDisponibles() {
        return bdCancha.listarDisponibles();
    }

    public List<Cancha> mostrarCanchasDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        List<Cancha> disponibles = new ArrayList<>();
        for (Cancha c : bdCancha.listarTodas()) {
            if (!c.getEstado().equals("Mantenimiento") &&
                verificarDisponibilidad(c.getId(), fecha, horaInicio, horaFin)) {
                disponibles.add(c);
            }
        }
        return disponibles;
    }

    public boolean agregarCancha(Cancha cancha) {
        if (cancha == null) return false;
        if (cancha.getId() == 0) cancha.setId(contadorCanchas++);
        if (bdCancha.buscarPorNombre(cancha.getNombre()) != null) return false;
        bdCancha.agregar(cancha);
        return true;
    }

    public boolean modificarCancha(Cancha cancha) {
        return bdCancha.modificar(cancha);
    }

    public boolean eliminarCancha(int idCancha) {
        List<Reserva> reservasCancha = bdReserva.listarPorCancha(idCancha);
        for (Reserva r : reservasCancha) {
            if (r.getEstado().equals("Activa")) {
                return false;
            }
        }
        return bdCancha.eliminar(idCancha);
    }

    // ============== MÉTODOS DE RESERVAS ==============

    public boolean crearReserva(Reserva reserva) {
        if (reserva == null) return false;
        if (reserva.getCancha() == null) return false;
        if (reserva.getUsuario() == null) return false;

        Cancha cancha = bdCancha.buscarPorId(reserva.getCancha().getId());
        if (cancha == null) return false;
        if (cancha.getEstado().equals("Mantenimiento")) return false;

        if (bdReserva.hayConflicto(cancha.getId(), reserva.getFecha(),
                reserva.getHoraInicio(), reserva.getHoraFin())) return false;

        if (reserva.getIdReserva() == 0) {
            reserva.setIdReserva(contadorReservas++);
        }
        reserva.setEstado("Activa");
        bdReserva.crear(reserva);
        return true;
    }

    public boolean verificarDisponibilidad(int idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return !bdReserva.hayConflicto(idCancha, fecha, inicio, fin);
    }

    public boolean cancelarReserva(int idReserva) {
        return bdReserva.cancelar(idReserva);
    }

    public List<Reserva> listarReservas() {
        return bdReserva.listarTodas();
    }

    public List<Reserva> listarReservasPorUsuario(int idUsuario) {
        return bdReserva.listarPorUsuario(idUsuario);
    }

    public List<Reserva> listarReservasActivas() {
        return bdReserva.listarActivas();
    }

    public List<LocalTime> obtenerHorariosDisponibles(int idCancha, LocalDate fecha) {
        List<LocalTime> horarios = new ArrayList<>();
        for (int h = 6; h <= 22; h++) {
            horarios.add(LocalTime.of(h, 0));
        }
        List<Reserva> reservasDia = bdReserva.listarPorCanchaYFecha(idCancha, fecha);
        List<LocalTime> ocupados = new ArrayList<>();
        for (Reserva r : reservasDia) {
            ocupados.addAll(r.obtenerHorasOcupadas());
        }
        horarios.removeIf(h -> ocupados.contains(h));
        return horarios;
    }

    // ============== MÉTODOS DE USUARIOS ==============

    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        if (bdUsuario.existe(usuario.getCorreo())) return false;
        if (usuario.getIdUsuario() == 0) {
            usuario.setIdUsuario(contadorUsuarios++);
        }
        bdUsuario.registrar(usuario);
        return true;
    }

    public Usuario autenticarUsuario(String correo, String contrasena) {
        return bdUsuario.autenticar(correo, contrasena);
    }

    public Usuario buscarUsuarioPorId(int idUsuario) {
        return bdUsuario.buscarPorId(idUsuario);
    }

    public List<Usuario> listarUsuarios() {
        return bdUsuario.listarTodos();
    }

    public boolean eliminarUsuario(int idUsuario) {
        return bdUsuario.eliminar(idUsuario);
    }

    // ============== MÉTODOS DE ADMINISTRACIÓN ==============

    public Administracion getAdministrador() {
        return administrador;
    }

    public Administracion autenticarAdmin(String correo, String contrasena) {
        if (administrador == null) return null;
        if (administrador.getCorreo().equals(correo) &&
                administrador.getContraseña().equals(contrasena)) {
            return administrador;
        }
        return null;
    }

    // ============== GETTERS ==============

    public IBDCancha getBdCancha() { 
        return bdCancha; 
    }
    
    public IBDUsuario getBdUsuario() { 
        return bdUsuario; 
    }
    
    public IBDReserva getBdReserva() { 
        return bdReserva; 
    }
}
