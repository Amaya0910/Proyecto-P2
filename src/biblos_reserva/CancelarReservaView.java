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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CancelarReservaView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Usuario usuario;
    private final MenuUsuarioView menuAnterior;
    private VBox contenedorReservas;
    private ComboBox<String> cmbFiltro;

    public CancelarReservaView(Stage stage, GestorCanchas gestor, Usuario usuario, MenuUsuarioView menuAnterior) {
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
        
        Label lblTitulo = new Label("❌ Cancelar Reserva");
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
        
        Label lblTitulo = new Label("Cancelar una Reserva");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblSubtitulo = new Label("Selecciona la reserva que deseas cancelar");
        lblSubtitulo.setFont(Font.font("Arial", 14));
        lblSubtitulo.setTextFill(Color.GRAY);
        
        // Panel de filtros
        HBox panelFiltros = crearPanelFiltros();
        
        // Contenedor de reservas
        contenedorReservas = new VBox(15);
        contenedorReservas.setAlignment(Pos.TOP_CENTER);
        contenedorReservas.setMaxWidth(900);
        
        // Cargar reservas activas del usuario
        cargarReservas("Todas");
        
        container.getChildren().addAll(lblTitulo, lblSubtitulo, panelFiltros, contenedorReservas);
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
        
        Label lblFiltro = new Label("🔍 Mostrar:");
        lblFiltro.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        cmbFiltro = new ComboBox<>();
        cmbFiltro.setPrefWidth(200);
        cmbFiltro.setPrefHeight(40);
        cmbFiltro.getItems().addAll("Todas", "Próximas", "Hoy");
        cmbFiltro.setValue("Todas");
        cmbFiltro.setStyle("-fx-font-size: 13;");
        cmbFiltro.setOnAction(e -> cargarReservas(cmbFiltro.getValue()));
        
        Button btnActualizar = new Button("🔄 Actualizar");
        btnActualizar.setPrefHeight(40);
        btnActualizar.setStyle(
            "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnActualizar.setOnAction(e -> cargarReservas(cmbFiltro.getValue()));
        
        panel.getChildren().addAll(lblFiltro, cmbFiltro, btnActualizar);
        return panel;
    }

    private void cargarReservas(String filtro) {
        contenedorReservas.getChildren().clear();
        
        try {
            List<Reserva> todasReservas = gestor.listarReservasActivas();
            
            if (todasReservas == null || todasReservas.isEmpty()) {
                mostrarSinReservas();
                return;
            }
            
            LocalDate hoy = LocalDate.now();
            
            // CORRECCIÓN: usar getIdUsuario()
            List<Reserva> reservasUsuario = todasReservas.stream()
                .filter(r -> r.getUsuario().getIdUsuario() == usuario.getIdUsuario())
                .filter(r -> !r.getFecha().isBefore(hoy)) // Solo futuras y de hoy
                .filter(r -> {
                    // Aplicar filtro adicional
                    if (filtro.equals("Próximas")) {
                        return r.getFecha().isAfter(hoy);
                    } else if (filtro.equals("Hoy")) {
                        return r.getFecha().isEqual(hoy);
                    }
                    return true; // "Todas"
                })
                .collect(Collectors.toList());
            
            // Ordenar por fecha (más cercanas primero)
            reservasUsuario.sort((r1, r2) -> {
                int fechaComp = r1.getFecha().compareTo(r2.getFecha());
                if (fechaComp != 0) return fechaComp;
                return r1.getHoraInicio().compareTo(r2.getHoraInicio());
            });
            
            if (reservasUsuario.isEmpty()) {
                mostrarSinReservas(filtro);
                return;
            }
            
            // Mostrar estadísticas
            HBox estadisticas = crearEstadisticas(reservasUsuario, hoy);
            contenedorReservas.getChildren().add(estadisticas);
            
            Label lblTotal = new Label("📋 " + reservasUsuario.size() + " reserva(s) " + filtro.toLowerCase());
            lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            lblTotal.setTextFill(Color.web("#2c3e50"));
            lblTotal.setPadding(new Insets(20, 0, 10, 0));
            contenedorReservas.getChildren().add(lblTotal);
            
            // Mostrar cada reserva
            for (Reserva reserva : reservasUsuario) {
                VBox tarjeta = crearTarjetaReserva(reserva, hoy);
                contenedorReservas.getChildren().add(tarjeta);
            }
            
        } catch (Exception e) {
            mostrarError("Error al cargar las reservas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox crearEstadisticas(List<Reserva> reservas, LocalDate hoy) {
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(20));
        
        int total = reservas.size();
        long hoyCount = reservas.stream()
            .filter(r -> r.getFecha().isEqual(hoy))
            .count();
        long proximasCount = reservas.stream()
            .filter(r -> r.getFecha().isAfter(hoy))
            .count();
        
        stats.getChildren().addAll(
            crearTarjetaEstadistica("📊", String.valueOf(total), "Total", "#3498db"),
            crearTarjetaEstadistica("🔥", String.valueOf(hoyCount), "Hoy", "#e74c3c"),
            crearTarjetaEstadistica("📅", String.valueOf(proximasCount), "Próximas", "#27ae60")
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

    private VBox crearTarjetaReserva(Reserva reserva, LocalDate hoy) {
        VBox tarjeta = new VBox(15);
        tarjeta.setPadding(new Insets(25));
        tarjeta.setMaxWidth(850);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Encabezado
        HBox encabezado = new HBox(15);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        
        VBox infoBox = new VBox(3);
        Label lblId = new Label("Reserva #" + reserva.getIdReserva());
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblId.setTextFill(Color.web("#2c3e50"));
        
        Label lblCancha = new Label(reserva.getCancha().getNombre());
        lblCancha.setFont(Font.font("Arial", 14));
        lblCancha.setTextFill(Color.GRAY);
        
        infoBox.getChildren().addAll(lblId, lblCancha);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Badge dinámico
        Label lblBadge = crearBadgeEstado(reserva.getFecha(), hoy);
        
        encabezado.getChildren().addAll(infoBox, spacer, lblBadge);
        
        Separator sep1 = new Separator();
        
        // Información de la reserva
        HBox infoReserva = new HBox(40);
        infoReserva.setPadding(new Insets(10, 0, 10, 0));
        
        VBox fechaBox = new VBox(5);
        Label lblFechaTitulo = new Label("📅 FECHA");
        lblFechaTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblFechaTitulo.setTextFill(Color.GRAY);
        
        Label lblFechaValor = new Label(formatearFecha(reserva.getFecha()));
        lblFechaValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblFechaValor.setTextFill(Color.web("#2c3e50"));
        
        fechaBox.getChildren().addAll(lblFechaTitulo, lblFechaValor);
        
        VBox horarioBox = new VBox(5);
        Label lblHorarioTitulo = new Label("🕐 HORARIO");
        lblHorarioTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblHorarioTitulo.setTextFill(Color.GRAY);
        
        Label lblHorarioValor = new Label(formatearHorario(reserva));
        lblHorarioValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblHorarioValor.setTextFill(Color.web("#2c3e50"));
        
        horarioBox.getChildren().addAll(lblHorarioTitulo, lblHorarioValor);
        
        infoReserva.getChildren().addAll(fechaBox, horarioBox);
        
        Separator sep2 = new Separator();
        
        // Botones de acción
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnDetalles = new Button("Ver Detalles");
        btnDetalles.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #3498db;" +
            "-fx-border-color: #3498db;" +
            "-fx-border-width: 2;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-cursor: hand;"
        );
        btnDetalles.setOnAction(e -> mostrarDetalles(reserva));
        
        Button btnCancelar = new Button("❌ CANCELAR RESERVA");
        btnCancelar.setPrefHeight(45);
        btnCancelar.setStyle(
            "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnCancelar.setOnAction(e -> confirmarCancelacion(reserva));
        
        // Efecto hover en botón cancelar
        btnCancelar.setOnMouseEntered(e -> {
            btnCancelar.setStyle(
                "-fx-background-color: #c0392b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        btnCancelar.setOnMouseExited(e -> {
            btnCancelar.setStyle(
                "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;"
            );
        });
        
        botones.getChildren().addAll(btnDetalles, btnCancelar);
        
        tarjeta.getChildren().addAll(encabezado, sep1, infoReserva, sep2, botones);
        
        return tarjeta;
    }

    private Label crearBadgeEstado(LocalDate fechaReserva, LocalDate hoy) {
        String texto;
        String estilo;
        
        if (fechaReserva.isEqual(hoy)) {
            texto = "🔥 HOY";
            estilo = "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
        } else {
            texto = "📅 Próxima";
            estilo = "-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2;";
        }
        
        Label badge = new Label(texto);
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        badge.setPadding(new Insets(5, 15, 5, 15));
        badge.setStyle(estilo + "-fx-background-radius: 15;");
        
        return badge;
    }

    private String formatearFecha(LocalDate fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }

    private String formatearHorario(Reserva reserva) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return reserva.getHoraInicio().format(formatter) + " - " + 
               reserva.getHoraFin().format(formatter);
    }

    private void confirmarCancelacion(Reserva reserva) {
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("⚠️ Confirmar Cancelación");
        confirmacion.setHeaderText("¿Estás seguro de cancelar esta reserva?");
        
        String mensaje = String.format(
            "Esta acción NO se puede deshacer.\n\n" +
            "Reserva: #%d\n" +
            "Cancha: %s\n" +
            "Fecha: %s\n" +
            "Horario: %s",
            reserva.getIdReserva(),
            reserva.getCancha().getNombre(),
            formatearFecha(reserva.getFecha()),
            formatearHorario(reserva)
        );
        
        confirmacion.setContentText(mensaje);
        
        ButtonType btnConfirmar = new ButtonType("Sí, Cancelar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnVolver = new ButtonType("No, Volver", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnConfirmar, btnVolver);
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnConfirmar) {
                cancelarReserva(reserva);
            }
        });
    }

    private void cancelarReserva(Reserva reserva) {
        boolean exito = gestor.cancelarReserva(reserva.getIdReserva());
        
        if (exito) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("✅ Cancelación Exitosa");
            alerta.setHeaderText("Reserva cancelada correctamente");
            alerta.setContentText(
                "La reserva #" + reserva.getIdReserva() + " ha sido cancelada.\n\n" +
                "Cancha: " + reserva.getCancha().getNombre() + "\n" +
                "Fecha: " + formatearFecha(reserva.getFecha())
            );
            alerta.showAndWait();
            
            // Recargar las reservas
            cargarReservas(cmbFiltro.getValue());
            
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("❌ Error");
            alerta.setHeaderText("No se pudo cancelar la reserva");
            alerta.setContentText(
                "Ocurrió un error al intentar cancelar la reserva.\n" +
                "Por favor, intenta nuevamente o contacta al administrador."
            );
            alerta.showAndWait();
        }
    }

    private void mostrarDetalles(Reserva reserva) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Reserva");
        alert.setHeaderText("Reserva #" + reserva.getIdReserva());
        
        LocalDate hoy = LocalDate.now();
        String estadoTemporal = reserva.getFecha().isEqual(hoy) ? "HOY" : "Próxima";
        
        String detalles = String.format(
            "━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "🏟️  CANCHA\n" +
            "   %s\n" +
            "   ID: #%d\n\n" +
            "📅  FECHA\n" +
            "   %s\n" +
            "   Estado: %s\n\n" +
            "🕐  HORARIO\n" +
            "   %s\n\n" +
            "👤  USUARIO\n" +
            "   %s\n" +
            "   %s\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━",
            reserva.getCancha().getNombre(),
            reserva.getCancha().getId(),
            formatearFecha(reserva.getFecha()),
            estadoTemporal,
            formatearHorario(reserva),
            usuario.getNombre(),
            usuario.getCorreo()
        );
        
        alert.setContentText(detalles);
        alert.showAndWait();
    }

    private void mostrarSinReservas() {
        mostrarSinReservas("Todas");
    }

    private void mostrarSinReservas(String filtro) {
        VBox sinReservas = new VBox(20);
        sinReservas.setAlignment(Pos.CENTER);
        sinReservas.setPadding(new Insets(60));
        
        Label lblIcono = new Label("📋");
        lblIcono.setFont(Font.font(70));
        
        String mensaje = filtro.equals("Todas") ? 
            "No tienes reservas activas para cancelar" :
            "No tienes reservas " + filtro.toLowerCase();
        
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));
        
        Label lblSugerencia = new Label("Solo puedes cancelar reservas futuras o de hoy");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);
        
        Button btnVerReservas = new Button("📋 Ver Mis Reservas");
        btnVerReservas.setPrefSize(200, 45);
        btnVerReservas.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnVerReservas.setOnAction(e -> {
            MisReservasView vista = new MisReservasView(stage, gestor, usuario, menuAnterior);
            stage.setScene(vista.crearEscena());
        });
        
        sinReservas.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia, btnVerReservas);
        contenedorReservas.getChildren().add(sinReservas);
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
        contenedorReservas.getChildren().add(errorBox);
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}
