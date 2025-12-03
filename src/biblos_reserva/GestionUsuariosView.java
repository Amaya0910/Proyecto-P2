package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Administracion;
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

public class GestionUsuariosView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;
    private final MenuAdminView menuAnterior;
    private VBox contenedorUsuarios;
    private TextField txtBuscar;
    private ComboBox<String> cmbFiltro;
    private ComboBox<String> cmbOrdenar;

    public GestionUsuariosView(Stage stage, GestorCanchas gestor, Administracion admin, MenuAdminView menuAnterior) {
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

        Label lblTitulo = new Label("👥 Gestión de Usuarios");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);

        Label lblSubtitulo = new Label("Administrar usuarios del sistema");
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

        HBox panelControl = crearPanelControl();

        contenedorUsuarios = new VBox(15);
        contenedorUsuarios.setAlignment(Pos.TOP_CENTER);
        contenedorUsuarios.setMaxWidth(950);

        cargarUsuarios();

        container.getChildren().addAll(panelControl, contenedorUsuarios);
        return container;
    }

    private HBox crearPanelControl() {
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
        txtBuscar.setPromptText("Nombre, correo o ID...");
        txtBuscar.setPrefWidth(250);
        txtBuscar.setPrefHeight(38);
        txtBuscar.setStyle("-fx-font-size: 13; -fx-background-radius: 8;");
        txtBuscar.textProperty().addListener((obs, old, nuevo) -> cargarUsuarios());

        buscarBox.getChildren().addAll(lblBuscar, txtBuscar);

        // Filtro
        VBox filtroBox = new VBox(5);
        Label lblFiltro = new Label("📋 Filtro");
        lblFiltro.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblFiltro.setTextFill(Color.web("#2c3e50"));

        cmbFiltro = new ComboBox<>();
        cmbFiltro.setPrefWidth(160);
        cmbFiltro.setPrefHeight(38);
        cmbFiltro.getItems().addAll("Todos", "Con Reservas", "Sin Reservas");
        cmbFiltro.setValue("Todos");
        cmbFiltro.setStyle("-fx-font-size: 13;");
        cmbFiltro.setOnAction(e -> cargarUsuarios());

        filtroBox.getChildren().addAll(lblFiltro, cmbFiltro);

        // Ordenar
        VBox ordenarBox = new VBox(5);
        Label lblOrdenar = new Label("⬇️ Ordenar");
        lblOrdenar.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lblOrdenar.setTextFill(Color.web("#2c3e50"));

        cmbOrdenar = new ComboBox<>();
        cmbOrdenar.setPrefWidth(160);
        cmbOrdenar.setPrefHeight(38);
        cmbOrdenar.getItems().addAll("Por Nombre", "Por ID", "Más Reservas");
        cmbOrdenar.setValue("Por Nombre");
        cmbOrdenar.setStyle("-fx-font-size: 13;");
        cmbOrdenar.setOnAction(e -> cargarUsuarios());

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
        btnActualizar.setOnAction(e -> cargarUsuarios());

        VBox btnBox = new VBox(5);
        Label lblEspacio = new Label(" ");
        lblEspacio.setFont(Font.font("Arial", 12));
        btnBox.getChildren().addAll(lblEspacio, btnActualizar);

        panel.getChildren().addAll(buscarBox, filtroBox, ordenarBox, btnBox);
        return panel;
    }

    private void cargarUsuarios() {
        contenedorUsuarios.getChildren().clear();

        try {
            List<Usuario> usuarios = gestor.listarUsuarios();

            if (usuarios == null || usuarios.isEmpty()) {
                mostrarSinUsuarios();
                return;
            }

            // Aplicar búsqueda
            String busqueda = txtBuscar.getText().toLowerCase().trim();
            if (!busqueda.isEmpty()) {
                usuarios = usuarios.stream()
                        .filter(u
                                -> u.getNombre().toLowerCase().contains(busqueda)
                        || u.getCorreo().toLowerCase().contains(busqueda)
                        || String.valueOf(u.getId()).contains(busqueda)
                        )
                        .collect(Collectors.toList());
            }

            // Aplicar filtro
            String filtro = cmbFiltro.getValue();
            if (filtro.equals("Con Reservas")) {
                usuarios = usuarios.stream()
                        .filter(u -> gestor.listarReservasActivas().stream()
                        .anyMatch(r -> r.getUsuario().getId() == u.getId()))
                        .collect(Collectors.toList());
            } else if (filtro.equals("Sin Reservas")) {
                usuarios = usuarios.stream()
                        .filter(u -> gestor.listarReservasActivas().stream()
                        .noneMatch(r -> r.getUsuario().getId() == u.getId()))
                        .collect(Collectors.toList());
            }

            // Aplicar ordenamiento
            String orden = cmbOrdenar.getValue();
            if (orden.equals("Por ID")) {
                usuarios.sort((u1, u2) -> Integer.compare(u1.getId(), u2.getId()));
            } else if (orden.equals("Más Reservas")) {
                usuarios.sort((u1, u2) -> {
                    long reservasU1 = gestor.listarReservasActivas().stream()
                            .filter(r -> r.getUsuario().getId() == u1.getId()).count();
                    long reservasU2 = gestor.listarReservasActivas().stream()
                            .filter(r -> r.getUsuario().getId() == u2.getId()).count();
                    return Long.compare(reservasU2, reservasU1);
                });
            } else {
                // Por Nombre
                usuarios.sort((u1, u2) -> u1.getNombre().compareToIgnoreCase(u2.getNombre()));
            }

            if (usuarios.isEmpty()) {
                mostrarSinResultados();
                return;
            }

            // Estadísticas
            HBox estadisticas = crearEstadisticas(usuarios);
            contenedorUsuarios.getChildren().add(estadisticas);

            // Título con cantidad
            Label lblTotal = new Label("👥 " + usuarios.size() + " usuario(s) encontrado(s)");
            lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            lblTotal.setTextFill(Color.web("#2c3e50"));
            lblTotal.setPadding(new Insets(20, 0, 10, 0));
            contenedorUsuarios.getChildren().add(lblTotal);

            // Mostrar usuarios
            for (Usuario usuario : usuarios) {
                VBox tarjeta = crearTarjetaUsuario(usuario);
                contenedorUsuarios.getChildren().add(tarjeta);
            }

        } catch (Exception e) {
            mostrarError("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private HBox crearEstadisticas(List<Usuario> usuarios) {
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(20));

        int total = usuarios.size();
        long conReservas = usuarios.stream()
                .filter(u -> gestor.listarReservasActivas().stream()
                .anyMatch(r -> r.getUsuario().getId() == u.getId()))
                .count();
        long sinReservas = total - conReservas;

        stats.getChildren().addAll(
                crearTarjetaEstadistica("👥", String.valueOf(total), "Total Usuarios", "#3498db"),
                crearTarjetaEstadistica("✅", String.valueOf(conReservas), "Con Reservas", "#2ecc71"),
                crearTarjetaEstadistica("⭕", String.valueOf(sinReservas), "Sin Reservas", "#95a5a6")
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

    private VBox crearTarjetaUsuario(Usuario usuario) {
        VBox tarjeta = new VBox(15);
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
        Label lblNombre = new Label("👤 " + usuario.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblNombre.setTextFill(Color.web("#2c3e50"));

        Label lblCorreo = new Label("📧 " + usuario.getCorreo());
        lblCorreo.setFont(Font.font("Arial", 14));
        lblCorreo.setTextFill(Color.GRAY);

        infoBox.getChildren().addAll(lblNombre, lblCorreo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Badge de ID
        Label lblId = new Label("ID: " + usuario.getId());
        lblId.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblId.setPadding(new Insets(5, 12, 5, 12));
        lblId.setStyle(
                "-fx-background-color: #e3f2fd;"
                + "-fx-text-fill: #1976d2;"
                + "-fx-background-radius: 12;"
        );

        encabezado.getChildren().addAll(infoBox, spacer, lblId);

        // Información adicional
        HBox infoExtra = new HBox(40);
        infoExtra.setPadding(new Insets(10, 0, 0, 0));

        // Contar reservas del usuario
        long cantidadReservas = gestor.listarReservasActivas().stream()
                .filter(r -> r.getUsuario().getId() == usuario.getId())
                .count();

        VBox reservasBox = new VBox(3);
        Label lblReservasTitulo = new Label("📋 RESERVAS ACTIVAS");
        lblReservasTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblReservasTitulo.setTextFill(Color.GRAY);

        Label lblReservasValor = new Label(String.valueOf(cantidadReservas));
        lblReservasValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblReservasValor.setTextFill(cantidadReservas > 0 ? Color.web("#2ecc71") : Color.web("#95a5a6"));

        reservasBox.getChildren().addAll(lblReservasTitulo, lblReservasValor);

        VBox tipoBox = new VBox(3);
        Label lblTipoTitulo = new Label("🔖 TIPO");
        lblTipoTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lblTipoTitulo.setTextFill(Color.GRAY);

        Label lblTipoValor = new Label("Usuario");
        lblTipoValor.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblTipoValor.setTextFill(Color.web("#3498db"));

        tipoBox.getChildren().addAll(lblTipoTitulo, lblTipoValor);

        infoExtra.getChildren().addAll(reservasBox, tipoBox);

        // Botones de acción
        HBox acciones = new HBox(15);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        acciones.setPadding(new Insets(10, 0, 0, 0));

        Button btnVerReservas = new Button("📋 Ver Reservas");
        btnVerReservas.setStyle(
                "-fx-background-color: #3498db;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 12;"
                + "-fx-padding: 8 16;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnVerReservas.setOnAction(e -> verReservasUsuario(usuario));
        btnVerReservas.setDisable(cantidadReservas == 0);

        Button btnSuspender = new Button("⏸️ Suspender");
        btnSuspender.setStyle(
                "-fx-background-color: #f39c12;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 12;"
                + "-fx-padding: 8 16;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnSuspender.setOnAction(e -> suspenderUsuario(usuario));

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
        btnEliminar.setOnAction(e -> eliminarUsuario(usuario));

        acciones.getChildren().addAll(btnVerReservas, btnSuspender, btnEliminar);

        tarjeta.getChildren().addAll(encabezado, infoExtra, acciones);

        return tarjeta;
    }

    private void verReservasUsuario(Usuario usuario) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reservas del Usuario");
        alert.setHeaderText("Reservas de " + usuario.getNombre());

        StringBuilder reservas = new StringBuilder();
        gestor.listarReservasActivas().stream()
                .filter(r -> r.getUsuario().getId() == usuario.getId())
                .forEach(r -> reservas.append(String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━\n"
                + "Reserva #%d\n"
                + "🏟️ Cancha: %s\n"
                + "📅 Fecha: %s\n"
                + "🕐 Horario: %s\n\n",
                r.getIdReserva(),
                r.getCancha().getNombre(),
                r.getFechaFormateada(),
                r.getHorarioFormateado()
        )));

        if (reservas.length() == 0) {
            reservas.append("Este usuario no tiene reservas activas.");
        }

        alert.setContentText(reservas.toString());
        alert.showAndWait();
    }

    private void suspenderUsuario(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("Suspender Usuario");
        confirmacion.setHeaderText("¿Suspender a " + usuario.getNombre() + "?");
        confirmacion.setContentText(
                "Esta acción impedirá que el usuario realice nuevas reservas.\n"
                + "Las reservas existentes permanecerán activas.\n\n"
                + "Nota: Esta funcionalidad requiere implementación adicional\n"
                + "en el sistema de gestión de estados de usuario."
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Funcionalidad en Desarrollo");
                info.setHeaderText("Sistema de Suspensión");
                info.setContentText(
                        "La suspensión de usuarios está planificada para futuras versiones.\n\n"
                        + "Características pendientes:\n"
                        + "• Estado de usuario (Activo/Suspendido)\n"
                        + "• Historial de suspensiones\n"
                        + "• Notificaciones al usuario\n"
                        + "• Fecha de finalización de suspensión"
                );
                info.showAndWait();
            }
        });
    }

    private void eliminarUsuario(Usuario usuario) {
        long cantidadReservas = gestor.listarReservasActivas().stream()
                .filter(r -> r.getUsuario().getId() == usuario.getId())
                .count();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar a " + usuario.getNombre() + "?");

        String mensaje = "Usuario: " + usuario.getNombre() + "\n"
                + "Correo: " + usuario.getCorreo() + "\n"
                + "ID: " + usuario.getId() + "\n"
                + "Reservas activas: " + cantidadReservas + "\n\n";

        if (cantidadReservas > 0) {
            mensaje += "⚠️ ADVERTENCIA: Este usuario tiene " + cantidadReservas + " reserva(s) activa(s).\n"
                    + "Al eliminar el usuario, todas sus reservas también serán eliminadas.\n\n";
        }

        mensaje += "Esta acción NO se puede deshacer.";
        confirmacion.setContentText(mensaje);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    // Primero eliminar todas las reservas del usuario
                    List<Integer> idsReservas = gestor.listarReservasActivas().stream()
                            .filter(r -> r.getUsuario().getId() == usuario.getId())
                            .map(r -> r.getIdReserva())
                            .collect(Collectors.toList());

                    for (Integer idReserva : idsReservas) {
                        gestor.cancelarReserva(idReserva);
                    }

                    // Luego eliminar el usuario
                    boolean eliminado = gestor.eliminarUsuario(usuario.getId());

                    if (eliminado) {
                        Alert exito = new Alert(Alert.AlertType.INFORMATION);
                        exito.setTitle("Éxito");
                        exito.setHeaderText("Usuario Eliminado");
                        exito.setContentText(
                                "El usuario " + usuario.getNombre() + " ha sido eliminado correctamente.\n"
                                + (cantidadReservas > 0 ? "Se eliminaron " + cantidadReservas + " reserva(s)." : "")
                        );
                        exito.showAndWait();

                        cargarUsuarios();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("No se pudo eliminar");
                        error.setContentText("El usuario no pudo ser eliminado. Intenta nuevamente.");
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

    private void mostrarSinUsuarios() {
        VBox sinUsuarios = new VBox(20);
        sinUsuarios.setAlignment(Pos.CENTER);
        sinUsuarios.setPadding(new Insets(60));

        Label lblIcono = new Label("👥");
        lblIcono.setFont(Font.font(70));

        Label lblMensaje = new Label("No hay usuarios en el sistema");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));

        Label lblSugerencia = new Label("Los usuarios aparecerán aquí cuando se registren");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);

        sinUsuarios.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia);
        contenedorUsuarios.getChildren().add(sinUsuarios);
    }

    private void mostrarSinResultados() {
        VBox sinResultados = new VBox(20);
        sinResultados.setAlignment(Pos.CENTER);
        sinResultados.setPadding(new Insets(60));

        Label lblIcono = new Label("🔍");
        lblIcono.setFont(Font.font(70));

        Label lblMensaje = new Label("No se encontraron usuarios");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));

        Label lblSugerencia = new Label("Intenta con otros criterios de búsqueda o filtrado");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);

        sinResultados.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia);
        contenedorUsuarios.getChildren().add(sinResultados);
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
        contenedorUsuarios.getChildren().add(errorBox);
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}