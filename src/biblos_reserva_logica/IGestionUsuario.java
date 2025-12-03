/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package biblos_reserva_logica;

import biblos_reserva_dominio.Usuario;
import java.util.List;

/**
 *
 * @author taich
 */
public interface IGestionUsuario {
    boolean registrarUsuario(Usuario usuario);
    Usuario autenticarUsuario(String correo, String contraseña);
    Usuario buscarUsuarioPorId(int idUsuario);
    List<Usuario> listarTodosLosUsuarios();
}
