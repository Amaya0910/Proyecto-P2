package biblos_reserva;

import biblos_reserva_datos.*;
import biblos_reserva_dominio.*;
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
import java.util.Map;
import java.util.stream.Collectors;

public class HistorialAdminView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;
    private final MenuAdminView menuAnterior;
    private VBox contenedorHistorial;
    private ComboBox<String> cmbFiltro;
    private ComboBox<String> cmbOrdenar;
    private TextField txtBuscar;

    public HistorialAdminView(Stage stage, GestorCanchas gestor, Administracion admin, MenuAdminView menuAnterior) {
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
        header.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);");
        header.setPadding(new Insets(20, 30, 20, 30));

        HBox headerContent = new HBox(20);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);

        Label lblTitulo = new Label("📊 Historial Completo del Sistema");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);

        Label lblSubtitulo = new Label("Vista administrativa - Todas las reservas");
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

        HBox panelFiltros = crearPanelFiltros();

        contenedorHistorial = new VBox(15);
        contenedorHistorial.setAlignment(Pos.TOP_CENTER);
        contenedorHistorial.setMaxWidth(950);

        cargarHistorial();

        container.getChildren().addAll(panelFiltros, contenedorHistorial);
        return container;
    }

    private HBox crearPanelFiltros() {
        HBox panel = new HBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(25));
        panel.setMaxWidth(900);
        panel.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        // Búsqueda
        VBox buscarBox = new VBox(5);
        Label lblBuscar = new Label("🔍 Buscar");
        lblBuscar.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblBuscar.setTextFill(Color.web("#2c3e50"));

        txtBuscar = new TextField();
        txtBuscar.setPromptText("Usuario, cancha o ID...");
        txtBuscar.setPrefWidth(200);
        txtBuscar.setPrefHeight(38);
        txtBuscar.setStyle("-fx-font-size: 13; -fx-background-radius: 8;");
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> cargarHistorial());

        buscarBox.getChildren().addAll(lblBuscar, txtBuscar);

        // Filtro por estado
        VBox filtroBox = new VBox(5);
        Label lblFiltro = new Label("📋 Estado");
        lblFiltro.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblFiltro.setTextFill(Color.web("#2c3e50"));

        cmbFiltro = new ComboBox<>();
        cmbFiltro.setPrefWidth(160);
        cmbFiltro.setPrefHeight(38);
        cmbFiltro.getItems().addAll("Todas", "Por Usuario", "Por Cancha");
        cmbFiltro.setValue("Todas");
        cmbFiltro.setStyle("-fx-font-size: 13;");
        cmbFiltro.setOnAction(e -> cargarHistorial());

        filtroBox.getChildren().addAll(lblFiltro, cmbFiltro);

        // Ordenar
        VBox ordenarBox = new VBox(5);
        Label lblOrdenar = new Label("⬇️ Ordenar");
        lblOrdenar.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblOrdenar.setTextFill(Color.web("#2c3e50"));

        cmbOrdenar = new ComboBox<>();
        cmbOrdenar.setPrefWidth(160);
        cmbOrdenar.setPrefHeight(38);
        cmbOrdenar.getItems().addAll("Más Recientes", "Más Antiguas", "Por Usuario", "Por Cancha");
        cmbOrdenar.setValue("Más Recientes");
        cmbOrdenar.setStyle("-fx-font-size: 13;");
        cmbOrdenar.setOnAction(e -> cargarHistorial());

        ordenarBox.getChildren().addAll(lblOrdenar, cmbOrdenar);

        // Botón actualizar
        Button btnActualizar = new Button("🔄");
        btnActualizar.setPrefSize(38, 38);
        btnActualizar.setStyle(
                "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 16;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnActualizar.setOnAction(e -> cargarHistorial());

        VBox btnBox = new VBox(5);
        Label lblEspacio = new Label(" ");
        lblEspacio.setFont(Font.font("Arial", 12));
        btnBox.getChildren().addAll(lblEspacio, btnActualizar);

        panel.getChildren().addAll(buscarBox, filtroBox, ordenarBox, btnBox);
        return panel;
    }

    private void cargarHistorial() {
        contenedorHistorial.getChildren().clear();

        try {
            List<Reserva> todasReservas = gestor.listarReservasActivas();

            if (todasReservas == null || todasReservas.isEmpty()) {
                mostrarSinHistorial();
                return;
            }

            // Aplicar búsqueda
            String busqueda = txtBuscar.getText().toLowerCase().trim();
            if (!busqueda.isEmpty()) {
                todasReservas = todasReservas.stream()
                        .filter(r
                                -> r.getUsuario().getNombre().toLowerCase().contains(busqueda)
                        || r.getCancha().getNombre().toLowerCase().contains(busqueda)
                        || String.valueOf(r.getIdReserva()).contains(busqueda)
                        || r.getUsuario().getCorreo().toLowerCase().contains(busqueda)
                        )
                        .collect(Collectors.toList());
            }

            // Aplicar ordenamiento
            String orden = cmbOrdenar.getValue();
            if (orden.equals("Más Antiguas")) {
                todasReservas.sort((r1, r2) -> Integer.compare(r1.getIdReserva(), r2.getIdReserva()));
            } else if (orden.equals("Por Usuario")) {
                todasReservas.sort((r1, r2) -> r1.getUsuario().getNombre().compareTo(r2.getUsuario().getNombre()));
            } else if (orden.equals("Por Cancha")) {
                todasReservas.sort((r1, r2) -> r1.getCancha().getNombre().compareTo(r2.getCancha().getNombre()));
            } else {
                // Más Recientes (por defecto)
                todasReservas.sort((r1, r2) -> Integer.compare(r2.getIdReserva(), r1.getIdReserva()));
            }

            if (todasReservas.isEmpty()) {
                mostrarSinResultados();
                return;
            }

            // Estadísticas generales
            HBox estadisticas = crearEstadisticas(todasReservas);
            contenedorHistorial.getChildren().add(estadisticas);

            // Título con cantidad
            Label lblTotal = new Label("📋 " + todasReservas.size() + " reserva(s) encontrada(s)");
            lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            lblTotal.setTextFill(Color.web("#2c3e50"));
            lblTotal.setPadding(new Insets(20, 0, 10, 0));
            contenedorHistorial.getChildren().add(lblTotal);

            // Mostrar según filtro
            String filtro = cmbFiltro.getValue();
            if (filtro.equals("Por Usuario")) {
                mostrarAgrupadasPorUsuario(todasReservas);
            } else if (filtro.equals("Por Cancha")) {
                mostrarAgrupadasPorCancha(todasReservas);
            } else {
                // Mostrar todas individualmente
                for (Reserva reserva : todasReservas) {
                    VBox tarjeta = crearTarjetaHistorial(reserva);
                    contenedorHistorial.getChildren().add(tarjeta);
                }
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
        long usuariosUnicos = reservas.stream()
                .map(r -> r.getUsuario().getId())
                .distinct()
                .count();
        long canchasUnicas = reservas.stream()
                .map(r -> r.getCancha().getId())
                .distinct()
                .count();

        stats.getChildren().addAll(
                crearTarjetaEstadistica("📊", String.valueOf(total), "Total Reservas", "#e74c3c"),
                crearTarjetaEstadistica("👥", String.valueOf(usuariosUnicos), "Usuarios", "#3498db"),
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
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
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
        tarjeta.setMaxWidth(900);
        tarjeta.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );

        // Encabezado
        HBox encabezado = new HBox(15);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(3);
        Label lblId = new Label("Reserva #" + reserva.getIdReserva());
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblId.setTextFill(Color.web("#2c3e50"));

        Label lblCancha = new Label("🏟️ " + reserva.getCancha().getNombre());
        lblCancha.setFont(Font.font("Arial", 14));
        lblCancha.setTextFill(Color.GRAY);

        infoBox.getChildren().addAll(lblId, lblCancha);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblBadge = new Label("✅ Activa");
        lblBadge.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblBadge.setPadding(new Insets(4, 12, 4, 12));
        lblBadge.setStyle(
                "-fx-background-color: #d4edda;"
                + "-fx-text-fill: #155724;"
                + "-fx-background-radius: 12;"
        );

        encabezado.getChildren().addAll(infoBox, spacer, lblBadge);

        // Información de la reserva
        HBox infoReserva = new HBox(30);
        infoReserva.setPadding(new Insets(10, 0, 0, 0));

        VBox usuarioBox = new VBox(3);
        Label lblUsuarioTitulo = new Label("👤 USUARIO");
        lblUsuarioTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblUsuarioTitulo.setTextFill(Color.GRAY);

        Label lblUsuarioValor = new Label(reserva.getUsuario().getNombre());
        lblUsuarioValor.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblUsuarioValor.setTextFill(Color.web("#2c3e50"));

        usuarioBox.getChildren().addAll(lblUsuarioTitulo, lblUsuarioValor);

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

        infoReserva.getChildren().addAll(usuarioBox, fechaBox, horarioBox);

        // Botones de acción
        HBox acciones = new HBox(15);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        acciones.setPadding(new Insets(10, 0, 0, 0));

        Button btnDetalles = new Button("📋 Ver Detalles");
        btnDetalles.setStyle(
                "-fx-background-color: #3498db;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 12;"
                + "-fx-padding: 8 16;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnDetalles.setOnAction(e -> mostrarDetallesCompletos(reserva));

        Button btnEliminar = new Button("🗑️ Eliminar");
        btnEliminar.setStyle(
                "-fx-background-color: #e74c3c;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 12;"
                + "-fx-padding: 8 16;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnEliminar.setOnAction(e -> eliminarReserva(reserva));

        acciones.getChildren().addAll(btnDetalles, btnEliminar);

        tarjeta.getChildren().addAll(encabezado, infoReserva, acciones);

        return tarjeta;
    }

    private void mostrarAgrupadasPorUsuario(List<Reserva> reservas) {
        Map<Integer, List<Reserva>> porUsuario = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getUsuario().getId()));

        for (Map.Entry<Integer, List<Reserva>> entrada : porUsuario.entrySet()) {
            List<Reserva> reservasUsuario = entrada.getValue();
            if (!reservasUsuario.isEmpty()) {
                VBox grupoUsuario = crearGrupoUsuario(reservasUsuario);
                contenedorHistorial.getChildren().add(grupoUsuario);
            }
        }
    }

    private VBox crearGrupoUsuario(List<Reserva> reservas) {
        VBox grupo = new VBox(10);
        grupo.setPadding(new Insets(15));
        grupo.setMaxWidth(900);
        grupo.setStyle(
                "-fx-background-color: #f8f9fa;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #dee2e6;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 10;"
        );

        String nombreUsuario = reservas.get(0).getUsuario().getNombre();
        String correoUsuario = reservas.get(0).getUsuario().getCorreo();

        Label lblUsuario = new Label("👤 " + nombreUsuario + " (" + correoUsuario + ")");
        lblUsuario.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblUsuario.setTextFill(Color.web("#2c3e50"));

        Label lblCantidad = new Label(reservas.size() + " reserva(s)");
        lblCantidad.setFont(Font.font("Arial", 13));
        lblCantidad.setTextFill(Color.GRAY);

        grupo.getChildren().addAll(lblUsuario, lblCantidad);

        for (Reserva reserva : reservas) {
            VBox tarjeta = crearTarjetaHistorial(reserva);
            tarjeta.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-background-radius: 8;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);"
            );
            grupo.getChildren().add(tarjeta);
        }

        return grupo;
    }

    private void mostrarAgrupadasPorCancha(List<Reserva> reservas) {
        Map<Integer, List<Reserva>> porCancha = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getCancha().getId()));

        for (Map.Entry<Integer, List<Reserva>> entrada : porCancha.entrySet()) {
            List<Reserva> reservasCancha = entrada.getValue();
            if (!reservasCancha.isEmpty()) {
                VBox grupoCancha = crearGrupoCancha(reservasCancha);
                contenedorHistorial.getChildren().add(grupoCancha);
            }
        }
    }

    private VBox crearGrupoCancha(List<Reserva> reservas) {
        VBox grupo = new VBox(10);
        grupo.setPadding(new Insets(15));
        grupo.setMaxWidth(900);
        grupo.setStyle(
                "-fx-background-color: #f8f9fa;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #dee2e6;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 10;"
        );

        String nombreCancha = reservas.get(0).getCancha().getNombre();

        Label lblCancha = new Label("🏟️ " + nombreCancha);
        lblCancha.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblCancha.setTextFill(Color.web("#2c3e50"));

        Label lblCantidad = new Label(reservas.size() + " reserva(s)");
        lblCantidad.setFont(Font.font("Arial", 13));
        lblCantidad.setTextFill(Color.GRAY);

        grupo.getChildren().addAll(lblCancha, lblCantidad);

        for (Reserva reserva : reservas) {
            VBox tarjeta = crearTarjetaHistorial(reserva);
            tarjeta.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-background-radius: 8;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);"
            );
            grupo.getChildren().add(tarjeta);
        }

        return grupo;
    }

    private void mostrarSinHistorial() {
        VBox sinHistorial = new VBox(20);
        sinHistorial.setAlignment(Pos.CENTER);
        sinHistorial.setPadding(new Insets(60));

        Label lblIcono = new Label("📊");
        lblIcono.setFont(Font.font(70));

        Label lblMensaje = new Label("No hay reservas en el sistema");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));

        Label lblSugerencia = new Label("Las reservas aparecerán aquí cuando los usuarios las realicen");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);

        sinHistorial.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia);
        contenedorHistorial.getChildren().add(sinHistorial);
    }

    private void mostrarSinResultados() {
        VBox sinResultados = new VBox(20);
        sinResultados.setAlignment(Pos.CENTER);
        sinResultados.setPadding(new Insets(60));

        Label lblIcono = new Label("🔍");
        lblIcono.setFont(Font.font(70));

        Label lblMensaje = new Label("No se encontraron resultados");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));

        Label lblSugerencia = new Label("Intenta con otros criterios de búsqueda");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);

        sinResultados.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia);
        contenedorHistorial.getChildren().add(sinResultados);
    }

    private void mostrarError(String mensaje) {
        VBox errorBox = new VBox(15);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(40));
        errorBox.setMaxWidth(800);
        errorBox.setStyle(
                "-fx-background-color: #fee;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #e74c3c;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 10;"
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

    private void mostrarDetallesCompletos(Reserva reserva) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles Completos");
        alert.setHeaderText("Información de Reserva #" + reserva.getIdReserva());

        String detalles = String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n"
                + "🏟️  INFORMACIÓN DE CANCHA\n"
                + "   Nombre: %s\n"
                + "   ID Cancha: #%d\n"
                + "   Estado: %s\n\n"
                + "📅  DETALLES DE RESERVA\n"
                + "   ID Reserva: #%d\n"
                + "   Fecha: %s\n"
                + "   Horario: %s\n\n"
                + "👤  USUARIO RESPONSABLE\n"
                + "   Nombre: %s\n"
                + "   Correo: %s\n"
                + "   ID Usuario: #%d\n\n"
                + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                reserva.getCancha().getNombre(),
                reserva.getCancha().getId(),
                reserva.getCancha().getEstado(),
                reserva.getIdReserva(),
                reserva.getFechaFormateada(),
                reserva.getHorarioFormateado(),
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getCorreo(),
                reserva.getUsuario().getId()
        );

        alert.setContentText(detalles);
        alert.showAndWait();
    }

    private void eliminarReserva(Reserva reserva) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar Reserva #" + reserva.getIdReserva() + "?");
        confirmacion.setContentText(
                "Usuario: " + reserva.getUsuario().getNombre() + "\n"
                + "Cancha: " + reserva.getCancha().getNombre() + "\n"
                + "Fecha: " + reserva.getFechaFormateada() + "\n\n"
                + "Esta acción no se puede deshacer."
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    boolean eliminada = gestor.cancelarReserva(reserva.getIdReserva());

                    if (eliminada) {
                        Alert exito = new Alert(Alert.AlertType.INFORMATION);
                        exito.setTitle("Éxito");
                        exito.setHeaderText("Reserva Eliminada");
                        exito.setContentText("La reserva ha sido eliminada correctamente del sistema.");
                        exito.showAndWait();

                        cargarHistorial();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("No se pudo eliminar");
                        error.setContentText("La reserva no pudo ser eliminada. Intenta nuevamente.");
                        error.showAndWait();
                    }
                } catch (Exception e) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Error al eliminar");
                    error.setContentText("Ocurrió un error: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}

