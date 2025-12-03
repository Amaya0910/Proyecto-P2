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

public class MenuAdminView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;

    public MenuAdminView(Stage stage, GestorCanchas gestor, Administracion admin) {
        this.stage = stage;
        this.gestor = gestor;
        this.admin = admin;
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Header fijo en la parte superior
        root.setTop(crearHeader());
        
        // ScrollPane que envuelve todo el contenido
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(crearContenido());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f5f7fa;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        root.setCenter(scrollPane);

        return new Scene(root, 1000, 700);
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);");
        header.setPadding(new Insets(20));

        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(5);

        Label lblTipo = new Label("👨‍💼 PANEL DE ADMINISTRADOR");
        lblTipo.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTipo.setTextFill(Color.web("#e0e0e0"));

        Label lblNombre = new Label(admin.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblNombre.setTextFill(Color.WHITE);

        Label lblCorreo = new Label(admin.getCorreo());
        lblCorreo.setFont(Font.font("Arial", 12));
        lblCorreo.setTextFill(Color.web("#e0e0e0"));

        info.getChildren().addAll(lblTipo, lblNombre, lblCorreo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnCerrarSesion = new Button("Cerrar Sesión");
        btnCerrarSesion.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;"
        );
        btnCerrarSesion.setOnAction(e -> cerrarSesion());

        top.getChildren().addAll(info, spacer, btnCerrarSesion);
        header.getChildren().add(top);

        return header;
    }

    private VBox crearContenido() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);
        container.setMinHeight(600);

        // Estadísticas en la parte superior
        HBox estadisticas = crearEstadisticas();

        Label titulo = new Label("Panel de Administración");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web("#2c3e50"));

        // Grid con todas las opciones
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Fila 1
        grid.add(crearTarjetaOpcion("🏟️", "Gestionar Canchas", "Agregar, modificar o eliminar canchas",
                e -> abrirGestionCanchas()), 0, 0);

        grid.add(crearTarjetaOpcion("👥", "Gestionar Usuarios", "Ver y administrar usuarios",
                e -> abrirGestionUsuarios()), 1, 0);

        grid.add(crearTarjetaOpcion("📊", "Historial Completo", "Ver todas las reservas y estadísticas",
                e -> abrirHistorial()), 2, 0);
        
        // Fila 2
        grid.add(crearTarjetaOpcion("🗓️", "Calendario", "Ver disponibilidad por fecha",
                e -> abrirCalendario()), 0, 1);

        grid.add(crearTarjetaOpcion("⚙️", "Configuración", "Ajustes del sistema",
                e -> abrirConfiguracion()), 1, 1);

        container.getChildren().addAll(estadisticas, titulo, grid);
        return container;
    }

    private HBox crearEstadisticas() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        int totalCanchas = gestor.mostrarTodasLasCanchas().size();
        int canchasDisponibles = gestor.mostrarCanchasDisponibles().size();
        int totalReservas = gestor.listarReservasActivas().size();
        int totalUsuarios = gestor.listarUsuarios().size();

        container.getChildren().addAll(
                crearTarjetaEstadistica("🏟️", String.valueOf(totalCanchas), "Canchas", "#3498db"),
                crearTarjetaEstadistica("✔️", String.valueOf(canchasDisponibles), "Disponibles", "#2ecc71"),
                crearTarjetaEstadistica("📋", String.valueOf(totalReservas), "Reservas activas", "#e74c3c"),
                crearTarjetaEstadistica("👥", String.valueOf(totalUsuarios), "Usuarios", "#9b59b6")
        );

        return container;
    }

    private VBox crearTarjetaEstadistica(String icono, String valor, String desc, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 120);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font(35));

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        lblValor.setTextFill(Color.web(color));

        Label lblDesc = new Label(desc);
        lblDesc.setFont(Font.font("Arial", 12));
        lblDesc.setTextFill(Color.GRAY);

        card.getChildren().addAll(lblIcono, lblValor, lblDesc);
        return card;
    }

    private VBox crearTarjetaOpcion(String icono, String titulo, String subtitulo,
                                    javafx.event.EventHandler<javafx.event.ActionEvent> accion) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefSize(280, 200);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);" +
                "-fx-cursor: hand;"
        );

        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font(50));

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Label lblDesc = new Label(subtitulo);
        lblDesc.setFont(Font.font("Arial", 13));
        lblDesc.setTextFill(Color.GRAY);
        lblDesc.setWrapText(true);
        lblDesc.setAlignment(Pos.CENTER);
        lblDesc.setMaxWidth(240);

        Button btn = new Button("Acceder");
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 25;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;"
        );
        btn.setOnAction(accion);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 15, 0, 0, 5);" +
                "-fx-scale-x: 1.03;" +
                "-fx-scale-y: 1.03;" +
                "-fx-cursor: hand;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);" +
                "-fx-cursor: hand;"
        ));

        card.getChildren().addAll(lblIcono, lblTitulo, lblDesc, btn);
        return card;
    }

    private void abrirGestionCanchas() {
        stage.setScene(new GestionCanchasView(stage, gestor, admin, this).crearEscena());
    }

    private void abrirGestionUsuarios() {
        stage.setScene(new GestionUsuariosView(stage, gestor, admin, this).crearEscena());
    }

    private void abrirHistorial() {
        stage.setScene(new HistorialAdminView(stage, gestor, admin, this).crearEscena());
    }

    private void abrirCalendario() {
        stage.setScene(new CalendarioAdminView(stage, gestor, admin, this).crearEscena());
    }

    private void abrirConfiguracion() {
        stage.setScene(new ConfiguracionView(stage, gestor, admin, this).crearEscena());
    }

    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Seguro que deseas cerrar sesión?");
        alert.setContentText("Volverás a la pantalla de inicio.");

        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                stage.setScene(new LoginView(stage, gestor).crearEscena());
            }
        });
    }
}