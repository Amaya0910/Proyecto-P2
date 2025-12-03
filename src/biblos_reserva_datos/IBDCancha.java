/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblos_reserva_datos;

import biblos_reserva_dominio.Cancha;
import java.util.List;

/**
 *
 * @author Christ-son
 */

public interface IBDCancha {

    public void agregar(Cancha cancha);

    public Cancha buscarPorId(int id);

    public Cancha buscarPorNombre(String nombre);

    public List<Cancha> listarTodas();

    public List<Cancha> listarDisponibles();

    public boolean modificar(Cancha cancha);

    public boolean eliminar(int id);

    public boolean existe(int id);

    public int size();
}
