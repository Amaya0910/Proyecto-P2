package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Administracion;
import biblos_reserva_dominio.Cancha;
import biblos_reserva_dominio.Reserva;
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
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarioAdminView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;
    private final MenuAdminView menuAnterior;
    
    private YearMonth mesActual;
    private GridPane gridCalendario;
    private Label lblMesAnio;
    private ComboBox<String> cmbCancha;
    private VBox panelReservasDelDia;
    private LocalDate fechaSeleccionada;

    public CalendarioAdminView(Stage stage, GestorCanchas gestor, Administracion admin, MenuAdminView menuAnterior) {
        this.stage = stage;
        this.gestor = gestor;
        this.admin = admin;
        this.menuAnterior = menuAnterior;
        this.mesActual = YearMonth.now();
        this.fechaSeleccionada = LocalDate.now();
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

        return new Scene(root, 1200, 750);
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);");
        header.setPadding(new Insets(20, 30, 20, 30));

        HBox headerContent = new HBox(20);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);

        Label lblTitulo = new Label("🗓️ Calendario de Reservas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);

        Label lblSubtitulo = new Label("Vista general de disponibilidad y reservas");
        lblSubtitulo.setFont(Font.font("Arial", 13));
        lblSubtitulo.setTextFill(Color.web("#e0e0e0"));

        infoBox.getChildren().addAll(lblTitulo, lblSubtitulo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnVolver = new Button("← Volver al Panel");
        btnVolver.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 20;"
                + "-fx-cursor: hand;"
        );
        btnVolver.setOnAction(e -> volverAlMenu());

        headerContent.getChildren().addAll(infoBox, spacer, btnVolver);
        header.getChildren().add(headerContent);
        return header;
    }

    private VBox crearContenido() {
        VBox container = new VBox(25);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);

        HBox controles = crearControles();
        HBox estadisticas = crearEstadisticas();

        HBox contenidoPrincipal = new HBox(20);
        contenidoPrincipal.setAlignment(Pos.TOP_CENTER);

        VBox calendarioBox = crearCalendario();
        panelReservasDelDia = crearPanelReservasDelDia();

        contenidoPrincipal.getChildren().addAll(calendarioBox, panelReservasDelDia);

        container.getChildren().addAll(controles, estadisticas, contenidoPrincipal);
        return container;
    }

    private HBox crearControles() {
        HBox controles = new HBox(20);
        controles.setAlignment(Pos.CENTER);
        controles.setPadding(new Insets(20));
        controles.setMaxWidth(1000);
        controles.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        // Selector de cancha
        VBox canchaBox = new VBox(5);
        Label lblCancha = new Label("🏟️ Filtrar por Cancha");
        lblCancha.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblCancha.setTextFill(Color.web("#2c3e50"));

        cmbCancha = new ComboBox<>();
        cmbCancha.setPrefHeight(38);
        cmbCancha.setPrefWidth(250);
        cmbCancha.getItems().add("Todas las Canchas");
        
        for (Cancha cancha : gestor.mostrarTodasLasCanchas()) {
            cmbCancha.getItems().add(cancha.getNombre());
        }
        cmbCancha.setValue("Todas las Canchas");
        cmbCancha.setOnAction(e -> actualizarCalendario());

        canchaBox.getChildren().addAll(lblCancha, cmbCancha);

        // Navegación de mes
        Button btnAnterior = new Button("◀");
        btnAnterior.setPrefSize(45, 38);
        btnAnterior.setStyle(
                "-fx-background-color: #3498db;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 16;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnAnterior.setOnAction(e -> cambiarMes(-1));

        lblMesAnio = new Label(obtenerNombreMes());
        lblMesAnio.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblMesAnio.setTextFill(Color.web("#2c3e50"));
        lblMesAnio.setPrefWidth(200);
        lblMesAnio.setAlignment(Pos.CENTER);

        Button btnSiguiente = new Button("▶");
        btnSiguiente.setPrefSize(45, 38);
        btnSiguiente.setStyle(
                "-fx-background-color: #3498db;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 16;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnSiguiente.setOnAction(e -> cambiarMes(1));

        Button btnHoy = new Button("📅 Hoy");
        btnHoy.setPrefHeight(38);
        btnHoy.setStyle(
                "-fx-background-color: #2ecc71;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13;"
                + "-fx-padding: 8 20;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnHoy.setOnAction(e -> irAHoy());

        controles.getChildren().addAll(canchaBox, btnAnterior, lblMesAnio, btnSiguiente, btnHoy);
        return controles;
    }

    private HBox crearEstadisticas() {
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(10));

        // Contar reservas del mes actual
        long reservasMesActual = gestor.listarReservasActivas().stream()
                .filter(r -> {
                    // Asumiendo que existe un método para obtener la fecha
                    try {
                        LocalDate fechaReserva = LocalDate.parse(r.getFechaFormateada(), 
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        return fechaReserva.getYear() == mesActual.getYear() && 
                               fechaReserva.getMonthValue() == mesActual.getMonthValue();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        int totalCanchas = gestor.mostrarTodasLasCanchas().size();
        int diasEnMes = mesActual.lengthOfMonth();

        stats.getChildren().addAll(
                crearMiniEstadistica("📋", String.valueOf(reservasMesActual), "Reservas este mes", "#e74c3c"),
                crearMiniEstadistica("🏟️", String.valueOf(totalCanchas), "Canchas activas", "#3498db"),
                crearMiniEstadistica("📅", String.valueOf(diasEnMes), "Días en el mes", "#9b59b6")
        );

        return stats;
    }

    private VBox crearMiniEstadistica(String icono, String valor, String titulo, String color) {
        VBox mini = new VBox(5);
        mini.setAlignment(Pos.CENTER);
        mini.setPadding(new Insets(15));
        mini.setPrefWidth(150);
        mini.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        Label lblIcono = new Label(icono);
        lblIcono.setFont(Font.font(24));

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblValor.setTextFill(Color.web(color));

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Arial", 11));
        lblTitulo.setTextFill(Color.GRAY);

        mini.getChildren().addAll(lblIcono, lblValor, lblTitulo);
        return mini;
    }

    private VBox crearCalendario() {
        VBox calendarioBox = new VBox(15);
        calendarioBox.setPadding(new Insets(25));
        calendarioBox.setPrefWidth(700);
        calendarioBox.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        // Encabezado de días de la semana
        GridPane headerDias = new GridPane();
        headerDias.setHgap(5);
        headerDias.setVgap(5);

        String[] dias = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (int i = 0; i < 7; i++) {
            Label lblDia = new Label(dias[i]);
            lblDia.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lblDia.setTextFill(Color.web("#7f8c8d"));
            lblDia.setPrefWidth(90);
            lblDia.setAlignment(Pos.CENTER);
            headerDias.add(lblDia, i, 0);
        }

        // Grid del calendario
        gridCalendario = new GridPane();
        gridCalendario.setHgap(5);
        gridCalendario.setVgap(5);

        actualizarCalendario();

        calendarioBox.getChildren().addAll(headerDias, gridCalendario);
        return calendarioBox;
    }

    private void actualizarCalendario() {
        gridCalendario.getChildren().clear();

        LocalDate primerDia = mesActual.atDay(1);
        int diaSemanaInicio = primerDia.getDayOfWeek().getValue() % 7; // Domingo = 0
        int diasEnMes = mesActual.lengthOfMonth();

        int fila = 0;
        int columna = diaSemanaInicio;

        for (int dia = 1; dia <= diasEnMes; dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            VBox celdaDia = crearCeldaDia(fecha);
            gridCalendario.add(celdaDia, columna, fila);

            columna++;
            if (columna == 7) {
                columna = 0;
                fila++;
            }
        }

        lblMesAnio.setText(obtenerNombreMes());
    }

    private VBox crearCeldaDia(LocalDate fecha) {
        VBox celda = new VBox(5);
        celda.setPrefSize(90, 80);
        celda.setAlignment(Pos.TOP_CENTER);
        celda.setPadding(new Insets(8));

        boolean esHoy = fecha.equals(LocalDate.now());
        boolean esSeleccionado = fecha.equals(fechaSeleccionada);
        boolean esPasado = fecha.isBefore(LocalDate.now());

        // Contar reservas para este día
        long cantidadReservas = contarReservasDelDia(fecha);

        String estiloBase = "-fx-background-radius: 8; -fx-cursor: hand;";
        
        if (esHoy) {
            celda.setStyle(estiloBase + "-fx-background-color: #3498db; -fx-border-color: #2980b9; -fx-border-width: 2;");
        } else if (esSeleccionado) {
            celda.setStyle(estiloBase + "-fx-background-color: #e74c3c; -fx-border-color: #c0392b; -fx-border-width: 2;");
        } else if (esPasado) {
            celda.setStyle(estiloBase + "-fx-background-color: #ecf0f1;");
        } else {
            celda.setStyle(estiloBase + "-fx-background-color: #f8f9fa;");
        }

        Label lblDia = new Label(String.valueOf(fecha.getDayOfMonth()));
        lblDia.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblDia.setTextFill(esHoy || esSeleccionado ? Color.WHITE : Color.web("#2c3e50"));

        // Indicador de reservas
        if (cantidadReservas > 0) {
            Label lblReservas = new Label(cantidadReservas + " reserva" + (cantidadReservas > 1 ? "s" : ""));
            lblReservas.setFont(Font.font("Arial", 9));
            lblReservas.setTextFill(esHoy || esSeleccionado ? Color.web("#e0e0e0") : Color.web("#7f8c8d"));
            lblReservas.setPadding(new Insets(2, 5, 2, 5));
            lblReservas.setStyle(
                    "-fx-background-color: " + (esHoy || esSeleccionado ? "rgba(255,255,255,0.2)" : "#e8f5e9") + ";"
                    + "-fx-background-radius: 8;"
            );
            celda.getChildren().addAll(lblDia, lblReservas);
        } else {
            celda.getChildren().add(lblDia);
        }

        // Agregar evento de clic
        celda.setOnMouseClicked(e -> {
            fechaSeleccionada = fecha;
            actualizarCalendario();
            actualizarPanelReservas();
        });

        // Efectos hover
        celda.setOnMouseEntered(e -> {
            if (!esSeleccionado && !esHoy) {
                celda.setStyle(estiloBase + "-fx-background-color: #e8eaf6; -fx-border-color: #3498db; -fx-border-width: 1;");
            }
        });

        celda.setOnMouseExited(e -> {
            if (!esSeleccionado && !esHoy) {
                celda.setStyle(estiloBase + "-fx-background-color: " + (esPasado ? "#ecf0f1" : "#f8f9fa") + ";");
            }
        });

        return celda;
    }

    private long contarReservasDelDia(LocalDate fecha) {
        String fechaStr = String.format("%02d/%02d/%d", 
            fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());

        return gestor.listarReservasActivas().stream()
                .filter(r -> {
                    if (cmbCancha.getValue().equals("Todas las Canchas")) {
                        return r.getFechaFormateada().equals(fechaStr);
                    } else {
                        return r.getFechaFormateada().equals(fechaStr) && 
                               r.getCancha().getNombre().equals(cmbCancha.getValue());
                    }
                })
                .count();
    }

    private VBox crearPanelReservasDelDia() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(25));
        panel.setPrefWidth(400);
        panel.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        actualizarPanelReservas();

        return panel;
    }

    private void actualizarPanelReservas() {
        panelReservasDelDia.getChildren().clear();

        // Título
        Label lblTitulo = new Label("📋 Reservas del día");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Label lblFecha = new Label(
            fechaSeleccionada.getDayOfMonth() + " de " + 
            fechaSeleccionada.getMonth().getDisplayName(TextStyle.FULL, new Locale("es")) + 
            " " + fechaSeleccionada.getYear()
        );
        lblFecha.setFont(Font.font("Arial", 14));
        lblFecha.setTextFill(Color.GRAY);

        Separator separador = new Separator();

        panelReservasDelDia.getChildren().addAll(lblTitulo, lblFecha, separador);

        // Obtener reservas del día
        String fechaStr = String.format("%02d/%02d/%d", 
            fechaSeleccionada.getDayOfMonth(), 
            fechaSeleccionada.getMonthValue(), 
            fechaSeleccionada.getYear());

        List<Reserva> reservasDelDia = gestor.listarReservasActivas().stream()
                .filter(r -> {
                    if (cmbCancha.getValue().equals("Todas las Canchas")) {
                        return r.getFechaFormateada().equals(fechaStr);
                    } else {
                        return r.getFechaFormateada().equals(fechaStr) && 
                               r.getCancha().getNombre().equals(cmbCancha.getValue());
                    }
                })
                .sorted((r1, r2) -> r1.getHorarioFormateado().compareTo(r2.getHorarioFormateado()))
                .collect(Collectors.toList());

        if (reservasDelDia.isEmpty()) {
            VBox sinReservas = new VBox(15);
            sinReservas.setAlignment(Pos.CENTER);
            sinReservas.setPadding(new Insets(30));

            Label lblIcono = new Label("📅");
            lblIcono.setFont(Font.font(40));

            Label lblMensaje = new Label("No hay reservas este día");
            lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            lblMensaje.setTextFill(Color.web("#7f8c8d"));

            sinReservas.getChildren().addAll(lblIcono, lblMensaje);
            panelReservasDelDia.getChildren().add(sinReservas);
        } else {
            ScrollPane scrollReservas = new ScrollPane();
            scrollReservas.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollReservas.setFitToWidth(true);
            scrollReservas.setPrefHeight(400);
            scrollReservas.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox listaReservas = new VBox(10);
            
            for (Reserva reserva : reservasDelDia) {
                VBox tarjeta = crearTarjetaReservaCompacta(reserva);
                listaReservas.getChildren().add(tarjeta);
            }

            scrollReservas.setContent(listaReservas);
            panelReservasDelDia.getChildren().add(scrollReservas);
        }
    }

    private VBox crearTarjetaReservaCompacta(Reserva reserva) {
        VBox tarjeta = new VBox(8);
        tarjeta.setPadding(new Insets(12));
        tarjeta.setStyle(
                "-fx-background-color: #f8f9fa;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #dee2e6;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 8;"
        );

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblHora = new Label("🕐 " + reserva.getHorarioFormateado());
        lblHora.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lblHora.setTextFill(Color.web("#e74c3c"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblId = new Label("#" + reserva.getIdReserva());
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblId.setTextFill(Color.web("#7f8c8d"));

        header.getChildren().addAll(lblHora, spacer, lblId);

        Label lblCancha = new Label("🏟️ " + reserva.getCancha().getNombre());
        lblCancha.setFont(Font.font("Arial", 12));
        lblCancha.setTextFill(Color.web("#2c3e50"));

        Label lblUsuario = new Label("👤 " + reserva.getUsuario().getNombre());
        lblUsuario.setFont(Font.font("Arial", 11));
        lblUsuario.setTextFill(Color.GRAY);

        Button btnVer = new Button("Ver Detalles");
        btnVer.setStyle(
                "-fx-background-color: #3498db;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 10;"
                + "-fx-padding: 5 12;"
                + "-fx-background-radius: 5;"
                + "-fx-cursor: hand;"
        );
        btnVer.setOnAction(e -> mostrarDetallesReserva(reserva));

        tarjeta.getChildren().addAll(header, lblCancha, lblUsuario, btnVer);

        return tarjeta;
    }

    private void mostrarDetallesReserva(Reserva reserva) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de Reserva");
        alert.setHeaderText("Reserva #" + reserva.getIdReserva());

        String detalles = String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n"
                + "🏟️  CANCHA\n"
                + "   %s\n\n"
                + "📅  FECHA Y HORARIO\n"
                + "   Fecha: %s\n"
                + "   Horario: %s\n\n"
                + "👤  USUARIO\n"
                + "   Nombre: %s\n"
                + "   Correo: %s\n\n"
                + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                reserva.getCancha().getNombre(),
                reserva.getFechaFormateada(),
                reserva.getHorarioFormateado(),
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getCorreo()
        );

        alert.setContentText(detalles);
        alert.showAndWait();
    }

    private void cambiarMes(int cambio) {
        mesActual = mesActual.plusMonths(cambio);
        actualizarCalendario();
        actualizarPanelReservas();
    }

    private void irAHoy() {
        mesActual = YearMonth.now();
        fechaSeleccionada = LocalDate.now();
        actualizarCalendario();
        actualizarPanelReservas();
    }

    private String obtenerNombreMes() {
        return mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es")) + 
               " " + mesActual.getYear();
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}