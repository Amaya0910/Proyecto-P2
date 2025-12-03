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

public class LoginView {

    private final Stage stage;
    private final GestorCanchas gestor;
    private TextField txtCorreo;
    private PasswordField txtPassword;
    private RadioButton rbUsuario;
    private RadioButton rbAdmin;
    private Label lblMensaje;

    public LoginView(Stage stage, GestorCanchas gestor) {
        this.stage = stage;
        this.gestor = gestor;
    }

    public Scene crearEscena() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, white 0%, green 100%);");

        VBox contenedorCentral = crearFormularioLogin();
        root.setCenter(contenedorCentral);

        HBox footer = crearFooter();
        root.setBottom(footer);

        return new Scene(root, 900, 600);
    }

    private VBox crearFormularioLogin() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(450);

        VBox formPanel = new VBox(15);
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setPadding(new Insets(40));
        formPanel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );

        Label lblTitulo = new Label("BIBLOS RESERVA");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        lblTitulo.setTextFill(Color.web("#667eea"));

        Label lblSubtitulo = new Label("Sistema de Reservas de Canchas");
        lblSubtitulo.setFont(Font.font("Arial", 14));
        lblSubtitulo.setTextFill(Color.GRAY);

        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        HBox selectorTipo = crearSelectorTipoUsuario();

        VBox campoCorreo = crearCampoTexto("Correo electrónico", "usuario@email.com");
        txtCorreo = (TextField) campoCorreo.getChildren().get(1);

        VBox campoPassword = crearCampoPassword("Contraseña", "••••••••");
        txtPassword = (PasswordField) campoPassword.getChildren().get(1);

        lblMensaje = new Label("");
        lblMensaje.setFont(Font.font("Arial", 12));
        lblMensaje.setWrapText(true);
        lblMensaje.setMaxWidth(350);
        lblMensaje.setAlignment(Pos.CENTER);

        HBox botonesContainer = crearBotones();

        formPanel.getChildren().addAll(
                lblTitulo,
                lblSubtitulo,
                separator,
                selectorTipo,
                campoCorreo,
                campoPassword,
                lblMensaje,
                botonesContainer
        );

        container.getChildren().add(formPanel);
        return container;
    }

    private HBox crearSelectorTipoUsuario() {
        HBox container = new HBox(30);  // mayor espacio
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(10, 0, 10, 0));

        // Asegurar que no se oculte nada
        container.setMinWidth(380);
        container.setPrefWidth(380);

        Label lblTipo = new Label("Ingresar como:");
        lblTipo.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        lblTipo.setTextFill(Color.BLACK);

        ToggleGroup grupo = new ToggleGroup();

        rbUsuario = new RadioButton("Usuario");
        rbUsuario.setToggleGroup(grupo);
        rbUsuario.setSelected(true);
        rbUsuario.setFont(Font.font("Arial", 13));
        rbUsuario.setTextFill(Color.BLACK);

        rbAdmin = new RadioButton("Administrador");
        rbAdmin.setToggleGroup(grupo);
        rbAdmin.setFont(Font.font("Arial", 13));
        rbAdmin.setTextFill(Color.BLACK);

        container.getChildren().addAll(lblTipo, rbUsuario, rbAdmin);
        return container;
    }

    private VBox crearCampoTexto(String etiqueta, String placeholder) {
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

        container.getChildren().addAll(lbl, txt);
        return container;
    }

    private VBox crearCampoPassword(String etiqueta, String placeholder) {
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

        container.getChildren().addAll(lbl, txt);
        return container;
    }

    private HBox crearBotones() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);

        Button btnIngresar = new Button("INGRESAR");
        btnIngresar.setPrefSize(150, 45);
        btnIngresar.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnIngresar.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;"
        );

        btnIngresar.setOnAction(e -> manejarLogin());

        Button btnRegistrarse = new Button("REGISTRARSE");
        btnRegistrarse.setPrefSize(150, 45);
        btnRegistrarse.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        btnRegistrarse.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #667eea;" +
                "-fx-border-color: #667eea;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-cursor: hand;"
        );

        btnRegistrarse.setOnAction(e -> abrirRegistro());

        container.getChildren().addAll(btnIngresar, btnRegistrarse);
        return container;
    }

    private HBox crearFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));

        Label lblInfo = new Label("💡 Usuario demo: juan@gmail.com / 12345  |  Admin: admin@biblos.com / admin123");
        lblInfo.setTextFill(Color.WHITE);
        lblInfo.setFont(Font.font("Arial", 12));

        footer.getChildren().add(lblInfo);
        return footer;
    }

    private void manejarLogin() {
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos");
            return;
        }

        if (rbUsuario.isSelected()) {
            Usuario usuario = gestor.autenticarUsuario(correo, password);

            if (usuario != null) {
                mostrarExito("¡Bienvenido " + usuario.getNombre() + "!");
                abrirMenuUsuario(usuario);
            } else {
                mostrarError("Credenciales incorrectas");
            }

        } else {
            Administracion admin = gestor.autenticarAdmin(correo, password);

            if (admin != null) {
                mostrarExito("¡Bienvenido Administrador!");
                abrirMenuAdmin(admin);
            } else {
                mostrarError("Credenciales de administrador incorrectas");
            }
        }
    }

    private void abrirRegistro() {
        RegistroView registroView = new RegistroView(stage, gestor, this);
        stage.setScene(registroView.crearEscena());
    }

    private void abrirMenuUsuario(Usuario usuario) {
        MenuUsuarioView menuView = new MenuUsuarioView(stage, gestor, usuario);
        stage.setScene(menuView.crearEscena());
    }

    private void abrirMenuAdmin(Administracion admin) {
        MenuAdminView menuView = new MenuAdminView(stage, gestor, admin);
        stage.setScene(menuView.crearEscena());
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
