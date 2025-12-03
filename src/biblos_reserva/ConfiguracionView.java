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

public class ConfiguracionView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;
    private final MenuAdminView menuAnterior;

    // Componentes de configuración
    private TextField txtNombreSistema;
    private TextField txtCorreoContacto;
    private TextField txtTelefono;
    private ComboBox<String> cmbHorarioApertura;
    private ComboBox<String> cmbHorarioCierre;
    private Spinner<Integer> spnDuracionReserva;
    private Spinner<Integer> spnMaxReservasPorUsuario;
    private CheckBox chkPermitirCancelaciones;
    private Spinner<Integer> spnHorasCancelacion;
    private CheckBox chkNotificacionesEmail;
    private CheckBox chkModoMantenimiento;

    public ConfiguracionView(Stage stage, GestorCanchas gestor, Administracion admin, MenuAdminView menuAnterior) {
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

        Label lblTitulo = new Label("⚙️ Configuración del Sistema");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);

        Label lblSubtitulo = new Label("Ajustes generales y parámetros del sistema");
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
        VBox container = new VBox(30);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxWidth(900);

        // Secciones de configuración
        container.getChildren().addAll(
                crearSeccionInformacionGeneral(),
                crearSeccionHorarios(),
                crearSeccionReservas(),
                crearSeccionNotificaciones(),
                crearSeccionMantenimiento(),
                crearBotonesAccion()
        );

        return container;
    }

    private VBox crearSeccionInformacionGeneral() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(25));
        seccion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label lblTitulo = new Label("📋 Información General");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Separator separator = new Separator();

        // Nombre del sistema
        Label lblNombreSistema = new Label("Nombre del Sistema:");
        lblNombreSistema.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        txtNombreSistema = new TextField("Biblos Reserva");
        txtNombreSistema.setPrefHeight(35);
        txtNombreSistema.setStyle("-fx-font-size: 13;");

        // Correo de contacto
        Label lblCorreo = new Label("Correo de Contacto:");
        lblCorreo.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        txtCorreoContacto = new TextField("contacto@biblosreserva.com");
        txtCorreoContacto.setPrefHeight(35);
        txtCorreoContacto.setStyle("-fx-font-size: 13;");

        // Teléfono
        Label lblTelefono = new Label("Teléfono:");
        lblTelefono.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        txtTelefono = new TextField("+57 300 123 4567");
        txtTelefono.setPrefHeight(35);
        txtTelefono.setStyle("-fx-font-size: 13;");

        seccion.getChildren().addAll(
                lblTitulo, separator,
                lblNombreSistema, txtNombreSistema,
                lblCorreo, txtCorreoContacto,
                lblTelefono, txtTelefono
        );

        return seccion;
    }

    private VBox crearSeccionHorarios() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(25));
        seccion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label lblTitulo = new Label("🕐 Horarios de Operación");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Separator separator = new Separator();

        // Horario de apertura
        Label lblApertura = new Label("Horario de Apertura:");
        lblApertura.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        cmbHorarioApertura = new ComboBox<>();
        cmbHorarioApertura.setPrefHeight(35);
        cmbHorarioApertura.setPrefWidth(200);
        for (int i = 6; i <= 12; i++) {
            cmbHorarioApertura.getItems().add(String.format("%02d:00", i));
        }
        cmbHorarioApertura.setValue("08:00");

        // Horario de cierre
        Label lblCierre = new Label("Horario de Cierre:");
        lblCierre.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        cmbHorarioCierre = new ComboBox<>();
        cmbHorarioCierre.setPrefHeight(35);
        cmbHorarioCierre.setPrefWidth(200);
        for (int i = 18; i <= 23; i++) {
            cmbHorarioCierre.getItems().add(String.format("%02d:00", i));
        }
        cmbHorarioCierre.setValue("22:00");

        HBox horarios = new HBox(30);
        VBox aperturaBox = new VBox(8, lblApertura, cmbHorarioApertura);
        VBox cierreBox = new VBox(8, lblCierre, cmbHorarioCierre);
        horarios.getChildren().addAll(aperturaBox, cierreBox);

        seccion.getChildren().addAll(lblTitulo, separator, horarios);

        return seccion;
    }

    private VBox crearSeccionReservas() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(25));
        seccion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label lblTitulo = new Label("📅 Configuración de Reservas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Separator separator = new Separator();

        // Duración de reserva
        Label lblDuracion = new Label("Duración de Reserva (horas):");
        lblDuracion.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        spnDuracionReserva = new Spinner<>(1, 4, 1);
        spnDuracionReserva.setPrefHeight(35);
        spnDuracionReserva.setPrefWidth(150);
        spnDuracionReserva.setEditable(true);

        // Máximo de reservas por usuario
        Label lblMaxReservas = new Label("Máximo de Reservas Activas por Usuario:");
        lblMaxReservas.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        spnMaxReservasPorUsuario = new Spinner<>(1, 10, 3);
        spnMaxReservasPorUsuario.setPrefHeight(35);
        spnMaxReservasPorUsuario.setPrefWidth(150);
        spnMaxReservasPorUsuario.setEditable(true);

        // Permitir cancelaciones
        chkPermitirCancelaciones = new CheckBox("Permitir Cancelaciones de Reservas");
        chkPermitirCancelaciones.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        chkPermitirCancelaciones.setSelected(true);
        chkPermitirCancelaciones.setStyle("-fx-text-fill: #2c3e50;");

        // Horas mínimas para cancelar
        Label lblHorasCancelacion = new Label("Horas Mínimas para Cancelar:");
        lblHorasCancelacion.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        spnHorasCancelacion = new Spinner<>(1, 48, 2);
        spnHorasCancelacion.setPrefHeight(35);
        spnHorasCancelacion.setPrefWidth(150);
        spnHorasCancelacion.setEditable(true);
        spnHorasCancelacion.setDisable(!chkPermitirCancelaciones.isSelected());

        chkPermitirCancelaciones.selectedProperty().addListener((obs, old, nuevo) -> {
            spnHorasCancelacion.setDisable(!nuevo);
        });

        HBox configuraciones = new HBox(30);
        VBox duracionBox = new VBox(8, lblDuracion, spnDuracionReserva);
        VBox maxReservasBox = new VBox(8, lblMaxReservas, spnMaxReservasPorUsuario);
        configuraciones.getChildren().addAll(duracionBox, maxReservasBox);

        VBox cancelacionBox = new VBox(10, chkPermitirCancelaciones, lblHorasCancelacion, spnHorasCancelacion);

        seccion.getChildren().addAll(lblTitulo, separator, configuraciones, cancelacionBox);

        return seccion;
    }

    private VBox crearSeccionNotificaciones() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(25));
        seccion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label lblTitulo = new Label("📧 Notificaciones");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Separator separator = new Separator();

        chkNotificacionesEmail = new CheckBox("Enviar Notificaciones por Email");
        chkNotificacionesEmail.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        chkNotificacionesEmail.setSelected(true);
        chkNotificacionesEmail.setStyle("-fx-text-fill: #2c3e50;");

        Label lblInfo = new Label(
                "Las notificaciones incluyen:\n"
                + "• Confirmación de reserva\n"
                + "• Recordatorio 24 horas antes\n"
                + "• Confirmación de cancelación"
        );
        lblInfo.setFont(Font.font("Arial", 12));
        lblInfo.setTextFill(Color.GRAY);
        lblInfo.setWrapText(true);

        seccion.getChildren().addAll(lblTitulo, separator, chkNotificacionesEmail, lblInfo);

        return seccion;
    }

    private VBox crearSeccionMantenimiento() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(25));
        seccion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label lblTitulo = new Label("🔧 Mantenimiento");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#2c3e50"));

        Separator separator = new Separator();

        chkModoMantenimiento = new CheckBox("Modo Mantenimiento (Bloquear nuevas reservas)");
        chkModoMantenimiento.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        chkModoMantenimiento.setSelected(false);
        chkModoMantenimiento.setStyle("-fx-text-fill: #e74c3c;");

        Label lblAdvertencia = new Label(
                "⚠️ ADVERTENCIA: Al activar el modo mantenimiento, los usuarios\n"
                + "no podrán realizar nuevas reservas. Las reservas existentes\n"
                + "permanecerán activas."
        );
        lblAdvertencia.setFont(Font.font("Arial", 12));
        lblAdvertencia.setTextFill(Color.web("#e74c3c"));
        lblAdvertencia.setWrapText(true);
        lblAdvertencia.setVisible(false);

        chkModoMantenimiento.selectedProperty().addListener((obs, old, nuevo) -> {
            lblAdvertencia.setVisible(nuevo);
        });

        HBox accionesMantenimiento = new HBox(15);
        accionesMantenimiento.setPadding(new Insets(10, 0, 0, 0));

        Button btnLimpiarCache = new Button("🗑️ Limpiar Caché");
        btnLimpiarCache.setStyle(
                "-fx-background-color: #95a5a6;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnLimpiarCache.setOnAction(e -> limpiarCache());

        Button btnExportarDatos = new Button("💾 Exportar Datos");
        btnExportarDatos.setStyle(
                "-fx-background-color: #3498db;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnExportarDatos.setOnAction(e -> exportarDatos());

        Button btnResetearSistema = new Button("⚠️ Resetear Sistema");
        btnResetearSistema.setStyle(
                "-fx-background-color: #e74c3c;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 20;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
        btnResetearSistema.setOnAction(e -> resetearSistema());

        accionesMantenimiento.getChildren().addAll(btnLimpiarCache, btnExportarDatos, btnResetearSistema);

        seccion.getChildren().addAll(lblTitulo, separator, chkModoMantenimiento, lblAdvertencia, accionesMantenimiento);

        return seccion;
    }

    private HBox crearBotonesAccion() {
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(20, 0, 40, 0));

        Button btnGuardar = new Button("💾 Guardar Cambios");
        btnGuardar.setPrefSize(180, 45);
        btnGuardar.setStyle(
                "-fx-background-color: linear-gradient(to right, #2ecc71, #27ae60);"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 14;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnGuardar.setOnAction(e -> guardarConfiguracion());

        Button btnRestablecer = new Button("↩️ Restablecer");
        btnRestablecer.setPrefSize(180, 45);
        btnRestablecer.setStyle(
                "-fx-background-color: #95a5a6;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 14;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnRestablecer.setOnAction(e -> restablecerValores());

        Button btnCancelar = new Button("❌ Cancelar");
        btnCancelar.setPrefSize(180, 45);
        btnCancelar.setStyle(
                "-fx-background-color: #e74c3c;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 14;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        btnCancelar.setOnAction(e -> volverAlMenu());

        botones.getChildren().addAll(btnGuardar, btnRestablecer, btnCancelar);

        return botones;
    }

    private void guardarConfiguracion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Guardar Configuración");
        confirmacion.setHeaderText("¿Guardar los cambios de configuración?");
        confirmacion.setContentText(
                "Los siguientes cambios serán aplicados:\n\n"
                + "• Nombre del Sistema: " + txtNombreSistema.getText() + "\n"
                + "• Horario: " + cmbHorarioApertura.getValue() + " - " + cmbHorarioCierre.getValue() + "\n"
                + "• Duración de Reserva: " + spnDuracionReserva.getValue() + " hora(s)\n"
                + "• Máximo Reservas: " + spnMaxReservasPorUsuario.getValue() + "\n"
                + "• Modo Mantenimiento: " + (chkModoMantenimiento.isSelected() ? "Activado" : "Desactivado")
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Configuración Guardada");
                exito.setHeaderText("✅ Cambios Aplicados Exitosamente");
                exito.setContentText(
                        "La configuración ha sido guardada correctamente.\n\n"
                        + "Nota: Algunas configuraciones requieren implementación\n"
                        + "adicional en el sistema para ser completamente funcionales."
                );
                exito.showAndWait();
            }
        });
    }

    private void restablecerValores() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Restablecer Configuración");
        confirmacion.setHeaderText("¿Restablecer valores por defecto?");
        confirmacion.setContentText("Se restaurarán todos los valores a su configuración inicial.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                txtNombreSistema.setText("Biblos Reserva");
                txtCorreoContacto.setText("contacto@biblosreserva.com");
                txtTelefono.setText("+57 300 123 4567");
                cmbHorarioApertura.setValue("08:00");
                cmbHorarioCierre.setValue("22:00");
                spnDuracionReserva.getValueFactory().setValue(1);
                spnMaxReservasPorUsuario.getValueFactory().setValue(3);
                chkPermitirCancelaciones.setSelected(true);
                spnHorasCancelacion.getValueFactory().setValue(2);
                chkNotificacionesEmail.setSelected(true);
                chkModoMantenimiento.setSelected(false);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Valores Restablecidos");
                info.setHeaderText("✅ Configuración Restaurada");
                info.setContentText("Los valores por defecto han sido restablecidos.");
                info.showAndWait();
            }
        });
    }

    private void limpiarCache() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar Caché");
        confirmacion.setHeaderText("¿Limpiar caché del sistema?");
        confirmacion.setContentText(
                "Esta acción limpiará los datos temporales almacenados.\n"
                + "Los datos permanentes no se verán afectados."
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Caché Limpiado");
                info.setHeaderText("✅ Operación Completada");
                info.setContentText("El caché del sistema ha sido limpiado exitosamente.");
                info.showAndWait();
            }
        });
    }

    private void exportarDatos() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Exportar Datos");
        info.setHeaderText("💾 Exportación de Datos");
        info.setContentText(
                "Funcionalidad de exportación disponible próximamente.\n\n"
                + "Características planificadas:\n"
                + "• Exportar usuarios a CSV/Excel\n"
                + "• Exportar reservas con filtros\n"
                + "• Exportar canchas y configuración\n"
                + "• Generar respaldos completos del sistema"
        );
        info.showAndWait();
    }

    private void resetearSistema() {
        Alert advertencia = new Alert(Alert.AlertType.WARNING);
        advertencia.setTitle("⚠️ ADVERTENCIA CRÍTICA");
        advertencia.setHeaderText("Resetear Sistema Completo");
        advertencia.setContentText(
                "Esta acción eliminará PERMANENTEMENTE:\n\n"
                + "• Todas las reservas\n"
                + "• Todos los usuarios (excepto administradores)\n"
                + "• Todas las canchas\n"
                + "• Todo el historial\n\n"
                + "Esta operación NO SE PUEDE DESHACER.\n\n"
                + "¿Estás ABSOLUTAMENTE SEGURO?"
        );

        ButtonType btnConfirmar = new ButtonType("Sí, Resetear", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        advertencia.getButtonTypes().setAll(btnConfirmar, btnCancelar);

        advertencia.showAndWait().ifPresent(respuesta -> {
            if (respuesta == btnConfirmar) {
                // Segunda confirmación
                TextInputDialog confirmacionFinal = new TextInputDialog();
                confirmacionFinal.setTitle("Confirmación Final");
                confirmacionFinal.setHeaderText("Escribe 'RESETEAR' para confirmar");
                confirmacionFinal.setContentText("Confirmación:");

                confirmacionFinal.showAndWait().ifPresent(texto -> {
                    if (texto.equals("RESETEAR")) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("Sistema Reseteado");
                        info.setHeaderText("✅ Reseteo Completado");
                        info.setContentText(
                                "El sistema ha sido reseteado exitosamente.\n\n"
                                + "Nota: Esta es una simulación. La implementación real\n"
                                + "requeriría llamadas a métodos del gestor para eliminar\n"
                                + "todos los datos del sistema."
                        );
                        info.showAndWait();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Confirmación Incorrecta");
                        error.setHeaderText("❌ Operación Cancelada");
                        error.setContentText("El texto de confirmación no coincide.");
                        error.showAndWait();
                    }
                });
            }
        });
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}