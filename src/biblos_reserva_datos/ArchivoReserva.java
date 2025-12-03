package biblos_reserva_datos;

import biblos_reserva_dominio.Cancha;
import biblos_reserva_dominio.Reserva;
import biblos_reserva_dominio.Usuario;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de IBDReserva que persiste datos en archivo TXT
 * Formato: idReserva|idUsuario|idCancha|fecha|horaInicio|horaFin|estado
 */
public class ArchivoReserva implements IBDReserva {
    
    private final String rutaArchivo;
    private final IBDUsuario bdUsuario;
    private final IBDCancha bdCancha;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;
    
    public ArchivoReserva(String rutaArchivo, IBDUsuario bdUsuario, IBDCancha bdCancha) {
        this.rutaArchivo = rutaArchivo;
        this.bdUsuario = bdUsuario;
        this.bdCancha = bdCancha;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        crearArchivoSiNoExiste();
    }
    
    private void crearArchivoSiNoExiste() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            try {
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al crear archivo de reservas: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void crear(Reserva reserva) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            writer.write(convertirReservaALinea(reserva));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
        }
    }
    
    @Override
    public Reserva buscarPorId(int id) {
        List<Reserva> reservas = listarTodas();
        
        for (Reserva r : reservas) {
            if (r.getIdReserva() == id) {
                return r;
            }
        }
        
        return null;
    }
    
    @Override
    public List<Reserva> listarTodas() {
        List<Reserva> reservas = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    Reserva reserva = convertirLineaAReserva(linea);
                    if (reserva != null) {
                        reservas.add(reserva);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Archivo no existe aún, retornar lista vacía
        } catch (IOException e) {
            System.err.println("Error al leer reservas: " + e.getMessage());
        }
        
        return reservas;
    }
    
    @Override
    public List<Reserva> listarActivas() {
        List<Reserva> todasReservas = listarTodas();
        List<Reserva> reservasActivas = new ArrayList<>();
        
        for (Reserva r : todasReservas) {
            if ("Activa".equals(r.getEstado())) {
                reservasActivas.add(r);
            }
        }
        
        return reservasActivas;
    }
    
    @Override
    public List<Reserva> listarPorUsuario(int idUsuario) {
        List<Reserva> todasReservas = listarTodas();
        List<Reserva> reservasUsuario = new ArrayList<>();
        
        for (Reserva r : todasReservas) {
            if (r.getUsuario().getIdUsuario() == idUsuario) {
                reservasUsuario.add(r);
            }
        }
        
        return reservasUsuario;
    }
    
    @Override
    public List<Reserva> listarPorCancha(int idCancha) {
        List<Reserva> todasReservas = listarTodas();
        List<Reserva> reservasCancha = new ArrayList<>();
        
        for (Reserva r : todasReservas) {
            if (r.getCancha().getId() == idCancha) {
                reservasCancha.add(r);
            }
        }
        
        return reservasCancha;
    }
    
    @Override
    public List<Reserva> listarPorFecha(LocalDate fecha) {
        List<Reserva> todasReservas = listarTodas();
        List<Reserva> reservasFecha = new ArrayList<>();
        
        for (Reserva r : todasReservas) {
            if (r.getFecha().equals(fecha)) {
                reservasFecha.add(r);
            }
        }
        
        return reservasFecha;
    }
    
    @Override
    public List<Reserva> listarPorCanchaYFecha(int idCancha, LocalDate fecha) {
        List<Reserva> todasReservas = listarTodas();
        List<Reserva> reservasFiltradas = new ArrayList<>();
        
        for (Reserva r : todasReservas) {
            if (r.getCancha().getId() == idCancha && 
                r.getFecha().equals(fecha) &&
                !"Cancelada".equals(r.getEstado())) {
                reservasFiltradas.add(r);
            }
        }
        
        return reservasFiltradas;
    }
    
    @Override
    public boolean cancelar(int id) {
        List<Reserva> reservas = listarTodas();
        boolean encontrado = false;
        
        for (Reserva r : reservas) {
            if (r.getIdReserva() == id) {
                r.setEstado("Cancelada");
                encontrado = true;
                break;
            }
        }
        
        if (encontrado) {
            guardarTodas(reservas);
        }
        
        return encontrado;
    }
    
    @Override
    public boolean existe(int id) {
        return buscarPorId(id) != null;
    }
    
    @Override
    public boolean hayConflicto(int idCancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<Reserva> reservasDia = listarPorCanchaYFecha(idCancha, fecha);
        
        for (Reserva r : reservasDia) {
            // Verificar si hay superposición de horarios
            if (!(fin.isBefore(r.getHoraInicio()) || fin.equals(r.getHoraInicio()) ||
                  inicio.isAfter(r.getHoraFin()) || inicio.equals(r.getHoraFin()))) {
                return true; // Hay conflicto
            }
        }
        
        return false;
    }
    
    @Override
    public int size() {
        return listarTodas().size();
    }
    
    // ============== MÉTODOS AUXILIARES PRIVADOS ==============
    
    /**
     * Convierte una Reserva a formato de línea para guardar en archivo
     * Formato: idReserva|idUsuario|idCancha|fecha|horaInicio|horaFin|estado
     */
    private String convertirReservaALinea(Reserva reserva) {
        return String.format("%d|%d|%d|%s|%s|%s|%s",
            reserva.getIdReserva(),
            reserva.getUsuario().getIdUsuario(),
            reserva.getCancha().getId(),
            reserva.getFecha().format(dateFormatter),
            reserva.getHoraInicio().format(timeFormatter),
            reserva.getHoraFin().format(timeFormatter),
            reserva.getEstado()
        );
    }
    
    /**
     * Convierte una línea del archivo a objeto Reserva
     */
    private Reserva convertirLineaAReserva(String linea) {
        try {
            String[] partes = linea.split("\\|");
            
            if (partes.length != 7) {
                System.err.println("Línea con formato inválido: " + linea);
                return null;
            }
            
            int idReserva = Integer.parseInt(partes[0]);
            int idUsuario = Integer.parseInt(partes[1]);
            int idCancha = Integer.parseInt(partes[2]);
            LocalDate fecha = LocalDate.parse(partes[3], dateFormatter);
            LocalTime horaInicio = LocalTime.parse(partes[4], timeFormatter);
            LocalTime horaFin = LocalTime.parse(partes[5], timeFormatter);
            String estado = partes[6];
            
            // Buscar usuario y cancha
            Usuario usuario = bdUsuario.buscarPorId(idUsuario);
            Cancha cancha = bdCancha.buscarPorId(idCancha);
            
            if (usuario == null || cancha == null) {
                System.err.println("Usuario o cancha no encontrada para reserva ID: " + idReserva);
                return null;
            }
            
            Reserva reserva = new Reserva(idReserva, usuario, cancha, fecha, horaInicio, horaFin);
            reserva.setEstado(estado);
            
            return reserva;
            
        } catch (Exception e) {
            System.err.println("Error al parsear línea: " + linea + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Guarda todas las reservas en el archivo (reemplaza contenido completo)
     */
    private void guardarTodas(List<Reserva> reservas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            for (Reserva r : reservas) {
                writer.write(convertirReservaALinea(r));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo de reservas: " + e.getMessage());
        }
    }
}
