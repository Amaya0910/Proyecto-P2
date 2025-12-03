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
import java.util.Optional;

public class GestionCanchasView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final Administracion admin;
    private final MenuAdminView menuAnterior;
    private VBox contenedorCanchas;

    public GestionCanchasView(Stage stage, GestorCanchas gestor, Administracion admin, MenuAdminView menuAnterior) {
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
        
        Label lblTitulo = new Label("🏟️ Gestión de Canchas");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnVolver = new Button("← Volver al Panel");
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
        
        Label lblTitulo = new Label("Administrar Canchas del Sistema");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#2c3e50"));
        
        Label lblSubtitulo = new Label("Agregar, modificar o eliminar canchas");
        lblSubtitulo.setFont(Font.font("Arial", 14));
        lblSubtitulo.setTextFill(Color.GRAY);
        
        Button btnNuevaCancha = new Button("➕ Agregar Nueva Cancha");
        btnNuevaCancha.setPrefSize(250, 50);
        btnNuevaCancha.setStyle(
            "-fx-background-color: linear-gradient(to right, #27ae60, #229954);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnNuevaCancha.setOnAction(e -> mostrarDialogoAgregarCancha());
        
        contenedorCanchas = new VBox(15);
        contenedorCanchas.setAlignment(Pos.TOP_CENTER);
        contenedorCanchas.setMaxWidth(900);
        
        cargarCanchas();
        
        container.getChildren().addAll(lblTitulo, lblSubtitulo, btnNuevaCancha, contenedorCanchas);
        return container;
    }

    private void cargarCanchas() {
        contenedorCanchas.getChildren().clear();
        
        List<Cancha> canchas = gestor.mostrarTodasLasCanchas();
        
        if (canchas.isEmpty()) {
            mostrarSinCanchas();
        } else {
            HBox estadisticas = crearEstadisticas(canchas);
            contenedorCanchas.getChildren().add(estadisticas);
            
            Label lblTotal = new Label("📋 Total de Canchas: " + canchas.size());
            lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            lblTotal.setTextFill(Color.web("#2c3e50"));
            lblTotal.setPadding(new Insets(20, 0, 10, 0));
            contenedorCanchas.getChildren().add(lblTotal);
            
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
                if (columna > 2) {
                    columna = 0;
                    fila++;
                }
            }
            
            contenedorCanchas.getChildren().add(grid);
        }
    }

    private HBox crearEstadisticas(List<Cancha> canchas) {
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(20));
        
        int total = canchas.size();
        long disponibles = canchas.stream()
            .filter(c -> c.getEstado().equalsIgnoreCase("Disponible"))
            .count();
        long ocupadas = canchas.stream()
            .filter(c -> c.getEstado().equalsIgnoreCase("Ocupada"))
            .count();
        long mantenimiento = canchas.stream()
            .filter(c -> c.getEstado().equalsIgnoreCase("Mantenimiento"))
            .count();
        
        stats.getChildren().addAll(
            crearTarjetaEstadistica("🏟️", String.valueOf(total), "Total", "#3498db"),
            crearTarjetaEstadistica("✅", String.valueOf(disponibles), "Disponibles", "#27ae60"),
            crearTarjetaEstadistica("🔴", String.valueOf(ocupadas), "Ocupadas", "#e74c3c"),
            crearTarjetaEstadistica("🔧", String.valueOf(mantenimiento), "Mantenimiento", "#f39c12")
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

    private VBox crearTarjetaCancha(Cancha cancha) {
        VBox tarjeta = new VBox(12);
        tarjeta.setAlignment(Pos.TOP_LEFT);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setPrefSize(280, 240);
        tarjeta.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        HBox encabezado = new HBox(10);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        
        Label lblIcono = new Label("⚽");
        lblIcono.setFont(Font.font(30));
        
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
        
        Label lblNombre = new Label(cancha.getNombre());
        lblNombre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblNombre.setTextFill(Color.web("#2c3e50"));
        lblNombre.setWrapText(true);
        
        Label lblId = new Label("ID: #" + cancha.getId());
        lblId.setFont(Font.font("Arial", 12));
        lblId.setTextFill(Color.GRAY);
        
        Separator separator = new Separator();
        
        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER);
        
        Button btnEditar = new Button("✏️ Editar");
        btnEditar.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11;" +
            "-fx-padding: 8 15;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        btnEditar.setOnAction(e -> mostrarDialogoEditarCancha(cancha));
        
        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11;" +
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;"
        );
        btnEliminar.setOnAction(e -> confirmarEliminarCancha(cancha));
        
        botones.getChildren().addAll(btnEditar, btnEliminar);
        
        tarjeta.getChildren().addAll(encabezado, lblNombre, lblId, separator, botones);
        
        // Efecto hover corregido - sin propiedades CSS inválidas
        tarjeta.setOnMouseEntered(e -> {
            tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 15, 0, 0, 5);"
            );
        });
        
        tarjeta.setOnMouseExited(e -> {
            tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
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
        sinCanchas.setPadding(new Insets(60));
        
        Label lblIcono = new Label("🏟️");
        lblIcono.setFont(Font.font(70));
        
        Label lblMensaje = new Label("No hay canchas registradas");
        lblMensaje.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblMensaje.setTextFill(Color.web("#7f8c8d"));
        
        Label lblSugerencia = new Label("Agrega la primera cancha al sistema");
        lblSugerencia.setFont(Font.font("Arial", 15));
        lblSugerencia.setTextFill(Color.GRAY);
        
        sinCanchas.getChildren().addAll(lblIcono, lblMensaje, lblSugerencia);
        contenedorCanchas.getChildren().add(sinCanchas);
    }

    private void mostrarDialogoAgregarCancha() {
        Dialog<Cancha> dialog = new Dialog<>();
        dialog.setTitle("➕ Agregar Nueva Cancha");
        dialog.setHeaderText("Complete la información de la nueva cancha");
        
        ButtonType btnAgregar = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAgregar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la cancha");
        
        ComboBox<String> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll("Disponible", "Ocupada", "Mantenimiento");
        cmbEstado.setValue("Disponible");
        
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Estado:"), 0, 1);
        grid.add(cmbEstado, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAgregar) {
                String nombre = txtNombre.getText().trim();
                String estado = cmbEstado.getValue();
                
                if (!nombre.isEmpty()) {
                    return new Cancha(0, nombre, estado);
                }
            }
            return null;
        });
        
        Optional<Cancha> result = dialog.showAndWait();
        result.ifPresent(cancha -> {
            if (gestor.agregarCancha(cancha)) {
                mostrarAlerta("Éxito", "Cancha agregada correctamente", Alert.AlertType.INFORMATION);
                cargarCanchas();
            } else {
                mostrarAlerta("Error", "No se pudo agregar la cancha", Alert.AlertType.ERROR);
            }
        });
    }

    private void mostrarDialogoEditarCancha(Cancha cancha) {
        Dialog<Cancha> dialog = new Dialog<>();
        dialog.setTitle("✏️ Editar Cancha");
        dialog.setHeaderText("Modificar información de: " + cancha.getNombre());
        
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtNombre = new TextField(cancha.getNombre());
        
        ComboBox<String> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll("Disponible", "Ocupada", "Mantenimiento");
        cmbEstado.setValue(cancha.getEstado());
        
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Estado:"), 0, 1);
        grid.add(cmbEstado, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                String nombre = txtNombre.getText().trim();
                String estado = cmbEstado.getValue();
                
                if (!nombre.isEmpty()) {
                    return new Cancha(cancha.getId(), nombre, estado);
                }
            }
            return null;
        });
        
        Optional<Cancha> result = dialog.showAndWait();
        result.ifPresent(canchaActualizada -> {
            if (gestor.modificarCancha(canchaActualizada)) {
                mostrarAlerta("Éxito", "Cancha actualizada correctamente", Alert.AlertType.INFORMATION);
                cargarCanchas();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la cancha", Alert.AlertType.ERROR);
            }
        });
    }

    private void confirmarEliminarCancha(Cancha cancha) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta cancha?");
        alert.setContentText(
            "Cancha: " + cancha.getNombre() + "\n" +
            "ID: #" + cancha.getId() + "\n\n" +
            "Esta acción no se puede deshacer."
        );
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (gestor.eliminarCancha(cancha.getId())) {
                mostrarAlerta("Éxito", "Cancha eliminada correctamente", Alert.AlertType.INFORMATION);
                cargarCanchas();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la cancha", Alert.AlertType.ERROR);
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void volverAlMenu() {
        stage.setScene(menuAnterior.crearEscena());
    }
}
