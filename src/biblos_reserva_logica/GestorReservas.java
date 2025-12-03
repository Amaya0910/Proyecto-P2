package biblos_reserva_logica;

import biblos_reserva_dominio.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestorReservas implements IGestionCanchas, IGestionReservas, IGestionUsuario {
    
    // Listas observables para JavaFX
    private final ObservableList<Cancha> listaCanchas;
    private final ObservableList<Reserva> listaReservas;
    private final ObservableList<Usuario> listaUsuarios;
    private Administracion administrador;
    
    // Contadores para IDs autoincrementales
    private int contadorReservas = 1;
    private int contadorCanchas = 6;
    private int contadorUsuarios = 1;

    public GestorReservas() {
        this.listaCanchas = FXCollections.observableArrayList();
        this.listaReservas = FXCollections.observableArrayList();
        this.listaUsuarios = FXCollections.observableArrayList();
        inicializarDatosPrueba();
    }

    // ========================================
    // GESTIÓN DE CANCHAS
    // ========================================
    
    @Override
    public boolean agregarCancha(Cancha cancha) {
        if (cancha == null) return false;
        
        // Asignar ID si no tiene
        if (cancha.getId() == 0) {
            cancha.setId(contadorCanchas++);
        }
        
        // Verificar que no exista otra con el mismo nombre
        if (buscarCanchaPorNombre(cancha.getNombre()) != null) {
            System.out.println("❌ Ya existe una cancha con ese nombre");
            return false;
        }
        
        listaCanchas.add(cancha);
        System.out.println("✓ Cancha agregada: " + cancha.getNombre());
        return true;
    }

    @Override
    public Cancha buscarCanchaPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        
        return listaCanchas.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Cancha buscarCanchaPorId(int id) {
        return listaCanchas.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cancha> listarTodasLasCanchas() {
        return new ArrayList<>(listaCanchas);
    }

    @Override
    public List<Cancha> listarCanchasDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        return listaCanchas.stream()
                .filter(c -> !c.getEstado().equals("Mantenimiento"))
                .filter(c -> verificarDisponibilidad(c.getId(), fecha, horaInicio, horaFin))
                .collect(Collectors.toList());
    }

    @Override
    public boolean modificarCancha(Cancha cancha) {
        if (cancha == null) return false;
        
        Cancha existente = buscarCanchaPorId(cancha.getId());
        if (existente == null) return false;
        
        existente.setNombre(cancha.getNombre());
        existente.setEstado(cancha.getEstado());
        return true;
    }

    @Override
    public boolean eliminarCancha(int idCancha) {
        Cancha cancha = buscarCanchaPorId(idCancha);
        if (cancha == null) return false;
        
        // Verificar si tiene reservas activas
        boolean tieneReservasActivas = listaReservas.stream()
                .anyMatch(r -> r.getCancha().getId() == idCancha && 
                              r.getEstado().equals("Activa"));
        
        if (tieneReservasActivas) {
            System.out.println("❌ No se puede eliminar: tiene reservas activas");
            return false;
        }
        
        listaCanchas.remove(cancha);
        return true;
    }

    // ========================================
    // GESTIÓN DE RESERVAS (SISTEMA POR HORAS)
    // ========================================
    
    @Override
    public boolean crearReserva(Reserva reserva) {
        if (reserva == null) return false;
        
        // Validaciones básicas
        if (reserva.getCancha() == null || reserva.getUsuario() == null) {
            System.out.println("❌ Reserva inválida: faltan datos");
            return false;
        }
        
        // Verificar que la cancha existe
        Cancha cancha = buscarCanchaPorId(reserva.getCancha().getId());
        if (cancha == null) {
            System.out.println("❌ La cancha no existe");
            return false;
        }
        
        // Verificar que no esté en mantenimiento
        if (cancha.getEstado().equals("Mantenimiento")) {
            System.out.println("❌ La cancha está en mantenimiento");
            return false;
        }
        
        // CLAVE: Verificar disponibilidad en el horario específico
        if (!verificarDisponibilidad(cancha.getId(), reserva.getFecha(), 
                                    reserva.getHoraInicio(), reserva.getHoraFin())) {
            System.out.println("❌ El horario no está disponible");
            return false;
        }
        
        // Asignar ID y agregar
        if (reserva.getIdReserva() == 0) {
            reserva.setIdReserva(contadorReservas++);
        }
        
        reserva.setEstado("Activa");
        listaReservas.add(reserva);
        
        System.out.println("✓ Reserva creada exitosamente");
        System.out.println("  - Cancha: " + cancha.getNombre());
        System.out.println("  - Fecha: " + reserva.getFechaFormateada());
        System.out.println("  - Horario: " + reserva.getHorarioFormateado());
        
        return true;
    }

    @Override
    public boolean verificarDisponibilidad(int idCancha, LocalDate fecha, 
                                          LocalTime horaInicio, LocalTime horaFin) {
        // Buscar reservas activas de esa cancha en esa fecha
        List<Reserva> reservasDelDia = listaReservas.stream()
                .filter(r -> r.getCancha().getId() == idCancha)
                .filter(r -> r.getFecha().equals(fecha))
                .filter(r -> r.getEstado().equals("Activa"))
                .collect(Collectors.toList());
        
        // Verificar si hay conflicto con alguna reserva
        for (Reserva reservaExistente : reservasDelDia) {
            if (reservaExistente.tieneConflictoHorario(horaInicio, horaFin)) {
                return false; // Hay conflicto
            }
        }
        
        return true; // Está disponible
    }

    @Override
    public List<LocalTime> obtenerHorariosDisponibles(int idCancha, LocalDate fecha) {
        List<LocalTime> horariosDisponibles = new ArrayList<>();
        
        // Horario de operación: 6:00 AM a 11:00 PM
        LocalTime horaApertura = LocalTime.of(6, 0);
        LocalTime horaCierre = LocalTime.of(23, 0);
        
        LocalTime horaActual = horaApertura;
        
        while (horaActual.isBefore(horaCierre)) {
            // Verificar si está disponible por 1 hora desde esta hora
            LocalTime horaFin = horaActual.plusHours(1);
            
            if (verificarDisponibilidad(idCancha, fecha, horaActual, horaFin)) {
                horariosDisponibles.add(horaActual);
            }
            
            horaActual = horaActual.plusHours(1);
        }
        
        return horariosDisponibles;
    }

    @Override
    public boolean cancelarReserva(int idReserva) {
        Reserva reserva = listaReservas.stream()
                .filter(r -> r.getIdReserva() == idReserva)
                .findFirst()
                .orElse(null);
        
        if (reserva == null) {
            System.out.println("❌ Reserva no encontrada");
            return false;
        }
        
        if (!reserva.getEstado().equals("Activa")) {
            System.out.println("❌ La reserva ya está " + reserva.getEstado().toLowerCase());
            return false;
        }
        
        reserva.cancelarReserva();
        System.out.println("✓ Reserva cancelada exitosamente");
        return true;
    }

    @Override
    public List<Reserva> listarReservasPorUsuario(int idUsuario) {
        return listaReservas.stream()
                .filter(r -> r.getUsuario().getIdUsuario() == idUsuario)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> listarReservasPorCancha(int idCancha, LocalDate fecha) {
        return listaReservas.stream()
                .filter(r -> r.getCancha().getId() == idCancha)
                .filter(r -> r.getFecha().equals(fecha))
                .filter(r -> r.getEstado().equals("Activa"))
                .sorted((r1, r2) -> r1.getHoraInicio().compareTo(r2.getHoraInicio()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> listarTodasLasReservas() {
        return new ArrayList<>(listaReservas);
    }

    @Override
    public List<Reserva> listarReservasActivas() {
        return listaReservas.stream()
                .filter(r -> r.getEstado().equals("Activa"))
                .collect(Collectors.toList());
    }

    // ========================================
    // GESTIÓN DE USUARIOS
    // ========================================
    
    @Override
    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        
        // Verificar que el correo no esté registrado
        if (listaUsuarios.stream().anyMatch(u -> u.getCorreo().equals(usuario.getCorreo()))) {
            System.out.println("❌ El correo ya está registrado");
            return false;
        }
        
        if (usuario.getIdUsuario() == 0) {
            usuario.setIdUsuario(contadorUsuarios++);
        }
        
        listaUsuarios.add(usuario);
        System.out.println("✓ Usuario registrado: " + usuario.getNombre());
        return true;
    }

    @Override
    public Usuario autenticarUsuario(String correo, String contraseña) {
        return listaUsuarios.stream()
                .filter(u -> u.getCorreo().equals(correo))
                .filter(u -> u.getContraseña().equals(contraseña))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Usuario buscarUsuarioPorId(int idUsuario) {
        return listaUsuarios.stream()
                .filter(u -> u.getIdUsuario() == idUsuario)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Usuario> listarTodosLosUsuarios() {
        return new ArrayList<>(listaUsuarios);
    }

    // ========================================
    // ADMINISTRADOR
    // ========================================
    
    public Administracion getAdministrador() {
        return administrador;
    }

    public Administracion autenticarAdmin(String correo, String contraseña) {
        if (administrador != null && administrador.getCorreo().equals(correo) 
            && administrador.getContraseña().equals(contraseña)) {
            return administrador;
        }
        return null;
    }

    // ========================================
    // GETTERS PARA JAVAFX (ObservableLists)
    // ========================================
    
    public ObservableList<Cancha> getListaCanchas() {
        return listaCanchas;
    }

    public ObservableList<Reserva> getListaReservas() {
        return listaReservas;
    }

    public ObservableList<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    // ========================================
    // DATOS DE PRUEBA
    // ========================================
    
    private void inicializarDatosPrueba() {
        // Administrador
        administrador = new Administracion(1, "Admin", "admin@biblos.com", "admin123");
        
        // Canchas
        agregarCancha(new Cancha(1, "Cancha Fútbol 5 - VIP", "Disponible"));
        agregarCancha(new Cancha(2, "Cancha Fútbol 7 - Premium", "Disponible"));
        agregarCancha(new Cancha(3, "Cancha Fútbol 11 - Estadio", "Disponible"));
        agregarCancha(new Cancha(4, "Cancha Fútbol 5 - Express", "Disponible"));
        agregarCancha(new Cancha(5, "Cancha Fútbol 8 - Pro", "Mantenimiento"));
        
        // Usuario de prueba
        Usuario usuarioPrueba = new Usuario(100, "3001234567", "Juan Pérez", 
                                           "juan@email.com", "12345");
        registrarUsuario(usuarioPrueba);
        
        // Algunas reservas de ejemplo
        Cancha cancha1 = buscarCanchaPorId(1);
        LocalDate hoy = LocalDate.now();
        
        // Reserva para hoy a las 14:00-16:00
        Reserva r1 = new Reserva(1, hoy, LocalTime.of(14, 0), LocalTime.of(16, 0), 
                                usuarioPrueba, cancha1);
        crearReserva(r1);
        
        // Reserva para hoy a las 18:00-20:00
        Reserva r2 = new Reserva(2, hoy, LocalTime.of(18, 0), LocalTime.of(20, 0), 
                                usuarioPrueba, cancha1);
        crearReserva(r2);
        
        System.out.println("\n✓ Sistema inicializado con datos de prueba");
    }
}