package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuUsuarioView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Usuario usuario;

    public MenuUsuarioView(Stage stage, GestorCanchas gestor, Usuario usuario) {
        this.stage = stage;
        this.gestor = gestor;
        this.usuario = usuario;
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Barra superior
        VBox header = crearHeader();
        root.setTop(header);
        
        // ScrollPane para el contenido
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Contenido central con opciones
        VBox contenido = crearContenido();
        scroll.setContent(contenido);
        
        root.setCenter(scroll);
        
        Scene scene = new Scene(root, 1000, 700);
        return scene;
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, green, green);");
        header.setPadding(new Insets(20, 30, 20, 30));
        
        HBox contenidoHeader = new HBox();
        contenidoHeader.setAlignment(Pos.CENTER_LEFT);
        contenidoHeader.setSpacing(20);
        
        // Información del usuario
        VBox infoUsuario = new VBox(5);
        Label lblBienvenida = new Label("Bienvenido de nuevo");
        lblBienvenida.setFont(Font.font("Arial", 14));
        lblBienvenida.setTextFill(Color.web("#e0e0e0"));
        
        Label lblNombre = new Label(usuario.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblNombre.setTextFill(Color.WHITE);
        
        Label lblCorreo = new Label(usuario.getCorreo());
        lblCorreo.setFont(Font.font("Arial", 12));
        lblCorreo.setTextFill(Color.web("#e0e0e0"));
        
        infoUsuario.getChildren().addAll(lblBienvenida, lblNombre, lblCorreo);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botón cerrar sesión
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
        
        contenidoHeader.getChildren().addAll(infoUsuario, spacer, btnCerrarSesion);
        header.getChildren().add(contenidoHeader);
        
        return header;
    }

    private VBox crearContenido() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);
        container.setMinHeight(600); // Altura mínima para asegurar scroll
        
        Label lblTitulo = new Label("¿Qué deseas hacer hoy?");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        // Grid con las opciones
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(900);
        
        // Fila 1
        grid.add(crearTarjetaOpcion(
            "🏟️", "Ver Canchas", "Explora todas las canchas disponibles",
            e -> abrirVerCanchas()
        ), 0, 0);
        
        grid.add(crearTarjetaOpcion(
            "🔍", "Buscar Cancha", "Encuentra la cancha perfecta para ti",
            e -> abrirBuscarCancha()
        ), 1, 0);
        
        grid.add(crearTarjetaOpcion(
            "➕", "Nueva Reserva", "Reserva una cancha ahora",
            e -> abrirNuevaReserva()
        ), 2, 0);
        
        // Fila 2
        grid.add(crearTarjetaOpcion(
            "📋", "Mis Reservas", "Revisa tus reservas actuales",
            e -> abrirMisReservas()
        ), 0, 1);
        
        grid.add(crearTarjetaOpcion(
            "❌", "Cancelar Reserva", "Cancela una reserva existente",
            e -> abrirCancelarReserva()
        ), 1, 1);
        
        container.getChildren().addAll(lblTitulo, grid);
        return container;
    }

    private VBox crearTarjetaOpcion(String icono, String titulo, String descripcion, 
                                    javafx.event.EventHandler<javafx.event.ActionEvent> accion) {
        VBox tarjeta = new VBox(15);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(30));
        tarjeta.setPrefSize(280, 200);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
            "-fx-cursor: hand;"
        );
        
        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font(50));
        
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblDescripcion = new Label(descripcion);
        lblDescripcion.setFont(Font.font("Arial", 13));
        lblDescripcion.setTextFill(Color.GRAY);
        lblDescripcion.setWrapText(true);
        lblDescripcion.setAlignment(Pos.CENTER);
        lblDescripcion.setMaxWidth(240);
        
        Button btnAccion = new Button("Ir");
        btnAccion.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 30;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnAccion.setOnAction(accion);
        
        tarjeta.getChildren().addAll(lblIcono, lblTitulo, lblDescripcion, btnAccion);
        
        // Efecto hover
        tarjeta.setOnMouseEntered(e -> {
            tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(103,126,234,0.4), 15, 0, 0, 5);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.03;" +
                "-fx-scale-y: 1.03;"
            );
        });
        
        tarjeta.setOnMouseExited(e -> {
            tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                "-fx-cursor: hand;"
            );
        });
        
        return tarjeta;
    }

    // Métodos para abrir las diferentes vistas
    private void abrirVerCanchas() {
        VerCanchasView vista = new VerCanchasView(stage, gestor, usuario, this);
        stage.setScene(vista.crearEscena());
    }

    private void abrirBuscarCancha() {
        BuscarCanchaView vista = new BuscarCanchaView(stage, gestor, usuario, this);
        stage.setScene(vista.crearEscena());
    }

    private void abrirNuevaReserva() {
        NuevaReservaView vista = new NuevaReservaView(stage, gestor, usuario, this);
        stage.setScene(vista.crearEscena());
    }

    private void abrirMisReservas() {
        MisReservasView vista = new MisReservasView(stage, gestor, usuario, this);
        stage.setScene(vista.crearEscena());
    }

    private void abrirCancelarReserva() {
        CancelarReservaView vista = new CancelarReservaView(stage, gestor, usuario, this);
        stage.setScene(vista.crearEscena());
    }
    

    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Estás seguro que deseas cerrar sesión?");
        alert.setContentText("Serás redirigido a la pantalla de inicio");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoginView loginView = new LoginView(stage, gestor);
                stage.setScene(loginView.crearEscena());
            }
        });
    }
}