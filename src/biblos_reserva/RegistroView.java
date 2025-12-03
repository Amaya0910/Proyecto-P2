    package biblos_reserva;

import biblos_reserva_datos.GestorCanchas;
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

/**
 * Ventana de registro de nuevos usuarios
 */
public class RegistroView {
    
    private final Stage stage;
    private final GestorCanchas gestor;
    private final LoginView loginView;
    
    private TextField txtNombre;
    private TextField txtCorreo;
    private TextField txtTelefono;
    private PasswordField txtPassword;
    private PasswordField txtConfirmPassword;
    private Label lblMensaje;

    public RegistroView(Stage stage, GestorCanchas gestor, LoginView loginView) {
        this.stage = stage;
        this.gestor = gestor;
        this.loginView = loginView;
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, white 0%, green 100%);");
        
        VBox contenedorCentral = crearFormularioRegistro();
        root.setCenter(contenedorCentral);
        
        Scene scene = new Scene(root, 900, 600);
        return scene;
    }

    private VBox crearFormularioRegistro() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(500);
        
        VBox formPanel = new VBox(15);
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setPadding(new Insets(40));
        formPanel.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );
        
        // Título
        Label lblTitulo = new Label("REGISTRO DE USUARIO");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web("#667eea"));
        
        Label lblSubtitulo = new Label("Complete el formulario para crear su cuenta");
        lblSubtitulo.setFont(Font.font("Arial", 13));
        lblSubtitulo.setTextFill(Color.GRAY);
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        // Campos
        txtNombre = crearCampoTexto("Nombre completo", "Juan Pérez");
        txtCorreo = crearCampoTexto("Correo electrónico", "usuario@email.com");
        txtTelefono = crearCampoTexto("Teléfono de contacto", "3001234567");
        txtPassword = crearCampoPassword("Contraseña", "••••••••");
        txtConfirmPassword = crearCampoPassword("Confirmar contraseña", "••••••••");
        
        // Mensaje
        lblMensaje = new Label("");
        lblMensaje.setFont(Font.font("Arial", 12));
        lblMensaje.setWrapText(true);
        lblMensaje.setMaxWidth(400);
        lblMensaje.setAlignment(Pos.CENTER);
        
        // Botones
        HBox botones = crearBotones();
        
        formPanel.getChildren().addAll(
            lblTitulo,
            lblSubtitulo,
            separator,
            txtNombre,
            txtCorreo,
            txtTelefono,
            txtPassword,
            txtConfirmPassword,
            lblMensaje,
            botones
        );
        
        container.getChildren().add(formPanel);
        return container;
    }

    private TextField crearCampoTexto(String etiqueta, String placeholder) {
        VBox container = new VBox(5);
        
        Label lbl = new Label(etiqueta);
        lbl.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        
        TextField txt = new TextField();
        txt.setPromptText(placeholder);
        txt.setPrefHeight(40);
        txt.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;" +
            "-fx-font-size: 14;"
        );
        
        txt.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txt.setStyle(
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 2;" +
                    "-fx-font-size: 14;"
                );
            } else {
                txt.setStyle(
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #ddd;" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 1;" +
                    "-fx-font-size: 14;"
                );
            }
        });
        
        return txt;
    }

    private PasswordField crearCampoPassword(String etiqueta, String placeholder) {
        VBox container = new VBox(5);
        
        Label lbl = new Label(etiqueta);
        lbl.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        
        PasswordField txt = new PasswordField();
        txt.setPromptText(placeholder);
        txt.setPrefHeight(40);
        txt.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;" +
            "-fx-font-size: 14;"
        );
        
        txt.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txt.setStyle(
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 2;" +
                    "-fx-font-size: 14;"
                );
            } else {
                txt.setStyle(
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #ddd;" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 1;" +
                    "-fx-font-size: 14;"
                );
            }
        });
        
        return txt;
    }

    private HBox crearBotones() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 0, 0, 0));
        
        Button btnRegistrar = new Button("REGISTRARSE");
        btnRegistrar.setPrefSize(150, 45);
        btnRegistrar.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnRegistrar.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnRegistrar.setOnAction(e -> manejarRegistro());
        
        Button btnVolver = new Button("VOLVER");
        btnVolver.setPrefSize(150, 45);
        btnVolver.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        btnVolver.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #667eea;" +
            "-fx-border-color: #667eea;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-cursor: hand;"
        );
        btnVolver.setOnAction(e -> volverAlLogin());
        
        container.getChildren().addAll(btnRegistrar, btnVolver);
        return container;
    }

    private void manejarRegistro() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        
        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }
        
        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarError("El correo no es válido");
            return;
        }
        
        if (telefono.length() < 10) {
            mostrarError("El teléfono debe tener al menos 10 dígitos");
            return;
        }
        
        if (password.length() < 5) {
            mostrarError("La contraseña debe tener al menos 5 caracteres");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden");
            return;
        }
        
        // Crear usuario
        Usuario nuevoUsuario = new Usuario(0, telefono, nombre, correo, password);
        
        if (gestor.registrarUsuario(nuevoUsuario)) {
            mostrarExito("¡Registro exitoso! Redirigiendo al menú...");
            
            // Esperar 1.5 segundos y abrir el menú del usuario
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        MenuUsuarioView menuView = new MenuUsuarioView(stage, gestor, nuevoUsuario);
                        stage.setScene(menuView.crearEscena());
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else {
            mostrarError("El correo ya está registrado");
        }
    }

    private void volverAlLogin() {
        stage.setScene(loginView.crearEscena());
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText("❌ " + mensaje);
        lblMensaje.setTextFill(Color.web("#e74c3c"));
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText("✓ " + mensaje);
        lblMensaje.setTextFill(Color.web("#27ae60"));
    }
}