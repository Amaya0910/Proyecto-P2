package biblos_reserva_datos;

import biblos_reserva_dominio.Usuario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoUsuario implements IBDUsuario {
    
    private final String rutaArchivo;

    public ArchivoUsuario(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        crearArchivoSiNoExiste();
    }

    private void crearArchivoSiNoExiste() {
        File archivo = new File(rutaArchivo);
    System.out.println("\n=== CREACIÓN DE ARCHIVO ===");
    System.out.println("Ruta especificada: " + rutaArchivo);
    System.out.println("Ruta COMPLETA: " + archivo.getAbsolutePath());
    System.out.println("Directorio actual de trabajo: " + System.getProperty("user.dir"));
    
    try {
        File padre = archivo.getParentFile();
        if (padre != null && !padre.exists()) {
            padre.mkdirs();
            System.out.println("✓ Directorio creado: " + padre.getAbsolutePath());
        }
        if (!archivo.exists()) {
            archivo.createNewFile();
            System.out.println("✓ Archivo creado exitosamente");
        } else {
            System.out.println("✓ El archivo ya existe");
        }
        System.out.println("===========================\n");
    } catch (IOException e) {
        System.err.println("Error al crear archivo: " + e.getMessage());
        e.printStackTrace();
    }
}

    @Override
    public void registrar(Usuario usuario) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            String linea = usuario.getIdUsuario() + ";" +
                          usuario.getContacto() + ";" +
                          usuario.getNombre() + ";" +
                          usuario.getCorreo() + ";" +
                          usuario.getContraseña();
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario buscarPorId(int id) {
        for (Usuario u : listarTodos()) {
            if (u.getIdUsuario() == id) return u;
        }
        return null;
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        for (Usuario u : listarTodos()) {
            if (u.getCorreo().equals(correo)) return u;
        }
        return null;
    }

    @Override
    public Usuario autenticar(String correo, String contrasena) {
        for (Usuario u : listarTodos()) {
            if (u.getCorreo().equals(correo) && u.getContraseña().equals(contrasena)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    Usuario u = parsearUsuario(linea);
                    if (u != null) usuarios.add(u);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    private Usuario parsearUsuario(String linea) {
        try {
            String[] partes = linea.split(";");
            int id = Integer.parseInt(partes[0]);
            String contacto = partes[1];
            String nombre = partes[2];
            String correo = partes[3];
            String contrasena = partes[4];
            // Constructor: (id, contacto, nombre, correo, contraseña)
            return new Usuario(id, contacto, nombre, correo, contrasena);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean modificar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        boolean encontrado = false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getIdUsuario() == usuario.getIdUsuario()) {
                usuarios.set(i, usuario);
                encontrado = true;
                break;
            }
        }
        if (encontrado) reescribirArchivo(usuarios);
        return encontrado;
    }

    @Override
    public boolean eliminar(int id) {
        List<Usuario> usuarios = listarTodos();
        Usuario aEliminar = null;
        for (Usuario u : usuarios) {
            if (u.getIdUsuario() == id) {
                aEliminar = u;
                break;
            }
        }
        if (aEliminar != null) {
            usuarios.remove(aEliminar);
            reescribirArchivo(usuarios);
            return true;
        }
        return false;
    }

    @Override
    public boolean existe(String correo) {
        return buscarPorCorreo(correo) != null;
    }

    @Override
    public int size() {
        return listarTodos().size();
    }

    private void reescribirArchivo(List<Usuario> usuarios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (Usuario u : usuarios) {
                String linea = u.getIdUsuario() + ";" +
                              u.getContacto() + ";" +
                              u.getNombre() + ";" +
                              u.getCorreo() + ";" +
                              u.getContraseña();
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}