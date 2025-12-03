package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import javafx.application.Application;
import javafx.stage.Stage;




public class Principal extends Application {
    
    private GestorCanchas gestor;

    @Override
    public void start(Stage primaryStage) {
        try {
            
            gestor = new GestorCanchas("datos/usuarios.txt", "datos/reservas.txt");
            
            primaryStage.setTitle("Biblos Reserva - Sistema de Reservas de Canchas");
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
      
            LoginView loginView = new LoginView(primaryStage, gestor);
            primaryStage.setScene(loginView.crearEscena());
            
          
            primaryStage.show();
            
            System.out.println("✓ Aplicación JavaFX iniciada correctamente");
            
        } catch (Exception e) {
            System.err.println(" Error al iniciar la aplicación:");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("\n  Cerrando aplicación...");
        System.out.println("✓ Aplicación cerrada correctamente");
    }

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║        BIBLOS RESERVA - SISTEMA DE CANCHAS        ║");
        System.out.println("║            Versión 1.0 - JavaFX Edition            ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        launch(args);
    }
}