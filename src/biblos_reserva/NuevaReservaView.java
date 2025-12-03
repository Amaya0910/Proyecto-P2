package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
import biblos_reserva_dominio.Cancha;
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
import java.time.LocalTime;
import java.util.List;

public class NuevaReservaView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private final Usuario usuario;
    private final MenuUsuarioView menuAnterior;

    private ComboBox<Cancha> cmbCancha;
    private DatePicker dpFecha;
    private ComboBox<LocalTime> cmbHoraInicio;
    private ComboBox<LocalTime> cmbHoraFin;
    private Label lblDisponibilidad;
    private Label lblMensaje;
    private Button btnVerificar;
    private Button btnReservar;
    private VBox panelHorarios;

    public NuevaReservaView(Stage stage, GestorCanchas gestor, Usuario usuario, MenuUsuarioView menuAnterior) {
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
        scroll.setStyle("-fx-background: #f5f7fa;");

        VBox contenido = crearContenido();
        scroll.setContent(contenido);

        root.setCenter(scroll);

        return new Scene(root, 1000, 700);
    }

    private VBox crearHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, green, green);");
        header.setPadding(new Insets(20));

        HBox content = new HBox(20);
        content.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label("➕ Nueva Reserva");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lbl.setTextFill(Color.WHITE);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Button btnVolver = new Button("← Volver");
        btnVolver.setStyle(
                "-fx-background-color: rgba(255,255,255,0.3);" +
                        "-fx-text-fill: white; -fx-font-weight: bold;"
        );
        btnVolver.setOnAction(e -> volverAlMenu());

        content.getChildren().addAll(lbl, space, btnVolver);
        header.getChildren().add(content);

        return header;
    }

    private VBox crearContenido() {
        VBox container = new VBox(25);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Reservar una Cancha");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web("#2c3e50"));

        VBox form = crearFormulario();

        container.getChildren().addAll(titulo, form);
        return container;
    }

    private VBox crearFormulario() {
        VBox form = new VBox(20);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color:white;-fx-background-radius:15;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),10,0,0,2);");

        // Campo cancha
        VBox campoCancha = new VBox(8);
        Label lblC = new Label("🏟️ Seleccione la Cancha");
        lblC.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        cmbCancha = new ComboBox<>();
        cmbCancha.setPrefWidth(600);
        cmbCancha.getItems().addAll(gestor.mostrarCanchasDisponibles());

        campoCancha.getChildren().addAll(lblC, cmbCancha);

        // Fecha
        VBox campoFecha = new VBox(8);
        Label lblF = new Label("📅 Fecha");
        dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setPrefWidth(600);
        campoFecha.getChildren().addAll(lblF, dpFecha);

        // Hora inicio
        VBox campoInicio = new VBox(8);
        Label lblI = new Label("🕐 Hora Inicio");
        cmbHoraInicio = new ComboBox<>();
        cmbHoraInicio.setPrefWidth(290);
        for (int h = 6; h <= 22; h++) cmbHoraInicio.getItems().add(LocalTime.of(h, 0));
        campoInicio.getChildren().addAll(lblI, cmbHoraInicio);

        // Hora fin
        VBox campoFin = new VBox(8);
        Label lblF2 = new Label("🕐 Hora Fin");
        cmbHoraFin = new ComboBox<>();
        cmbHoraFin.setPrefWidth(290);
        for (int h = 7; h <= 23; h++) cmbHoraFin.getItems().add(LocalTime.of(h, 0));
        campoFin.getChildren().addAll(lblF2, cmbHoraFin);

        // Botones
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);

        btnVerificar = new Button("🔍 Verificar");
        btnVerificar.setStyle("-fx-background-color:#3498db;-fx-text-fill:white; -fx-font-weight:bold;");
        btnVerificar.setOnAction(e -> verificarDisponibilidad());

        btnReservar = new Button("✓ Reservar");
        btnReservar.setStyle("-fx-background-color:#764ba2;-fx-text-fill:white;-fx-font-weight:bold;");
        btnReservar.setDisable(true);
        btnReservar.setOnAction(e -> confirmarReserva());

        botones.getChildren().addAll(btnVerificar, btnReservar);

        // Labels
        lblDisponibilidad = new Label("");
        lblMensaje = new Label("");
        lblMensaje.setMaxWidth(600);

        panelHorarios = new VBox(10);
        panelHorarios.setVisible(false);

        form.getChildren().addAll(
                campoCancha,
                campoFecha,
                campoInicio,
                campoFin,
                botones,
                lblDisponibilidad,
                panelHorarios,
                lblMensaje
        );

        return form;
    }

    private void verificarDisponibilidad() {
        lblMensaje.setText("");
        lblDisponibilidad.setText("");
        panelHorarios.setVisible(false);
        btnReservar.setDisable(true);

        if (cmbCancha.getValue() == null ||
                dpFecha.getValue() == null ||
                cmbHoraInicio.getValue() == null ||
                cmbHoraFin.getValue() == null) {

            lblMensaje.setText("Complete todos los campos.");
            lblMensaje.setTextFill(Color.RED);
            return;
        }

        Cancha cancha = cmbCancha.getValue();
        LocalDate fecha = dpFecha.getValue();
        LocalTime inicio = cmbHoraInicio.getValue();
        LocalTime fin = cmbHoraFin.getValue();

        if (!fin.isAfter(inicio)) {
            lblMensaje.setText("La hora de fin debe ser mayor.");
            lblMensaje.setTextFill(Color.RED);
            return;
        }

        boolean disponible = gestor.verificarDisponibilidad(cancha.getId(), fecha, inicio, fin);

        mostrarHorariosDisponibles(cancha.getId(), fecha);

        if (disponible) {
            lblDisponibilidad.setText("✓ Disponible");
            lblDisponibilidad.setTextFill(Color.GREEN);
            btnReservar.setDisable(false);
        } else {
            lblDisponibilidad.setText("✗ No disponible");
            lblDisponibilidad.setTextFill(Color.RED);
        }
    }

    private void mostrarHorariosDisponibles(int idCancha, LocalDate fecha) {
        panelHorarios.getChildren().clear();

        List<LocalTime> disponibles = gestor.obtenerHorariosDisponibles(idCancha, fecha);

        Label titulo = new Label("⏰ Horarios disponibles:");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        FlowPane fp = new FlowPane();
        fp.setHgap(10);
        fp.setVgap(10);

        for (LocalTime h : disponibles) {
            Label lbl = new Label(h.toString());
            lbl.setStyle("-fx-background-color:#e8f5e9;padding:5px;");
            fp.getChildren().add(lbl);
        }

        panelHorarios.getChildren().addAll(titulo, fp);
        panelHorarios.setVisible(true);
    }

    private void confirmarReserva() {
        Cancha cancha = cmbCancha.getValue();

        Reserva r = new Reserva(
                0,
                dpFecha.getValue(),
                cmbHoraInicio.getValue(),
                cmbHoraFin.getValue(),
                usuario,
                cancha
        );

        boolean ok = gestor.crearReserva(r);

        if (ok) {
            lblMensaje.setText("✓ Reserva creada correctamente.");
            lblMensaje.setTextFill(Color.GREEN);

            btnReservar.setDisable(true);
            btnVerificar.setDisable(true);
        } else {
            lblMensaje.setText("Error creando la reserva.");
            lblMensaje.setTextFill(Color.RED);
        }
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}
