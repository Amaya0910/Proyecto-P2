package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Cancha;
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

import java.util.List;

public class VerCanchasView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Usuario usuario;
    private final MenuUsuarioView menuAnterior;
    private VBox contenedorCanchas;

    public VerCanchasView(Stage stage, GestorCanchas gestor, Usuario usuario, MenuUsuarioView menuAnterior) {
        this.stage = stage;
        this.gestor = gestor;
        this.usuario = usuario;
        this.menuAnterior = menuAnterior;
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox header = crearHeader();
        root.setTop(header);
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        
        VBox contenido = crearContenido();
        scroll.setContent(contenido);
        
        root.setCenter(scroll);
        
        return new Scene(root, 1000, 700);
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        header.setPadding(new Insets(20, 30, 20, 30));
        
        HBox headerContent = new HBox(20);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitulo = new Label("🏟️ Ver Canchas Disponibles");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnVolver = new Button("← Volver al Menú");
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
        VBox container = new VBox(25);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);
        
        // Título principal
        Label lblTitulo = new Label("Explora Nuestras Canchas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblSubtitulo = new Label("Todas las canchas disponibles para tu reserva");
        lblSubtitulo.setFont(Font.font("Arial", 14));
        lblSubtitulo.setTextFill(Color.GRAY);
        
        // Contenedor de canchas
        contenedorCanchas = new VBox(15);
        contenedorCanchas.setAlignment(Pos.TOP_CENTER);
        contenedorCanchas.setMaxWidth(900);
        
        // Cargar y mostrar todas las canchas
        cargarCanchas();
        
        container.getChildren().addAll(lblTitulo, lblSubtitulo, contenedorCanchas);
        return container;
    }

    private void cargarCanchas() {
        contenedorCanchas.getChildren().clear();
        
        List<Cancha> canchas = gestor.mostrarTodasLasCanchas();
        
        if (canchas.isEmpty()) {
            mostrarSinCanchas();
        } else {
            // Encabezado con número de canchas
            Label lblTotal = new Label("📋 Total de Canchas: " + canchas.size());
            lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            lblTotal.setTextFill(Color.web("#2c3e50"));
            lblTotal.setPadding(new Insets(10, 0, 20, 0));
            contenedorCanchas.getChildren().add(lblTotal);
            
            // Grid para mostrar las canchas
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(20);
            grid.setAlignment(Pos.CENTER);
            
            int columna = 0;
            int fila = 0;
            
            for (Cancha cancha : canchas) {
                VBox tarjeta = crearTarjetaCancha(cancha);
                grid.add(tarjeta, columna, fila);
                
                columna++;
                if (columna > 2) { // 3 columnas
                    columna = 0;
                    fila++;
                }
            }
            
            contenedorCanchas.getChildren().add(grid);
        }
    }

    private VBox crearTarjetaCancha(Cancha cancha) {
        VBox tarjeta = new VBox(12);
        tarjeta.setAlignment(Pos.TOP_LEFT);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setPrefSize(280, 220);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);" +
            "-fx-cursor: hand;"
        );
        
        // Encabezado con ícono y estado
        HBox encabezado = new HBox(10);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        
        Label lblIcono = new Label("⚽");
        lblIcono.setFont(Font.font(30));
        
        // Estado con color
        Label lblEstado = new Label(cancha.getEstado());
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblEstado.setPadding(new Insets(4, 10, 4, 10));
        lblEstado.setStyle(
            "-fx-background-radius: 12;" +
            obtenerColorEstado(cancha.getEstado())
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        encabezado.getChildren().addAll(lblIcono, spacer, lblEstado);
        
        // Nombre de la cancha
        Label lblNombre = new Label(cancha.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblNombre.setTextFill(Color.web("#2c3e50"));
        lblNombre.setWrapText(true);
        
        // ID de la cancha
        Label lblId = new Label("ID: #" + cancha.getId());
        lblId.setFont(Font.font("Arial", 12));
        lblId.setTextFill(Color.GRAY);
        
        Separator separator = new Separator();
        
        // Botones de acción
        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER);
        
        Button btnReservar = new Button("Reservar");
        btnReservar.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        
        // Solo permitir reservar si está disponible
        if (!cancha.getEstado().equalsIgnoreCase("Disponible")) {
            btnReservar.setDisable(true);
            btnReservar.setStyle(
                "-fx-background-color: #ccc;" +
                "-fx-text-fill: #666;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 15;"
            );
        }
        
        btnReservar.setOnAction(e -> abrirReservaParaCancha(cancha));
        
        Button btnVerMas = new Button("Ver Detalles");
        btnVerMas.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #667eea;" +
            "-fx-border-color: #667eea;" +
            "-fx-border-width: 2;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12;" +
            "-fx-padding: 8 15;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;"
        );
        btnVerMas.setOnAction(e -> mostrarDetallesCancha(cancha));
        
        botones.getChildren().addAll(btnReservar, btnVerMas);
        
        tarjeta.getChildren().addAll(encabezado, lblNombre, lblId, separator, botones);
        
        // Efecto hover
        tarjeta.setOnMouseEntered(e -> {
            tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(103,126,234,0.4), 15, 0, 0, 5);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        tarjeta.setOnMouseExited(e -> {
            tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);" +
                "-fx-cursor: hand;"
            );
        });
        
        return tarjeta;
    }

    private String obtenerColorEstado(String estado) {
        switch (estado.toLowerCase()) {
            case "disponible":
                return "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
            case "ocupada":
                return "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
            case "mantenimiento":
                return "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
            default:
                return "-fx-background-color: #e0e0e0; -fx-text-fill: #333;";
        }
    }

    private void mostrarSinCanchas() {
        VBox sinCanchas = new VBox(20);
        sinCanchas.setAlignment(Pos.CENTER);
        sinCanchas.setPadding(new Insets(50));
        
        Label lblIcono = new Label("🏟️");
        lblIcono.setFont(Font.font(60));
        
        Label lblMensaje = new Label("No hay canchas registradas");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));
        
        Label lblSugerencia = new Label("Contacte al administrador para más información");
        lblSugerencia.setFont(Font.font("Arial", 14));
        lblSugerencia.setTextFill(Color.GRAY);
        
        sinCanchas.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia);
        contenedorCanchas.getChildren().add(sinCanchas);
    }

    private void abrirReservaParaCancha(Cancha cancha) {
        // Redirigir a la vista de nueva reserva con la cancha pre-seleccionada
        NuevaReservaView vista = new NuevaReservaView(stage, gestor, usuario, menuAnterior);
        stage.setScene(vista.crearEscena());
    }

    private void mostrarDetallesCancha(Cancha cancha) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Cancha");
        alert.setHeaderText(cancha.getNombre());
        
        String detalles = String.format(
            "ID: #%d\n" +
            "Estado: %s\n" +
            "\nEsta cancha está %s para reservas.",
            cancha.getId(),
            cancha.getEstado(),
            cancha.getEstado().equalsIgnoreCase("Disponible") ? "disponible" : "no disponible"
        );
        
        alert.setContentText(detalles);
        
        // Agregar botón para reservar
        if (cancha.getEstado().equalsIgnoreCase("Disponible")) {
            ButtonType btnReservar = new ButtonType("Reservar Ahora");
            ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnReservar, btnCerrar);
            
            alert.showAndWait().ifPresent(response -> {
                if (response == btnReservar) {
                    abrirReservaParaCancha(cancha);
                }
            });
        } else {
            alert.showAndWait();
        }
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}