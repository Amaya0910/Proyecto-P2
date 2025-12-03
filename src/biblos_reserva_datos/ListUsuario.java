package biblos_reserva_datos;

import biblos_reserva_dominio.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ListUsuario implements IBDUsuario {
    
    private List<Usuario> usuarios;

    public ListUsuario() {
        this.usuarios = new ArrayList<>();
    }

    @Override
    public void registrar(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    @Override
    public Usuario buscarPorId(int id) {
        for (Usuario u : this.usuarios) {
            if (u.getIdUsuario() == id) return u;
        }
        return null;
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        for (Usuario u : this.usuarios) {
            if (u.getCorreo().equals(correo)) return u;
        }
        return null;
    }

    @Override
    public Usuario autenticar(String correo, String contrasena) {
        for (Usuario u : this.usuarios) {
            if (u.getCorreo().equals(correo) && u.getContraseña().equals(contrasena)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(this.usuarios);
    }

    @Override
    public boolean modificar(Usuario usuario) {
        Usuario existente = buscarPorId(usuario.getIdUsuario());
        if (existente == null) return false;
        int index = this.usuarios.indexOf(existente);
        this.usuarios.set(index, usuario);
        return true;
    }

    @Override
    public boolean eliminar(int id) {
        Usuario u = buscarPorId(id);
        if (u != null) {
            this.usuarios.remove(u);
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
        return this.usuarios.size();
    }
}