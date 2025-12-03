package biblos_reserva_datos;
import biblos_reserva_dominio.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de datos con capacidad de persistencia
 * Usa las interfaces IBDCancha, IBDUsuario, IBDReserva
 */
public class RepositorioDatos {
    
    private static final String ARCHIVO_CANCHAS = "datos/canchas.dat";
    private static final String ARCHIVO_USUARIOS = "datos/usuarios.txt";
    private static final String ARCHIVO_RESERVAS = "datos/reservas.txt";
    
    private final GestorCanchas gestor;

    // Constructor por defecto - usa memoria
    public RepositorioDatos() {
        this.gestor = new GestorCanchas();
    }

    // Constructor con persistencia en archivos TXT
    public RepositorioDatos(boolean usarArchivos) {
        if (usarArchivos) {
            crearDirectorioDatos();
            this.gestor = new GestorCanchas(ARCHIVO_USUARIOS, ARCHIVO_RESERVAS);
        } else {
            this.gestor = new GestorCanchas();
        }
    }

    // Constructor con rutas personalizadas
    public RepositorioDatos(String rutaUsuarios, String rutaReservas) {
        crearDirectorioDatos();
        this.gestor = new GestorCanchas(rutaUsuarios, rutaReservas);
    }

    // Constructor con gestor existente
    public RepositorioDatos(GestorCanchas gestor) {
        this.gestor = gestor;
    }

    // ========================================
    // MÉTODOS DE PERSISTENCIA PARA CANCHAS (BINARIO)
    // ========================================
    
    public boolean guardarCanchas() {
        try {
            crearDirectorioDatos();
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(ARCHIVO_CANCHAS))) {
                List<Cancha> canchas = gestor.mostrarTodasLasCanchas();
                oos.writeObject(canchas);
            }
            System.out.println("✓ Canchas guardadas exitosamente");
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error al guardar canchas: " + e.getMessage());
            return false;
        }
    }

    public boolean cargarCanchas() {
        try {
            File archivo = new File(ARCHIVO_CANCHAS);
            if (!archivo.exists()) {
                System.out.println("ℹ No hay archivo de canchas, usando datos por defecto");
                return false;
            }
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(ARCHIVO_CANCHAS))) {
                @SuppressWarnings("unchecked")
                List<Cancha> canchas = (List<Cancha>) ois.readObject();
                IBDCancha bdCancha = gestor.getBdCancha();
                // Limpiar y recargar
                for (Cancha c : bdCancha.listarTodas()) {
                    bdCancha.eliminar(c.getId());
                }
                for (Cancha c : canchas) {
                    bdCancha.agregar(c);
                }
            }
            System.out.println("✓ Canchas cargadas exitosamente");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al cargar canchas: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================
    
    private void crearDirectorioDatos() {
        File directorio = new File("datos");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    // ========================================
    // EXPORTAR REPORTES EN FORMATO TEXTO
    // ========================================
    
    public boolean exportarReservasDia(LocalDate fecha, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("═══════════════════════════════════════════════════");
            writer.println("         REPORTE DE RESERVAS - " + fecha);
            writer.println("═══════════════════════════════════════════════════");
            writer.println();
            
            List<Reserva> reservasDelDia = gestor.getBdReserva().listarPorFecha(fecha)
                    .stream()
                    .sorted((r1, r2) -> r1.getHoraInicio().compareTo(r2.getHoraInicio()))
                    .toList();
            
            if (reservasDelDia.isEmpty()) {
                writer.println("No hay reservas para este día.");
            } else {
                for (Reserva r : reservasDelDia) {
                    writer.println("ID Reserva: " + r.getIdReserva());
                    writer.println("Cancha: " + r.getCancha().getNombre());
                    writer.println("Usuario: " + r.getUsuario().getNombre());
                    writer.println("Horario: " + r.getHorarioFormateado());
                    writer.println("---------------------------------------------------");
                }
            }
            
            writer.println();
            writer.println("Total de reservas: " + reservasDelDia.size());
            
            System.out.println("✓ Reporte exportado a: " + nombreArchivo);
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al exportar: " + e.getMessage());
            return false;
        }
    }

    public boolean exportarUsuarios(String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("═══════════════════════════════════════════════════");
            writer.println("         LISTADO DE USUARIOS REGISTRADOS");
            writer.println("═══════════════════════════════════════════════════");
            writer.println();
            
            List<Usuario> usuarios = gestor.listarUsuarios();
            
            if (usuarios.isEmpty()) {
                writer.println("No hay usuarios registrados.");
            } else {
                for (Usuario u : usuarios) {
                    writer.println("ID: " + u.getIdUsuario());
                    writer.println("Nombre: " + u.getNombre());
                    writer.println("Correo: " + u.getCorreo());
                    writer.println("---------------------------------------------------");
                }
            }
            
            writer.println();
            writer.println("Total de usuarios: " + usuarios.size());
            
            System.out.println("✓ Reporte exportado a: " + nombreArchivo);
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al exportar: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // BACKUP COMPLETO
    // ========================================
    
    public boolean generarBackup(String nombreBackup) {
        try {
            String directorio = "backups/";
            new File(directorio).mkdirs();
            
            String rutaCompleta = directorio + nombreBackup + "_" + 
                                LocalDate.now() + ".bak";
            
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(rutaCompleta))) {
                
                DatosBackup backup = new DatosBackup(
                    gestor.mostrarTodasLasCanchas(),
                    gestor.listarUsuarios(),
                    gestor.listarReservas()
                );
                
                oos.writeObject(backup);
            }
            
            System.out.println("✓ Backup creado: " + rutaCompleta);
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error al crear backup: " + e.getMessage());
            return false;
        }
    }

    public boolean restaurarBackup(String rutaBackup) {
        try {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(rutaBackup))) {
                
                @SuppressWarnings("unchecked")
                DatosBackup backup = (DatosBackup) ois.readObject();
                
                // Restaurar canchas
                IBDCancha bdCancha = gestor.getBdCancha();
                for (Cancha c : backup.canchas) {
                    if (!bdCancha.existe(c.getId())) {
                        bdCancha.agregar(c);
                    }
                }
                
                // Restaurar usuarios
                IBDUsuario bdUsuario = gestor.getBdUsuario();
                for (Usuario u : backup.usuarios) {
                    if (!bdUsuario.existe(u.getCorreo())) {
                        bdUsuario.registrar(u);
                    }
                }
                
                // Restaurar reservas
                IBDReserva bdReserva = gestor.getBdReserva();
                for (Reserva r : backup.reservas) {
                    if (!bdReserva.existe(r.getIdReserva())) {
                        bdReserva.crear(r);
                    }
                }
            }
            
            System.out.println("✓ Backup restaurado: " + rutaBackup);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al restaurar backup: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // CLASE AUXILIAR PARA BACKUP
    // ========================================
    
    private static class DatosBackup implements Serializable {
        private static final long serialVersionUID = 1L;
        
        List<Cancha> canchas;
        List<Usuario> usuarios;
        List<Reserva> reservas;

        public DatosBackup(List<Cancha> canchas, List<Usuario> usuarios, List<Reserva> reservas) {
            this.canchas = new ArrayList<>(canchas);
            this.usuarios = new ArrayList<>(usuarios);
            this.reservas = new ArrayList<>(reservas);
        }
    }

    // ========================================
    // GETTER
    // ========================================
    
    public GestorCanchas getGestor() {
        return gestor;
    }
}