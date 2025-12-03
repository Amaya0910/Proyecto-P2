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

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MisReservasView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Usuario usuario;
    private final MenuUsuarioView menuAnterior;
    private VBox contenedorReservas;

    public MisReservasView(Stage stage, GestorCanchas gestor, Usuario usuario, MenuUsuarioView menuAnterior) {
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
        header.setStyle("-fx-background-color: linear-gradient(to right, green, green);");
        header.setPadding(new Insets(20, 30, 20, 30));
        
        HBox headerContent = new HBox(20);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTitulo = new Label("📋 Mis Reservas");
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
        
        Label lblTitulo = new Label("Mis Reservas Activas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblSubtitulo = new Label("Gestiona y revisa tus reservas actuales");
        lblSubtitulo.setFont(Font.font("Arial", 14));
        lblSubtitulo.setTextFill(Color.GRAY);
        
        contenedorReservas = new VBox(15);
        contenedorReservas.setAlignment(Pos.TOP_CENTER);
        contenedorReservas.setMaxWidth(900);
        
        cargarReservas();
        
        container.getChildren().addAll(lblTitulo, lblSubtitulo, contenedorReservas);
        return container;
    }

    private void cargarReservas() {
        contenedorReservas.getChildren().clear();
        
        try {
            // CORRECCIÓN: usar getIdUsuario() (coincide con gestorCanchas)
            List<Reserva> reservas = gestor.listarReservasPorUsuario(usuario.getIdUsuario());
            
            if (reservas == null || reservas.isEmpty()) {
                mostrarSinReservas();
            } else {
                Label lblTotal = new Label("✅ Total de Reservas: " + reservas.size());
                lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                lblTotal.setTextFill(Color.web("#27ae60"));
                lblTotal.setPadding(new Insets(10, 0, 10, 0));
                contenedorReservas.getChildren().add(lblTotal);
                
                for (Reserva reserva : reservas) {
                    VBox tarjetaReserva = crearTarjetaReserva(reserva);
                    contenedorReservas.getChildren().add(tarjetaReserva);
                }
            }
        } catch (Exception e) {
            mostrarError("Error al cargar reservas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox crearTarjetaReserva(Reserva reserva) {
        VBox tarjeta = new VBox(15);
        tarjeta.setPadding(new Insets(25));
        tarjeta.setMaxWidth(850);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        HBox encabezado = new HBox(15);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        
        Label lblId = new Label("Reserva #" + reserva.getIdReserva());
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblId.setTextFill(Color.web("#2c3e50"));
        
        Label lblEstado = new Label(reserva.getEstado() != null ? reserva.getEstado().toUpperCase() : "ACTIVA");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblEstado.setPadding(new Insets(5, 15, 5, 15));
        lblEstado.setStyle(
            "-fx-background-color: #d4edda;" +
            "-fx-text-fill: #155724;" +
            "-fx-background-radius: 15;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        encabezado.getChildren().addAll(lblId, lblEstado, spacer);
        
        Separator sep1 = new Separator();
        
        GridPane gridInfo = new GridPane();
        gridInfo.setHgap(30);
        gridInfo.setVgap(12);
        gridInfo.setPadding(new Insets(10, 0, 10, 0));
        
        // Cancha
        Label lblCanchaIcon = new Label("🏟️");
        lblCanchaIcon.setFont(Font.font(20));
        
        VBox infoCanchaBox = new VBox(3);
        Label lblCanchaTitulo = new Label("Cancha");
        lblCanchaTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblCanchaTitulo.setTextFill(Color.GRAY);
        
        Label lblCanchaValor = new Label(reserva.getCancha().getNombre());
        lblCanchaValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblCanchaValor.setTextFill(Color.web("#2c3e50"));
        
        infoCanchaBox.getChildren().addAll(lblCanchaTitulo, lblCanchaValor);
        
        HBox canchaBox = new HBox(10);
        canchaBox.setAlignment(Pos.CENTER_LEFT);
        canchaBox.getChildren().addAll(lblCanchaIcon, infoCanchaBox);
        
        // Fecha
        Label lblFechaIcon = new Label("📅");
        lblFechaIcon.setFont(Font.font(20));
        
        VBox infoFechaBox = new VBox(3);
        Label lblFechaTitulo = new Label("Fecha");
        lblFechaTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblFechaTitulo.setTextFill(Color.GRAY);
        
        String fechaFormateada = formatearFecha(reserva);
        Label lblFechaValor = new Label(fechaFormateada);
        lblFechaValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblFechaValor.setTextFill(Color.web("#2c3e50"));
        
        infoFechaBox.getChildren().addAll(lblFechaTitulo, lblFechaValor);
        
        HBox fechaBox = new HBox(10);
        fechaBox.setAlignment(Pos.CENTER_LEFT);
        fechaBox.getChildren().addAll(lblFechaIcon, infoFechaBox);
        
        // Horario
        Label lblHorarioIcon = new Label("🕐");
        lblHorarioIcon.setFont(Font.font(20));
        
        VBox infoHorarioBox = new VBox(3);
        Label lblHorarioTitulo = new Label("Horario");
        lblHorarioTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblHorarioTitulo.setTextFill(Color.GRAY);
        
        String horarioFormateado = formatearHorario(reserva);
        Label lblHorarioValor = new Label(horarioFormateado);
        lblHorarioValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblHorarioValor.setTextFill(Color.web("#2c3e50"));
        
        infoHorarioBox.getChildren().addAll(lblHorarioTitulo, lblHorarioValor);
        
        HBox horarioBox = new HBox(10);
        horarioBox.setAlignment(Pos.CENTER_LEFT);
        horarioBox.getChildren().addAll(lblHorarioIcon, infoHorarioBox);
        
        gridInfo.add(canchaBox, 0, 0);
        gridInfo.add(fechaBox, 1, 0);
        gridInfo.add(horarioBox, 2, 0);
        
        Separator sep2 = new Separator();
        
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnDetalles = new Button("Ver Detalles");
        btnDetalles.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #667eea;" +
            "-fx-border-color: #667eea;" +
            "-fx-border-width: 2;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnDetalles.setOnAction(e -> mostrarDetalles(reserva));
        
        Button btnCancelar = new Button("❌ Cancelar Reserva");
        btnCancelar.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        // Ahora redirige a la vista especializada de cancelación
        btnCancelar.setOnAction(e -> {
            CancelarReservaView vista = new CancelarReservaView(stage, gestor, usuario, menuAnterior);
            stage.setScene(vista.crearEscena());
        });
        
        botones.getChildren().addAll(btnDetalles, btnCancelar);
        
        tarjeta.getChildren().addAll(encabezado, sep1, gridInfo, sep2, botones);
        
        return tarjeta;
    }

    // Métodos helper para formatear
    private String formatearFecha(Reserva reserva) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return reserva.getFecha().format(formatter);
        } catch (Exception e) {
            return reserva.getFecha().toString();
        }
    }

    private String formatearHorario(Reserva reserva) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return reserva.getHoraInicio().format(formatter) + " - " + 
                   reserva.getHoraFin().format(formatter);
        } catch (Exception e) {
            return reserva.getHoraInicio().toString() + " - " + reserva.getHoraFin().toString();
        }
    }

    private void mostrarSinReservas() {
        VBox sinReservas = new VBox(20);
        sinReservas.setAlignment(Pos.CENTER);
        sinReservas.setPadding(new Insets(60));
        
        Label lblIcono = new Label("📋");
        lblIcono.setFont(Font.font(70));
        
        Label lblMensaje = new Label("No tienes reservas activas");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));
        
        Label lblSugerencia = new Label("¡Crea tu primera reserva ahora!");
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
            NuevaReservaView vista = new NuevaReservaView(stage, gestor, usuario, menuAnterior);
            stage.setScene(vista.crearEscena());
        });
        
        sinReservas.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia, btnNuevaReserva);
        contenedorReservas.getChildren().add(sinReservas);
    }

    private void mostrarError(String mensaje) {
        VBox errorBox = new VBox(15);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(40));
        errorBox.setMaxWidth(800);
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
        contenedorReservas.getChildren().add(errorBox);
    }

    private void mostrarDetalles(Reserva reserva) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Reserva");
        alert.setHeaderText("Reserva #" + reserva.getIdReserva());
        
        String detalles = String.format(
            "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "🏟️  CANCHA\n" +
            "   %s (ID: #%d)\n\n" +
            "📅  FECHA\n" +
            "   %s\n\n" +
            "🕐  HORARIO\n" +
            "   %s\n\n" +
            "👤  USUARIO\n" +
            "   %s\n" +
            "   %s\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━",
            reserva.getCancha().getNombre(),
            reserva.getCancha().getId(),
            formatearFecha(reserva),
            formatearHorario(reserva),
            usuario.getNombre(),
            usuario.getCorreo()
        );
        
        alert.setContentText(detalles);
        alert.showAndWait();
    }

    // Método de cancelación (opcional, ya no es llamado desde la tarjeta)
    private void cancelarReserva(Reserva reserva) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Reserva");
        confirmacion.setHeaderText("¿Estás seguro de cancelar esta reserva?");
        confirmacion.setContentText(
            "Cancha: " + reserva.getCancha().getNombre() + "\n" +
            "Fecha: " + formatearFecha(reserva) + "\n" +
            "Horario: " + formatearHorario(reserva)
        );
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean exito = gestor.cancelarReserva(reserva.getIdReserva());
                    
                    if (exito) {
                        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                        alerta.setTitle("Reserva Cancelada");
                        alerta.setHeaderText("✅ Reserva cancelada exitosamente");
                        alerta.setContentText("La reserva #" + reserva.getIdReserva() + " ha sido cancelada.");
                        alerta.showAndWait();
                        
                        cargarReservas();
                    } else {
                        Alert alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.setTitle("Error");
                        alerta.setHeaderText("❌ No se pudo cancelar la reserva");
                        alerta.setContentText("Por favor, intenta nuevamente.");
                        alerta.showAndWait();
                    }
                } catch (Exception e) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error");
                    alerta.setHeaderText("❌ Error al cancelar");
                    alerta.setContentText("Error: " + e.getMessage());
                    alerta.showAndWait();
                    e.printStackTrace();
                }
            }
        });
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}
