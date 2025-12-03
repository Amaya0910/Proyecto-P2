package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Administracion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminReservasView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;
    private final MenuAdminView menuAnterior;

    public AdminReservasView(Stage stage, GestorCanchas gestor, Administracion admin, MenuAdminView menuAnterior) {
        this.stage = stage;
        this.gestor = gestor;
        this.admin = admin;
        this.menuAnterior = menuAnterior;
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox header = crearHeader();
        root.setTop(header);
        
        VBox contenido = crearContenido();
        root.setCenter(contenido);
        
        return new Scene(root, 1000, 700);
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);");
        header.setPadding(new Insets(20, 30, 20, 30));
        
        HBox headerContent = new HBox(20);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitulo = new Label("Todas las Reservas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnVolver = new Button("← Volver al Panel");
        btnVolver.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnVolver.setOnAction(e -> volverAlMenu());
        
        headerContent.getChildren().addAll(lblTitulo, spacer, btnVolver);
        header.getChildren().add(headerContent);
        return header;
    }

    private VBox crearContenido() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);
        
        Label lblTitulo = new Label("📋 Administrar Reservas del Sistema");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblInfo = new Label("Ver, modificar o cancelar cualquier reserva");
        lblInfo.setFont(Font.font("Arial", 16));
        lblInfo.setTextFill(Color.GRAY);
        
        Label lblDesarrollo = new Label("Funcionalidad en desarrollo");
        lblDesarrollo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblDesarrollo.setTextFill(Color.web("#e74c3c"));
        lblDesarrollo.setPadding(new Insets(20, 0, 0, 0));
        
        container.getChildren().addAll(lblTitulo, lblInfo, lblDesarrollo);
        return container;
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}