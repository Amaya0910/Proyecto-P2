/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Christ-son
 */
package biblos_reserva_datos;

import biblos_reserva_dominio.Usuario;
import java.util.List;

public interface IBDUsuario {
    public void registrar(Usuario usuario);
    public Usuario buscarPorId(int id);
    public Usuario buscarPorCorreo(String correo);
    public Usuario autenticar(String correo, String contrasena);
    public List<Usuario> listarTodos();
    public boolean modificar(Usuario usuario);
    public boolean eliminar(int id);
    public boolean existe(String correo);
    public int size();
}