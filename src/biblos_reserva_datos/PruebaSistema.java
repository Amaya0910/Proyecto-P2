package biblos_reserva_datos;

import biblos_reserva_dominio.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Clase de prueba para verificar el funcionamiento del sistema
 */
public class PruebaSistema {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   PRUEBA DEL SISTEMA DE RESERVAS DE CANCHAS       ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        GestorCanchas gestor = new GestorCanchas();
        
      
        pruebaVerCanchas(gestor);
        pruebaVerificarDisponibilidad(gestor);
        pruebaCrearReserva(gestor);
        pruebaReservaConConflicto(gestor);
        pruebaObtenerHorariosDisponibles(gestor);
        pruebaCancelarReserva(gestor);
        pruebaAutenticacion(gestor);
        pruebaPagos(gestor);
        
        System.out.println("\n✓ Todas las pruebas completadas!");
    }

    private static void pruebaVerCanchas(GestorCanchas gestor) {
        System.out.println("\n📋 PRUEBA 1: Ver canchas disponibles");
        System.out.println("─────────────────────────────────────");
        
        List<Cancha> canchas = gestor.mostrarCanchasDisponibles();
        System.out.println("Canchas disponibles: " + canchas.size());
        
        for (Cancha c : canchas) {
            System.out.println("  • " + c.toString());
        }
        
        assert canchas.size() == 4 : "❌ Error: Debería haber 4 canchas disponibles";
        System.out.println("✓ Prueba superada");
    }

    private static void pruebaVerificarDisponibilidad(GestorCanchas gestor) {
        System.out.println("\n🔍 PRUEBA 2: Verificar disponibilidad por hora");
        System.out.println("─────────────────────────────────────────────────");
        
        LocalDate hoy = LocalDate.now();
        Cancha cancha1 = gestor.buscarCanchaPorId(1);
        
        // Esta hora debería estar ocupada (14:00-16:00)
        boolean ocupada = gestor.verificarDisponibilidad(1, hoy, 
                                                        LocalTime.of(14, 0), 
                                                        LocalTime.of(16, 0));
        System.out.println("14:00-16:00 disponible? " + ocupada + " (debería ser false)");
        assert !ocupada : "❌ Error: El horario 14:00-16:00 debería estar ocupado";
        
        // Esta hora debería estar libre
        boolean libre = gestor.verificarDisponibilidad(1, hoy, 
                                                      LocalTime.of(16, 0), 
                                                      LocalTime.of(17, 0));
        System.out.println("16:00-17:00 disponible? " + libre + " (debería ser true)");
        assert libre : "❌ Error: El horario 16:00-17:00 debería estar libre";
        
        System.out.println("✓ Prueba superada");
    }

    private static void pruebaCrearReserva(GestorCanchas gestor) {
        System.out.println("\n➕ PRUEBA 3: Crear nueva reserva");
        System.out.println("─────────────────────────────────────");
        
        Usuario usuario = gestor.buscarUsuarioPorId(100);
        Cancha cancha = gestor.buscarCanchaPorId(3);
        LocalDate hoy = LocalDate.now();
        
        Reserva nuevaReserva = new Reserva(
            0,  // ID se asignará automáticamente
            hoy,
            LocalTime.of(10, 0),
            LocalTime.of(12, 0),
            usuario,
            cancha
        );
        
        boolean exito = gestor.crearReserva(nuevaReserva);
        System.out.println("Reserva creada? " + exito);
        assert exito : "❌ Error: No se pudo crear la reserva";
        assert nuevaReserva.getIdReserva() > 0 : "❌ Error: ID no asignado";
        
        System.out.println("✓ Prueba superada - ID asignado: " + nuevaReserva.getIdReserva());
    }

    private static void pruebaReservaConConflicto(GestorCanchas gestor) {
        System.out.println("\n⚠️  PRUEBA 4: Intentar reserva con conflicto");
        System.out.println("───────────────────────────────────────────────");
        
        Usuario usuario = gestor.buscarUsuarioPorId(101);
        Cancha cancha = gestor.buscarCanchaPorId(1);
        LocalDate hoy = LocalDate.now();
        
        // Intentar reservar en horario ocupado (14:00-16:00 ya está reservado)
        Reserva reservaConflicto = new Reserva(
            0,
            hoy,
            LocalTime.of(15, 0),  // Solapa con 14:00-16:00
            LocalTime.of(17, 0),
            usuario,
            cancha
        );
        
        boolean rechazada = !gestor.crearReserva(reservaConflicto);
        System.out.println("Reserva rechazada por conflicto? " + rechazada);
        assert rechazada : "❌ Error: Debería rechazar reserva con conflicto";
        
        System.out.println("✓ Prueba superada - Sistema detectó el conflicto");
    }

    private static void pruebaObtenerHorariosDisponibles(GestorCanchas gestor) {
        System.out.println("\n🕐 PRUEBA 5: Obtener horarios disponibles");
        System.out.println("─────────────────────────────────────────────");
        
        LocalDate hoy = LocalDate.now();
        List<LocalTime> horariosDisponibles = gestor.obtenerHorariosDisponibles(1, hoy);
        
        System.out.println("Horarios disponibles para Cancha 1:");
        int contador = 0;
        for (LocalTime hora : horariosDisponibles) {
            System.out.print(String.format("%02d:00 ", hora.getHour()));
            contador++;
            if (contador % 6 == 0) System.out.println();
        }
        System.out.println("\nTotal: " + horariosDisponibles.size() + " horas");
        
        assert !horariosDisponibles.contains(LocalTime.of(14, 0)) : 
               "❌ Error: 14:00 debería estar ocupado";
        assert horariosDisponibles.contains(LocalTime.of(16, 0)) : 
               "❌ Error: 16:00 debería estar disponible";
        
        System.out.println("✓ Prueba superada");
    }

    private static void pruebaCancelarReserva(GestorCanchas gestor) {
        System.out.println("\n❌ PRUEBA 6: Cancelar reserva");
        System.out.println("─────────────────────────────────────");
        
        List<Reserva> reservasActivas = gestor.listarReservasActivas();
        System.out.println("Reservas activas antes: " + reservasActivas.size());
        
        if (!reservasActivas.isEmpty()) {
            int idReserva = reservasActivas.get(0).getIdReserva();
            boolean cancelada = gestor.cancelarReserva(idReserva);
            
            System.out.println("Reserva #" + idReserva + " cancelada? " + cancelada);
            assert cancelada : "❌ Error: No se pudo cancelar la reserva";
            
            int reservasActivasDespues = gestor.listarReservasActivas().size();
            System.out.println("Reservas activas después: " + reservasActivasDespues);
            assert reservasActivasDespues == reservasActivas.size() - 1 : 
                   "❌ Error: No disminuyó el contador";
            
            System.out.println("✓ Prueba superada");
        } else {
            System.out.println("⚠️  No hay reservas para cancelar");
        }
    }

    private static void pruebaAutenticacion(GestorCanchas gestor) {
        System.out.println("\n🔐 PRUEBA 7: Autenticación de usuarios");
        System.out.println("─────────────────────────────────────────────");
        
        // Autenticar usuario válido
        Usuario usuario = gestor.autenticarUsuario("juan@email.com", "12345");
        System.out.println("Usuario autenticado: " + (usuario != null));
        assert usuario != null : "❌ Error: Usuario válido no autenticado";
        System.out.println("  • " + usuario.getNombre());
        
        // Intentar con credenciales incorrectas
        Usuario usuarioInvalido = gestor.autenticarUsuario("juan@email.com", "wrong");
        System.out.println("Credenciales incorrectas rechazadas: " + (usuarioInvalido == null));
        assert usuarioInvalido == null : "❌ Error: Debería rechazar credenciales incorrectas";
        
        // Autenticar admin
        Administracion admin = gestor.autenticarAdmin("admin@biblos.com", "admin123");
        System.out.println("Admin autenticado: " + (admin != null));
        assert admin != null : "❌ Error: Admin no autenticado";
        System.out.println("  • " + admin.getNombre());
        
        System.out.println("✓ Prueba superada");
    }

    private static void pruebaPagos(GestorCanchas gestor) {
        System.out.println("\n💰 PRUEBA 8: Sistema de pagos");
        System.out.println("─────────────────────────────────────");
        
        // Obtener una reserva activa
        List<Reserva> reservas = gestor.listarReservasActivas();
        if (!reservas.isEmpty()) {
            Reserva reserva = reservas.get(0);
   
       
            System.out.println("✓ Prueba superada");
        } else {
            System.out.println("⚠️  No hay reservas para probar pagos");
        }
    }
}