package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Reserva;
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
import java.util.stream.Collectors;

public class HistorialView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Usuario usuario;
    private final MenuUsuarioView menuAnterior;
    private VBox contenedorHistorial;
    private ComboBox<String> cmbFiltro;

    public HistorialView(Stage stage, GestorCanchas gestor, Usuario usuario, MenuUsuarioView menuAnterior) {
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
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox contenido = crearContenido();
        scroll.setContent(contenido);
        
        root.setCenter(scroll);
        
        return new Scene(root, 1000, 700);
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, green, green);");
        header.setPadding(new Insets(20, 30, 20, 30));
        
        HBox headerContent = new HBox(20);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitulo = new Label("📊 Historial de Reservas");
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
        
        Label lblTitulo = new Label("Historial Completo de Reservas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblSubtitulo = new Label("Todas tus reservas registradas en el sistema");
        lblSubtitulo.setFont(Font.font("Arial", 14));
        lblSubtitulo.setTextFill(Color.GRAY);
        
        HBox panelFiltros = crearPanelFiltros();
        
        contenedorHistorial = new VBox(15);
        contenedorHistorial.setAlignment(Pos.TOP_CENTER);
        contenedorHistorial.setMaxWidth(900);
        
        cargarHistorial("Todas");
        
        container.getChildren().addAll(lblTitulo, lblSubtitulo, panelFiltros, contenedorHistorial);
        return container;
    }

    private HBox crearPanelFiltros() {
        HBox panel = new HBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setMaxWidth(600);
        panel.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        Label lblFiltro = new Label("🔍 Filtrar:");
        lblFiltro.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        cmbFiltro = new ComboBox<>();
        cmbFiltro.setPrefWidth(200);
        cmbFiltro.setPrefHeight(40);
        cmbFiltro.getItems().addAll("Todas", "Activas", "Históricas");
        cmbFiltro.setValue("Todas");
        cmbFiltro.setStyle("-fx-font-size: 13;");
        cmbFiltro.setOnAction(e -> cargarHistorial(cmbFiltro.getValue()));
        
        Button btnActualizar = new Button("🔄 Actualizar");
        btnActualizar.setPrefHeight(40);
        btnActualizar.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnActualizar.setOnAction(e -> cargarHistorial(cmbFiltro.getValue()));
        
        panel.getChildren().addAll(lblFiltro, cmbFiltro, btnActualizar);
        return panel;
    }

    private void cargarHistorial(String filtro) {
        contenedorHistorial.getChildren().clear();
        
        try {
            List<Reserva> todasReservas = gestor.listarReservasActivas();
            
            if (todasReservas == null || todasReservas.isEmpty()) {
                mostrarSinHistorial();
                return;
            }
            
            List<Reserva> reservasUsuario = todasReservas.stream()
                .filter(r -> r.getUsuario().getId() == usuario.getId())
                .collect(Collectors.toList());
            
            if (reservasUsuario.isEmpty()) {
                mostrarSinHistorial();
                return;
            }
            
            HBox estadisticas = crearEstadisticas(reservasUsuario);
            contenedorHistorial.getChildren().add(estadisticas);
            
            Label lblTotal = new Label("📋 " + reservasUsuario.size() + " reserva(s) registrada(s)");
            lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            lblTotal.setTextFill(Color.web("#2c3e50"));
            lblTotal.setPadding(new Insets(20, 0, 10, 0));
            contenedorHistorial.getChildren().add(lblTotal);
            
            for (Reserva reserva : reservasUsuario) {
                VBox tarjeta = crearTarjetaHistorial(reserva);
                contenedorHistorial.getChildren().add(tarjeta);
            }
            
        } catch (Exception e) {
            mostrarError("Error al cargar el historial: " + e.getMessage());
        }
    }

    private HBox crearEstadisticas(List<Reserva> reservas) {
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(20));
        
        int total = reservas.size();
        long canchasUnicas = reservas.stream()
            .map(r -> r.getCancha().getId())
            .distinct()
            .count();
        
        stats.getChildren().addAll(
            crearTarjetaEstadistica("📊", String.valueOf(total), "Total Reservas", "#3498db"),
            crearTarjetaEstadistica("✅", String.valueOf(total), "Registradas", "#27ae60"),
            crearTarjetaEstadistica("🏟️", String.valueOf(canchasUnicas), "Canchas", "#9b59b6")
        );
        
        return stats;
    }

    private VBox crearTarjetaEstadistica(String icono, String valor, String titulo, String color) {
        VBox tarjeta = new VBox(8);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setPrefSize(180, 110);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font(30));
        
        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblValor.setTextFill(Color.web(color));
        
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Arial", 12));
        lblTitulo.setTextFill(Color.GRAY);
        
        tarjeta.getChildren().addAll(lblIcono, lblValor, lblTitulo);
        return tarjeta;
    }

    private VBox crearTarjetaHistorial(Reserva reserva) {
        VBox tarjeta = new VBox(12);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setMaxWidth(850);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );
        
        HBox encabezado = new HBox(15);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        
        VBox infoBox = new VBox(3);
        Label lblId = new Label("Reserva #" + reserva.getIdReserva());
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblId.setTextFill(Color.web("#2c3e50"));
        
        Label lblCancha = new Label(reserva.getCancha().getNombre());
        lblCancha.setFont(Font.font("Arial", 14));
        lblCancha.setTextFill(Color.GRAY);
        
        infoBox.getChildren().addAll(lblId, lblCancha);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblBadge = new Label("📋 Registrada");
        lblBadge.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblBadge.setPadding(new Insets(4, 12, 4, 12));
        lblBadge.setStyle(
            "-fx-background-color: #e3f2fd;" +
            "-fx-text-fill: #1976d2;" +
            "-fx-background-radius: 12;"
        );
        
        encabezado.getChildren().addAll(infoBox, spacer, lblBadge);
        
        HBox infoReserva = new HBox(40);
        infoReserva.setPadding(new Insets(10, 0, 0, 0));
        
        VBox fechaBox = new VBox(3);
        Label lblFechaTitulo = new Label("📅 FECHA");
        lblFechaTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblFechaTitulo.setTextFill(Color.GRAY);
        
        Label lblFechaValor = new Label(reserva.getFechaFormateada());
        lblFechaValor.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblFechaValor.setTextFill(Color.web("#2c3e50"));
        
        fechaBox.getChildren().addAll(lblFechaTitulo, lblFechaValor);
        
        VBox horarioBox = new VBox(3);
        Label lblHorarioTitulo = new Label("🕐 HORARIO");
        lblHorarioTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblHorarioTitulo.setTextFill(Color.GRAY);
        
        Label lblHorarioValor = new Label(reserva.getHorarioFormateado());
        lblHorarioValor.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblHorarioValor.setTextFill(Color.web("#2c3e50"));
        
        horarioBox.getChildren().addAll(lblHorarioTitulo, lblHorarioValor);
        
        infoReserva.getChildren().addAll(fechaBox, horarioBox);
        
        Button btnDetalles = new Button("Ver Detalles Completos");
        btnDetalles.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #667eea;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12;" +
            "-fx-cursor: hand;" +
            "-fx-underline: true;"
        );
        btnDetalles.setOnAction(e -> mostrarDetallesHistorial(reserva));
        
        tarjeta.getChildren().addAll(encabezado, infoReserva, btnDetalles);
        
        return tarjeta;
    }

    private void mostrarSinHistorial() {
        VBox sinHistorial = new VBox(20);
        sinHistorial.setAlignment(Pos.CENTER);
        sinHistorial.setPadding(new Insets(60));
        
        Label lblIcono = new Label("📊");
        lblIcono.setFont(Font.font(70));
        
        Label lblMensaje = new Label("No tienes historial de reservas");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));
        
        Label lblSugerencia = new Label("Realiza tu primera reserva para comenzar tu historial");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);
        
        Button btnNuevaReserva = new Button("➕ Nueva Reserva");
        btnNuevaReserva.setPrefSize(200, 45);
        btnNuevaReserva.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnNuevaReserva.setOnAction(e -> {
            // Asegúrate de que NuevaReservaView exista y tenga este constructor
            stage.setScene(menuAnterior.crearEscena());
        });
        
        sinHistorial.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia, btnNuevaReserva);
        contenedorHistorial.getChildren().add(sinHistorial);
    }

    private void mostrarError(String mensaje) {
        VBox errorBox = new VBox(15);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(40));
        errorBox.setStyle(
            "-fx-background-color: #fee;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e74c3c;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        Label lblError = new Label("⚠️ Error");
        lblError.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblError.setTextFill(Color.web("#e74c3c"));
        
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setFont(Font.font("Arial", 14));
        lblMensaje.setTextFill(Color.web("#721c24"));
        lblMensaje.setWrapText(true);
        
        errorBox.getChildren().addAll(lblError, lblMensaje);
        contenedorHistorial.getChildren().add(errorBox);
    }

    private void mostrarDetallesHistorial(Reserva reserva) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles - Historial");
        alert.setHeaderText("Reserva #" + reserva.getIdReserva());
        
        String detalles = String.format(
            "━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "🏟️  CANCHA\n" +
            "   %s\n" +
            "   ID: #%d\n" +
            "   Estado: %s\n\n" +
            "📅  INFORMACIÓN DE RESERVA\n" +
            "   Fecha: %s\n" +
            "   Horario: %s\n\n" +
            "👤  RESERVADO POR\n" +
            "   %s\n" +
            "   %s\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━",
            reserva.getCancha().getNombre(),
            reserva.getCancha().getId(),
            reserva.getCancha().getEstado(),
            reserva.getFechaFormateada(),
            reserva.getHorarioFormateado(),
            usuario.getNombre(),
            usuario.getCorreo()
        );
        
        alert.setContentText(detalles);
        alert.showAndWait();
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}